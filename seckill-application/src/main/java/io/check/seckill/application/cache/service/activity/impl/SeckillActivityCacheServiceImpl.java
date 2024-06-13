package io.check.seckill.application.cache.service.activity.impl;

import com.alibaba.fastjson.JSON;
import io.check.seckill.application.builder.SeckillActivityBuilder;
import io.check.seckill.application.cache.model.SeckillBusinessCache;
import io.check.seckill.application.cache.service.activity.SeckillActivityCacheService;
import io.check.seckill.domain.constants.SeckillConstants;
import io.check.seckill.domain.model.SeckillActivity;
import io.check.seckill.domain.repository.SeckillActivityRepository;
import io.check.seckill.infrastructure.cache.distribute.DistributedCacheService;
import io.check.seckill.infrastructure.cache.local.LocalCacheService;
import io.check.seckill.infrastructure.lock.DistributedLock;
import io.check.seckill.infrastructure.lock.factoty.DistributedLockFactory;
import io.check.seckill.infrastructure.utils.string.StringUtil;
import io.check.seckill.infrastructure.utils.time.SystemClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class SeckillActivityCacheServiceImpl implements SeckillActivityCacheService {

    private final static Logger logger = LoggerFactory.getLogger(SeckillActivityCacheServiceImpl.class);

    @Autowired
    private LocalCacheService<Long, SeckillBusinessCache<SeckillActivity>> localCacheService;

    //更新活动时获取分布式锁使用
    private static final String SECKILL_ACTIVITY_UPDATE_CACHE_LOCK_KEY = "SECKILL_ACTIVITY_UPDATE_CACHE_LOCK_KEY_";

    //本地可重入锁
    private final Lock localCacheUpdatelock = new ReentrantLock();

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Autowired
    private SeckillActivityRepository seckillActivityRepository;

    @Autowired
    private DistributedLockFactory distributedLockFactory;

    @Override
    public SeckillBusinessCache<SeckillActivity> getCachedSeckillActivity(Long activityId, Long version) {
        //先从本地缓存中获取数据
        SeckillBusinessCache<SeckillActivity> seckillActivityCache = localCacheService.getIfPresent(activityId);
        if (seckillActivityCache != null){
            //传递的版本号为空，则直接返回本地缓存中的数据
            if(seckillActivityCache.getVersion() == null){
                logger.info("SeckillActivityCache|命中本地缓存|{}", activityId);
                return seckillActivityCache;
            }
            //传递的版本号小于等于缓存中的版本号，则说明缓存中的数据比客户端的数据新，直接返回本地缓存中的数据
            if (version.compareTo(seckillActivityCache.getVersion()) <= 0){
                logger.info("SeckillActivityCache|命中本地缓存|{}", activityId);
                return seckillActivityCache;
            }
            //传递的版本号大于缓存中的版本号，说明缓存中的数据比较落后，从分布式缓存获取数据并更新到本地缓存
            if (version.compareTo(seckillActivityCache.getVersion()) > 0){
                return getDistributedCache(activityId);
            }
        }
        //从分布式缓存中获取数据，并设置到本地缓存中
        return getDistributedCache(activityId);
    }

    /**
     * 从分布式缓存中获取数据
     */
    private SeckillBusinessCache<SeckillActivity> getDistributedCache(Long activityId) {
        logger.info("SeckillActivityCache|读取分布式缓存|{}", activityId);
        //从分布式缓存中获取数据
        SeckillBusinessCache<SeckillActivity> seckillActivityCache = SeckillActivityBuilder
                .getSeckillBusinessCache(distributedCacheService.getObject(buildCacheKey(activityId)), SeckillActivity.class);
        //分布式缓存中没有数据
        if (seckillActivityCache == null){
            // 尝试更新分布式缓存中的数据，注意的是只用一个线程去更新分布式缓存中的数据
            seckillActivityCache = tryUpdateSeckillActivityCacheByLock(activityId);
        }
        //获取的数据不为空，并且不需要重试
        if( seckillActivityCache != null && !seckillActivityCache.isRetryLater()){
            //获取本地锁，更新本地缓存
            if(localCacheUpdatelock.tryLock()){
                try {
                    localCacheService.put(activityId, seckillActivityCache);
                    logger.info("SeckillActivityCache|本地缓存已经更新|{}", activityId);
                }finally {
                    localCacheUpdatelock.unlock();
                }
            }
        }
        return seckillActivityCache;
    }

    /**
     * 利用分布式锁保证只有一个线程去更新分布式缓存中的数据
     */
    @Override
    public SeckillBusinessCache<SeckillActivity> tryUpdateSeckillActivityCacheByLock(Long activityId) {
        logger.info("SeckillActivityCache|更新分布式缓存|{}", activityId);
        //获取分布式锁
        DistributedLock lock = distributedLockFactory
                .getDistributedLock(SECKILL_ACTIVITY_UPDATE_CACHE_LOCK_KEY.concat(String.valueOf(activityId)));
        try {
            boolean isLockSuccess = lock.tryLock(1, 5, TimeUnit.SECONDS);
            //未获取到分布式锁的线程快速返回，不占用系统资源
            if(!isLockSuccess){
                return new SeckillBusinessCache<SeckillActivity>().retryLater();
            }
            SeckillActivity seckillActivity = seckillActivityRepository.getSeckillActivityById(activityId);
            SeckillBusinessCache<SeckillActivity> seckillActivityCache;
            if (seckillActivity == null) {
                seckillActivityCache = new SeckillBusinessCache<SeckillActivity>().notExist();
            }else {
                seckillActivityCache = new SeckillBusinessCache<SeckillActivity>().with(seckillActivity)
                        .withVersion(SystemClock.millisClock().now());
            }
            //将数据保存到分布式缓存
            distributedCacheService.put(buildCacheKey(activityId), JSON.toJSONString(seckillActivityCache),
                    SeckillConstants.FIVE_MINUTES);
            logger.info("SeckillActivityCache|分布式缓存已经更新|{}", activityId);
            return seckillActivityCache;
        }catch (InterruptedException e) {
            logger.error("SeckillActivityCache|更新分布式缓存失败|{}", activityId);
            return new SeckillBusinessCache<SeckillActivity>().retryLater();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String buildCacheKey(Object key) {
        return StringUtil.append(SeckillConstants.SECKILL_ACTIVITY_CACHE_KEY, key);
    }
}
