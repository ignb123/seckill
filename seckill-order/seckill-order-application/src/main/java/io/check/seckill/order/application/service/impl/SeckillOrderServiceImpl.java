package io.check.seckill.order.application.service.impl;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import io.check.seckill.common.cache.distribute.DistributedCacheService;
import io.check.seckill.common.constants.SeckillConstants;
import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.exception.SeckillException;
import io.check.seckill.common.model.message.ErrorMessage;
import io.check.seckill.order.application.model.command.SeckillOrderCommand;
import io.check.seckill.order.application.place.SeckillPlaceOrderService;
import io.check.seckill.order.application.security.SecurityService;
import io.check.seckill.order.application.service.SeckillOrderService;
import io.check.seckill.order.domain.model.entity.SeckillOrder;
import io.check.seckill.order.domain.service.SeckillOrderDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

/**
 * @author check
 * @version 1.0.0
 * @description 订单业务
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    private final Logger logger = LoggerFactory.getLogger(SeckillOrderServiceImpl.class);

    @Autowired
    private SeckillOrderDomainService seckillOrderDomainService;
    @Autowired
    private SeckillPlaceOrderService seckillPlaceOrderService;
    @Autowired
    private SecurityService securityService;

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Value("${place.order.type:bucket}")
    private String placeOrderType;

    @Override
    public List<SeckillOrder> getSeckillOrderByUserId(Long userId) {
        return seckillOrderDomainService.getSeckillOrderByUserId(userId);
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByGoodsId(Long goodsId) {
        return seckillOrderDomainService.getSeckillOrderByGoodsId(goodsId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteOrder(ErrorMessage errorMessage) {
        //成功提交过事务，才能清理订单，增加缓存库存
        Boolean submitTransaction = distributedCacheService
                .hasKey(SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY, String.valueOf(errorMessage.getTxNo())));
        if (BooleanUtil.isFalse(submitTransaction)){
            logger.info("deleteOrder|订单微服务未执行本地事务|{}",errorMessage.getTxNo());
            return;
        }
        seckillOrderDomainService.deleteOrderShardingUserId(errorMessage.getTxNo(), errorMessage.getUserId());
        seckillOrderDomainService.deleteOrderShardingGoodsId(errorMessage.getTxNo(), errorMessage.getGoodsId());
        //清除掉幂等数据
        if (!StrUtil.isEmpty(errorMessage.getOrderTaskId())){
            distributedCacheService.delete(SeckillConstants.getKey(SeckillConstants.ORDER_TASK_ORDER_ID_KEY, errorMessage.getOrderTaskId()));
            distributedCacheService.delete(SeckillConstants.getKey(SeckillConstants.ORDER_TASK_ID_KEY, errorMessage.getOrderTaskId()));
        }
        this.handlerCacheStock(errorMessage);
    }

    /**
     * 处理缓存库存
     */
    private void handlerCacheStock(ErrorMessage errorMessage) {
        //订单微服务之前未抛出异常，说明已经扣减了缓存中的库存，此时需要将缓存中的库存增加回来
        if (BooleanUtil.isFalse(errorMessage.getException())) {
            String luaKey = SeckillConstants.getKey(SeckillConstants.ORDER_TX_KEY,
                    String.valueOf(errorMessage.getTxNo())).concat(SeckillConstants.LUA_SUFFIX);
            Long result = distributedCacheService.checkExecute(luaKey, SeckillConstants.TX_LOG_EXPIRE_SECONDS);
            //已经执行过恢复缓存库存的方法
            if (NumberUtil.equals(result, SeckillConstants.CHECK_RECOVER_STOCK_HAS_EXECUTE)){
                logger.info("handlerCacheStock|已经执行过恢复缓存库存的方法|{}", JSONObject.toJSONString(errorMessage));
                return;
            }
            //库存分桶
            if (SeckillConstants.PLACE_ORDER_TYPE_BUCKET.equals(placeOrderType)){
                this.rollbackBucketStock(errorMessage);
            }else{
                this.rollbackLockOrLuaStock(errorMessage);
            }
        }
    }

    /**
     * 回滚bucket模式的库存
     */
    private void rollbackBucketStock(ErrorMessage errorMessage) {
        //获取库存分桶数据key
        String stockBucketKey = this.getStockBucketKey(errorMessage.getGoodsId(), errorMessage.getBucketSerialNo());
        //获取库存编排时加锁的Key
        String stockBucketSuspendKey = this.getStockBucketSuspendKey(errorMessage.getGoodsId());
        //获取库存校对key
        String stockBucketAlignKey = this.getStockBucketAlignKey(errorMessage.getGoodsId());
        //封装执行Lua脚本的Key
        List<String> keys = Arrays.asList(stockBucketKey, stockBucketSuspendKey, stockBucketAlignKey);
        Long incrementResult = distributedCacheService.incrementBucketStock(keys, errorMessage.getQuantity());
        if (incrementResult != SeckillConstants.LUA_BUCKET_STOCK_EXECUTE_SUCCESS){
            logger.error("rollbackBucketStock|恢复预扣减的库存失败|{}", JSONUtil.toJsonStr(errorMessage));
        }
    }

    /**
     * 获取库存校对key
     */
    private String getStockBucketAlignKey(Long goodsId){
        return SeckillConstants.getKey(SeckillConstants.GOODS_STOCK_BUCKETS_ALIGN_KEY, String.valueOf(goodsId));
    }

    /**
     * 获取库存编排时加锁的Key
     */
    private String getStockBucketSuspendKey(Long goodsId){
        return SeckillConstants.getKey(SeckillConstants.GOODS_STOCK_BUCKETS_SUSPEND_KEY, String.valueOf(goodsId));
    }

    /**
     * 获取库存分桶数据Key
     */
    private String getStockBucketKey(Long goodsId, Integer serialNo){
        return SeckillConstants.getKey(SeckillConstants.getKey(SeckillConstants.GOODS_BUCKET_AVAILABLE_STOCKS_KEY, String.valueOf(goodsId)),String.valueOf(serialNo));
    }

    /**
     * 回滚Lock和Lua模式的库存
     */
    private void rollbackLockOrLuaStock(ErrorMessage errorMessage) {
        //只有分布式锁方式和Lua脚本方法才会扣减缓存中的库存
        String key = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX, String.valueOf(errorMessage.getGoodsId()));
        //分布式锁方式
        logger.info("handlerCacheStock|回滚缓存库存|{}", JSONObject.toJSONString(errorMessage));
        if (SeckillConstants.PLACE_ORDER_TYPE_LOCK.equalsIgnoreCase(errorMessage.getPlaceOrderType())){
            distributedCacheService.increment(key, errorMessage.getQuantity());
        }else if (SeckillConstants.PLACE_ORDER_TYPE_LUA.equalsIgnoreCase(errorMessage.getPlaceOrderType())){  // Lua方式
            distributedCacheService.incrementByLua(key, errorMessage.getQuantity());
        }

    }
}
