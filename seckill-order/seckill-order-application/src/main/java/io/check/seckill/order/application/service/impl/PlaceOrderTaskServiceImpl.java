package io.check.seckill.order.application.service.impl;

import cn.hutool.core.util.NumberUtil;
import io.check.seckill.common.cache.distribute.DistributedCacheService;
import io.check.seckill.common.cache.local.LocalCacheService;
import io.check.seckill.common.cache.model.SeckillBusinessCache;
import io.check.seckill.common.constants.SeckillConstants;
import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.exception.SeckillException;
import io.check.seckill.common.lock.DistributedLock;
import io.check.seckill.common.lock.factoty.DistributedLockFactory;
import io.check.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import io.check.seckill.dubbo.interfaces.stock.SeckillStockDubboService;
import io.check.seckill.mq.MessageSenderService;
import io.check.seckill.order.application.model.task.SeckillOrderTask;
import io.check.seckill.order.application.service.PlaceOrderTaskService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
@ConditionalOnProperty(name = "submit.order.type", havingValue = "async")
public class PlaceOrderTaskServiceImpl implements PlaceOrderTaskService {
    private final Logger logger = LoggerFactory.getLogger(PlaceOrderTaskServiceImpl.class);

    @Autowired
    private MessageSenderService messageSenderService;

    @Autowired
    private LocalCacheService<Long, Integer> localCacheService;

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Autowired
    private DistributedLockFactory distributedLockFactory;

    @DubboReference(version = "1.0.0", check = false)
    private SeckillStockDubboService seckillStockDubboService;

    @DubboReference(version = "1.0.0", check = false)
    private SeckillGoodsDubboService seckillGoodsDubboService;

    @Value("${submit.order.token.multiple:1.5}")
    private Double multiple;

    @Value("${place.order.type:lua}")
    private String placeOrderType;

    //可重入锁
    private Lock lock = new ReentrantLock();

