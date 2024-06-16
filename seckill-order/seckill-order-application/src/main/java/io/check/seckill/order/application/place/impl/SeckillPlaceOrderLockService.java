package io.check.seckill.order.application.place.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
import com.alibaba.fastjson.JSONObject;
import io.check.seckill.common.cache.distribute.DistributedCacheService;
import io.check.seckill.common.constants.SeckillConstants;
import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.exception.SeckillException;
import io.check.seckill.common.lock.DistributedLock;
import io.check.seckill.common.lock.factoty.DistributedLockFactory;
import io.check.seckill.common.model.dto.SeckillGoodsDTO;
import io.check.seckill.common.model.message.TxMessage;
import io.check.seckill.common.utils.id.SnowFlakeFactory;
import io.check.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import io.check.seckill.order.application.command.SeckillOrderCommand;
import io.check.seckill.order.application.place.SeckillPlaceOrderService;
import io.check.seckill.order.domain.model.entity.SeckillOrder;
import io.check.seckill.order.domain.service.SeckillOrderDomainService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.TimeUnit;

/**
 * @author check
 * @version 1.0.0
 * @description 分布式锁下单
 */
@Service
@ConditionalOnProperty(name = "place.order.type", havingValue = "lock")
public class SeckillPlaceOrderLockService implements SeckillPlaceOrderService {
    private final Logger logger = LoggerFactory.getLogger(SeckillPlaceOrderLockService.class);
    @DubboReference(version = "1.0.0", check = false)
    private SeckillGoodsDubboService seckillGoodsDubboService;
    @Autowired
    private SeckillOrderDomainService seckillOrderDomainService;
    @Autowired
    private DistributedLockFactory distributedLockFactory;
    @Autowired
    private DistributedCacheService distributedCacheService;
    @Autowired
    private RocketMQTemplate rocketMQTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        //获取商品
        SeckillGoodsDTO seckillGoods = seckillGoodsDubboService.getSeckillGoods(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
        //检测商品信息
        this.checkSeckillGoods(seckillOrderCommand, seckillGoods);
        String lockKey = SeckillConstants.getKey(SeckillConstants.ORDER_LOCK_KEY_PREFIX, String.valueOf(seckillOrderCommand.getGoodsId()));
        DistributedLock lock = distributedLockFactory.getDistributedLock(lockKey);
        // 获取内存中的库存信息
        String key = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX, String.valueOf(seckillOrderCommand.getGoodsId()));
        //是否扣减了缓存中的库存
        boolean isDecrementCacheStock = false;
        boolean exception = false;
        long txNo = SnowFlakeFactory.getSnowFlakeFromCache().nextId();
        try {
            //未获取到分布式锁
            if (!lock.tryLock(2, 5, TimeUnit.SECONDS)){
                throw new SeckillException(ErrorCode.RETRY_LATER);
            }
            // 查询库存信息
            Integer stock = distributedCacheService.getObject(key, Integer.class);
            //库存不足
            if (stock < seckillOrderCommand.getQuantity()){
                throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
            }
            //扣减缓存库存
            Long result = distributedCacheService.decrement(key, seckillOrderCommand.getQuantity());
            if (result < 0){
                throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
            }
            //正常执行了扣减缓存中库存的操作
            isDecrementCacheStock = true;

        } catch (Exception e) {
            //已经扣减了缓存中的库存，则需要增加回来
            if (isDecrementCacheStock){
                distributedCacheService.increment(key, seckillOrderCommand.getQuantity());
            }
            if (e instanceof InterruptedException){
                logger.error("SeckillPlaceOrderLockService|下单分布式锁被中断|参数:{}|异常信息:{}", JSONObject.toJSONString(seckillOrderCommand), e.getMessage());
            }else{
                logger.error("SeckillPlaceOrderLockService|分布式锁下单失败|参数:{}|异常信息:{}", JSONObject.toJSONString(seckillOrderCommand), e.getMessage());
            }
            exception = true;
        }finally {
            lock.unlock();
        }
        //事务消息
        Message<String> message = this.getTxMessage(txNo, userId,  SeckillConstants.PLACE_ORDER_TYPE_LOCK, exception, seckillOrderCommand, seckillGoods);
        //发送事务消息
        rocketMQTemplate.sendMessageInTransaction(SeckillConstants.TOPIC_TX_MSG, message, null);
        return txNo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrderInTransaction(TxMessage txMessage) {
        try{
            Boolean submitTransaction = distributedCacheService.hasKey(SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY, String.valueOf(txMessage.getTxNo())));
            if (BooleanUtil.isTrue(submitTransaction)){
                logger.info("saveOrderInTransaction|已经执行过本地事务|{}", txMessage.getTxNo());
                return;
            }
            //构建订单
            SeckillOrder seckillOrder = this.buildSeckillOrder(txMessage);
            //保存订单
            seckillOrderDomainService.saveSeckillOrder(seckillOrder);
            //保存事务日志
            distributedCacheService.put(SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY, String.valueOf(txMessage.getTxNo())),
                    txMessage.getTxNo(), SeckillConstants.TX_LOG_EXPIRE_DAY, TimeUnit.DAYS);
        }catch (Exception e){
            logger.error("saveOrderInTransaction|异常|{}", e.getMessage());
            distributedCacheService.delete(SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY, String.valueOf(txMessage.getTxNo())));
            this.rollbackCacheStack(txMessage);
            throw e;
        }
    }

    /**
     * 回滚缓存库存
     */
    private void rollbackCacheStack(TxMessage txMessage) {
        //扣减过缓存库存
        if (BooleanUtil.isFalse(txMessage.getException())){
            String luaKey = SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY, String.valueOf(txMessage.getTxNo())).concat(SeckillConstants.LUA_SUFFIX);
            Long result = distributedCacheService.checkRecoverStockByLua(luaKey, SeckillConstants.TX_LOG_EXPIRE_SECONDS);
            //已经执行过恢复缓存库存的方法
            if (NumberUtil.equals(result, SeckillConstants.CHECK_RECOVER_STOCK_HAS_EXECUTE)){
                logger.info("handlerCacheStock|已经执行过恢复缓存库存的方法|{}", JSONObject.toJSONString(txMessage));
                return;
            }
            //只有分布式锁方式和Lua脚本方法才会扣减缓存中的库存
            String key = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX, String.valueOf(txMessage.getGoodsId()));
            distributedCacheService.increment(key, txMessage.getQuantity());
        }
    }

}
