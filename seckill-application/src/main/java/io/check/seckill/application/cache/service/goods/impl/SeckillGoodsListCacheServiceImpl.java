package io.check.seckill.application.cache.service.goods.impl;

import com.alibaba.fastjson.JSON;
import io.check.seckill.application.builder.SeckillGoodsBuilder;
import io.check.seckill.application.cache.model.SeckillBusinessCache;
import io.check.seckill.application.cache.service.goods.SeckillGoodsListCacheService;
import io.check.seckill.domain.constants.SeckillConstants;
import io.check.seckill.domain.model.SeckillGoods;
import io.check.seckill.domain.repository.SeckillGoodsRepository;
import io.check.seckill.infrastructure.cache.distribute.DistributedCacheService;
import io.check.seckill.infrastructure.cache.local.LocalCacheService;
import io.check.seckill.infrastructure.lock.DistributedLock;
import io.check.seckill.infrastructure.lock.factoty.DistributedLockFactory;
import io.check.seckill.infrastructure.utils.string.StringUtil;
import io.check.seckill.infrastructure.utils.time.SystemClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class SeckillGoodsListCacheServiceImpl implements SeckillGoodsListCacheService {

    private final static Logger logger = LoggerFactory.getLogger(SeckillGoodsListCacheServiceImpl.class);

    @Resource
    private LocalCacheService<Long, SeckillBusinessCache<List<SeckillGoods>>> localCacheService;

    //更新活动时获取分布式锁使用
    private static final String SECKILL_GOODS_LIST_UPDATE_CACHE_LOCK_KEY = "SECKILL_GOODS_LIST_UPDATE_CACHE_LOCK_KEY_";

    //本地可重入锁
    private final Lock localCacheUpdatelock = new ReentrantLock();

    @Resource
    private DistributedCacheService distributedCacheService;

    @Resource
    private SeckillGoodsRepository seckillGoodsRepository;

    @Resource
    private DistributedLockFactory distributedLockFactory;

    @Override
    public SeckillBusinessCache<List<SeckillGoods>> getCachedGoodsList(Long activityId, Long version) {
        // 获取本地缓存数据
        SeckillBusinessCache<List<SeckillGoods>> seckillGoodsListCache =
                localCacheService.getIfPresent(activityId);
        if(seckillGoodsListCache != null){
            //版本号为空，表示命中本地缓存
            if(version == null){
                logger.info("SeckillGoodsListCache|命中本地缓存|{}", activityId);
                return seckillGoodsListCache;
            }
            //传递的版本号比缓存中的版本号小，则直接返回缓存中的数据
            if(version.compareTo(seckillGoodsListCache.getVersion()) <= 0){
                logger.info("SeckillGoodsListCache|命中本地缓存|{}", activityId);
                return seckillGoodsListCache;
            }
            //传递的版本号大于缓存中色版本号，则更新缓存
            if (version.compareTo(seckillGoodsListCache.getVersion()) > 0){
                return getDistributedCache(activityId);
            }
        }
        return getDistributedCache(activityId);
    }

    /**
     * 获取分布式缓存中的数据
     */
    private SeckillBusinessCache<List<SeckillGoods>> getDistributedCache(Long activityId) {
        logger.info("SeckillGoodsListCache|读取分布式缓存|{}", activityId);
        SeckillBusinessCache<List<SeckillGoods>> seckillGoodsListCache =
                SeckillGoodsBuilder.getSeckillBusinessCacheList(
                        distributedCacheService.getObject(buildCacheKey(activityId)),SeckillGoods.class);
        //分布式缓存中的数据为空
        if(seckillGoodsListCache == null){
            //使用一个线程尝试去更新分布式缓存中的数据
            seckillGoodsListCache = tryUpdateSeckillGoodsCacheByLock(activityId);
        }
        if(seckillGoodsListCache != null && !seckillGoodsListCache.isRetryLater()){
            if (localCacheUpdatelock.tryLock()){
                try {
                    localCacheService.put(activityId, seckillGoodsListCache);
                    logger.info("SeckillGoodsListCache|本地缓存已经更新|{}", activityId);
                }finally {
                    localCacheUpdatelock.unlock();
                }
            }
        }
        return seckillGoodsListCache;
    }

    /**
     * 尝试去更新分布式缓存中的数据
     */
    @Override
    public SeckillBusinessCache<List<SeckillGoods>> tryUpdateSeckillGoodsCacheByLock(Long activityId) {
        logger.info("SeckillGoodsListCache|更新分布式缓存|{}", activityId);
        DistributedLock lock = distributedLockFactory
                .getDistributedLock(SECKILL_GOODS_LIST_UPDATE_CACHE_LOCK_KEY.concat(String.valueOf(activityId)));
        try {
            boolean isSuccess = lock.tryLock(2, 5, TimeUnit.SECONDS);
            if(!isSuccess){
                return new SeckillBusinessCache<List<SeckillGoods>>().retryLater();
            }
            List<SeckillGoods> seckillGoodsList =
                    seckillGoodsRepository.getSeckillGoodsByActivityId(activityId);
            SeckillBusinessCache<List<SeckillGoods>> seckillGoodsListCache;
            if(seckillGoodsList == null){
                seckillGoodsListCache = new SeckillBusinessCache<List<SeckillGoods>>().notExist();
            }else {
                seckillGoodsListCache = new SeckillBusinessCache<List<SeckillGoods>>()
                        .with(seckillGoodsList)
                        .withVersion(SystemClock.millisClock().now());
            }
            distributedCacheService.put(buildCacheKey(activityId), JSON.toJSONString(seckillGoodsListCache),
                    SeckillConstants.FIVE_MINUTES);
            logger.info("SeckillGoodsListCache|分布式缓存已经更新|{}", activityId);
            return seckillGoodsListCache;
        }catch (InterruptedException e) {
            logger.info("SeckillGoodsListCache|更新分布式缓存失败|{}", activityId);
            return new SeckillBusinessCache<List<SeckillGoods>>().retryLater();
        }finally {
            lock.unlock();
        }
    }

    @Override
    public String buildCacheKey(Object key) {
        return StringUtil.append(SeckillConstants.SECKILL_GOODSES_CACHE_KEY, key);
    }
}