    @Override
    public boolean submitOrderTask(SeckillOrderTask seckillOrderTask) {
        if (seckillOrderTask == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        String taskKey = SeckillConstants.getKey(SeckillConstants.ORDER_TASK_ID_KEY, seckillOrderTask.getOrderTaskId());
        //检测是否执行过
        Long result = distributedCacheService.checkExecute(taskKey, SeckillConstants.ORDER_TASK_EXPIRE_SECONDS);
        //已经执行过恢复缓存库存的方法
        if (NumberUtil.equals(result, SeckillConstants.CHECK_RECOVER_STOCK_HAS_EXECUTE)){
            throw new SeckillException(ErrorCode.REDUNDANT_SUBMIT);
        }
        //获取可用的下单许可
        Long goodsId = seckillOrderTask.getSeckillOrderCommand().getGoodsId();
        Integer availableOrderTokens = this.getAvailableOrderTokens(goodsId);
        //不存在下单许可
        if (availableOrderTokens == null || availableOrderTokens <= 0){
            throw new SeckillException(ErrorCode.ORDER_TOKENS_NOT_AVAILABLE);
        }
        //未获取到下单许可
        if (!this.takeOrderToken(goodsId)){
            logger.info("submitOrderTask|获取下单许可失败|{},{}", seckillOrderTask.getUserId(), seckillOrderTask.getOrderTaskId());
            throw new SeckillException(ErrorCode.ORDER_TOKENS_NOT_AVAILABLE);
        }
        //发送消息
        boolean sendSuccess = messageSenderService.send(seckillOrderTask);
        if (!sendSuccess){
            logger.info("submitOrderTask|下单任务提交失败|{},{}", seckillOrderTask.getUserId(), seckillOrderTask.getOrderTaskId());
            //恢复下单许可
            this.recoverOrderToken(goodsId);
            //清除是否被执行过的数据
            distributedCacheService.delete(taskKey);
        }
        return sendSuccess;
    }

    /**
     * 获取下单许可
     * 三次循环分别对应三种判断
     * 1.如果返回的结果为SeckillConstants.LUA_RESULT_NOT_EXECUTE常量，则调用本类的refreshLatestAvailableTokens()方法来刷新缓存，并跳过本次循环
     * 2.如果返回的结果为空，则直接返回false。否则，返回是否等于SeckillConstants.LUA_RESULT_EXECUTE_TOKEN_SUCCESS常量的结果
     * 3. 如果循环三次都没得到最终结果的话，会直接返回false
     */
    private boolean takeOrderToken(Long goodsId){
        for (int i = 0; i < 3; i++){
            Long result = distributedCacheService.
                    takeOrderToken(SeckillConstants.getKey(SeckillConstants.ORDER_TASK_AVAILABLE_TOKENS_KEY, String.valueOf(goodsId)));
            if (result == null){
                return false;
            }
            if (result == SeckillConstants.LUA_RESULT_NOT_EXECUTE){
                this.refreshLatestAvailableTokens(goodsId);
                continue;
            }
            return result == SeckillConstants.LUA_RESULT_EXECUTE_TOKEN_SUCCESS;
        }
        return false;
    }

    /**
     * 恢复下单许可
     */
    private boolean recoverOrderToken(Long goodsId){
        Long result = distributedCacheService.
                recoverOrderToken(SeckillConstants.getKey(SeckillConstants.ORDER_TASK_AVAILABLE_TOKENS_KEY, String.valueOf(goodsId)));
        if (result == null){
            return false;
        }
        if (result == SeckillConstants.LUA_RESULT_NOT_EXECUTE){
            this.refreshLatestAvailableTokens(goodsId);
            return true;
        }
        return result == SeckillConstants.LUA_RESULT_EXECUTE_TOKEN_SUCCESS;
    }

    /**
     * 获取可用的下单许可
     */
    private Integer getAvailableOrderTokens(Long goodsId) {
        Integer availableOrderTokens = localCacheService.getIfPresent(goodsId);
        if (availableOrderTokens != null){
            return availableOrderTokens;
        }
        return this.refreshLocalAvailableTokens(goodsId);
    }

    /**
     * 刷新本地缓存可用的下单许可，注意DoubleCheck
     */
    private Integer refreshLocalAvailableTokens(Long goodsId) {
        Integer availableOrderTokens = localCacheService.getIfPresent(goodsId);
        if (availableOrderTokens != null){
            return availableOrderTokens;
        }
        String availableTokensKey = SeckillConstants.getKey(SeckillConstants.ORDER_TASK_AVAILABLE_TOKENS_KEY, String.valueOf(goodsId));
        Integer latestAvailableOrderTokens = distributedCacheService.getObject(availableTokensKey, Integer.class);
        if (latestAvailableOrderTokens != null){
            if (lock.tryLock()){
                try{
                    localCacheService.put(goodsId, latestAvailableOrderTokens);
                }finally {
                    lock.unlock();
                }
            }
            return latestAvailableOrderTokens;
        }
        return this.refreshLatestAvailableTokens(goodsId);
    }

    /**
     * 刷新分布式缓存的下单许可，double check
     */
    private Integer refreshLatestAvailableTokens(Long goodsId) {
        String lockKey = SeckillConstants.getKey(SeckillConstants.LOCK_REFRESH_LATEST_AVAILABLE_TOKENS_KEY, String.valueOf(goodsId));
        DistributedLock distributedLock = distributedLockFactory.getDistributedLock(lockKey);
        try{
            boolean isLock = distributedLock.tryLock();
            if (!isLock){
                return null;
            }
            //本地缓存已经存在数据
            Integer availableOrderTokens = localCacheService.getIfPresent(goodsId);
            if (availableOrderTokens != null){
                return availableOrderTokens;
            }
            //获取分布式缓存数据
            String availableTokensKey = SeckillConstants.getKey(SeckillConstants.ORDER_TASK_AVAILABLE_TOKENS_KEY, String.valueOf(goodsId));
            Integer latestAvailableOrderTokens = distributedCacheService.getObject(availableTokensKey, Integer.class);
            //分布式缓存中存在数据，设置到本地缓存，并且返回数据
            if (latestAvailableOrderTokens != null){
                localCacheService.put(goodsId, latestAvailableOrderTokens);
                return latestAvailableOrderTokens;
            }
            //本地缓存和分布式缓存都没有数据，获取商品的库存数据
            SeckillBusinessCache<Integer> availableStockCache = null;
            //分桶模式
            if (SeckillConstants.PLACE_ORDER_TYPE_BUCKET.equals(placeOrderType)){
                availableStockCache = seckillStockDubboService.getAvailableStock(goodsId, 1L);
            }else{
                availableStockCache = seckillGoodsDubboService.getAvailableStock(goodsId, 1L);
            }
            if (availableStockCache == null || !availableStockCache.isExist() || availableStockCache.isRetryLater() || availableStockCache.getData() == null){
                return null;
            }
            Integer availableStock = availableStockCache.getData();
            //根据配置的比例计算下单许可
            latestAvailableOrderTokens = (int) Math.ceil(availableStock * multiple);
            distributedCacheService.put(availableTokensKey, latestAvailableOrderTokens,
                    SeckillConstants.ORDER_TASK_EXPIRE_SECONDS, TimeUnit.SECONDS);
            localCacheService.put(goodsId, latestAvailableOrderTokens);
            return latestAvailableOrderTokens;
        }catch (Exception e){
            logger.error("refreshLatestAvailableTokens|刷新下单许可失败:{}", goodsId, e);
        }finally {
            distributedLock.unlock();
        }
        return null;
    }
}
