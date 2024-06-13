package io.check.seckill.application.cache.service.activity.impl;

import com.alibaba.fastjson.JSON;
import io.check.seckill.application.builder.SeckillActivityBuilder;
import io.check.seckill.application.cache.model.SeckillBusinessCache;
import io.check.seckill.application.cache.service.activity.SeckillActivityListCacheService;
import io.check.seckill.domain.constants.SeckillConstants;
import io.check.seckill.domain.model.entity.SeckillActivity;
import io.check.seckill.domain.repository.SeckillActivityRepository;
import io.check.seckill.infrastructure.cache.distribute.DistributedCacheService;
import io.check.seckill.infrastructure.cache.local.LocalCacheService;
import io.check.seckill.infrastructure.lock.DistributedLock;
import io.check.seckill.infrastructure.lock.factoty.DistributedLockFactory;
import io.check.seckill.infrastructure.utils.time.SystemClock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import io.check.seckill.infrastructure.utils.string.StringUtil;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Service
public class SeckillActivityListCacheServiceImpl implements SeckillActivityListCacheService {

    private final static Logger logger = LoggerFactory.getLogger(SeckillActivityListCacheServiceImpl.class);

    @Autowired
    private LocalCacheService<Long, SeckillBusinessCache<List<SeckillActivity>>> localCacheService;

    //分布式锁的key
    private static final String SECKILL_ACTIVITES_UPDATE_CACHE_LOCK_KEY = "SECKILL_ACTIVITIES_UPDATE_CACHE_LOCK_KEY_";

    //本地锁
    private final Lock localCacheUpdatelock = new ReentrantLock();

    @Autowired
    private DistributedCacheService distributedCacheService;

    @Autowired
    private SeckillActivityRepository seckillActivityRepository;

    @Autowired
    private DistributedLockFactory distributedLockFactory;

    @Override
    public SeckillBusinessCache<List<SeckillActivity>> getCachedActivities(Integer status, Long version) {

        //获取本地缓存
        SeckillBusinessCache<List<SeckillActivity>> seckillActivitiyListCache =
                localCacheService.getIfPresent(status.longValue());
        if (seckillActivitiyListCache != null){
            if (version == null){
                logger.info("SeckillActivitesCache|命中本地缓存|{}", status);
                return seckillActivitiyListCache;
            }
            //传递过来的版本小于或等于缓存中的版本号
            if (version.compareTo(seckillActivitiyListCache.getVersion()) <= 0){
                logger.info("SeckillActivitesCache|命中本地缓存|{}", status);
                return seckillActivitiyListCache;
            }
            if (version.compareTo(seckillActivitiyListCache.getVersion()) > 0){
                return getDistributedCache(status);
            }
        }
        return getDistributedCache(status);
    }

    /**
     * 获取分布式缓存中的数据
     */
    private SeckillBusinessCache<List<SeckillActivity>> getDistributedCache(Integer status){
        logger.info("SeckillActivitesCache|读取分布式缓存|{}", status);
        SeckillBusinessCache<List<SeckillActivity>> seckillActivitiyListCache = SeckillActivityBuilder
                .getSeckillBusinessCacheList(distributedCacheService.getObject(buildCacheKey(status)),  SeckillActivity.class);
        if (seckillActivitiyListCache == null){
            seckillActivitiyListCache = tryUpdateSeckillActivityCacheByLock(status);
        }
        if (seckillActivitiyListCache != null && !seckillActivitiyListCache.isRetryLater()){
            if (localCacheUpdatelock.tryLock()){
                try {
                    localCacheService.put(status.longValue(), seckillActivitiyListCache);
                    logger.info("SeckillActivitesCache|本地缓存已经更新|{}", status);
                }finally {
                    localCacheUpdatelock.unlock();
                }
            }
        }
        return seckillActivitiyListCache;
    }

    /**
     * 根据状态更新分布式缓存数据
     */
    @Override
    public SeckillBusinessCache<List<SeckillActivity>> tryUpdateSeckillActivityCacheByLock(Integer status) {
        logger.info("SeckillActivitesCache|更新分布式缓存|{}", status);
        DistributedLock lock = distributedLockFactory.
                getDistributedLock(SECKILL_ACTIVITES_UPDATE_CACHE_LOCK_KEY.concat(String.valueOf(status)));
        try {
            boolean isLockSuccess = lock.tryLock(1, 5, TimeUnit.SECONDS);
            if (!isLockSuccess){
                return new SeckillBusinessCache<List<SeckillActivity>>().retryLater();
            }
            List<SeckillActivity> seckillActivityList = seckillActivityRepository.getSeckillActivityList(status);
            SeckillBusinessCache<List<SeckillActivity>> seckillActivitiyListCache;
            if (seckillActivityList == null) {
                seckillActivitiyListCache = new SeckillBusinessCache<List<SeckillActivity>>().notExist();
            }else {
                seckillActivitiyListCache = new SeckillBusinessCache<List<SeckillActivity>>()
                        .with(seckillActivityList).withVersion(SystemClock .millisClock().now());
            }
            distributedCacheService.put(buildCacheKey(status), JSON.toJSONString(seckillActivitiyListCache), SeckillConstants.FIVE_MINUTES);
            logger.info("SeckillActivitesCache|分布式缓存已经更新|{}", status);
            return seckillActivitiyListCache;
        }catch (InterruptedException e) {
            logger.info("SeckillActivitesCache|更新分布式缓存失败|{}", status);
            return new SeckillBusinessCache<List<SeckillActivity>>().retryLater();
        }finally {
            lock.unlock();
        }
    }

    @Override
    public String buildCacheKey(Object key) {
        return StringUtil.append(SeckillConstants.SECKILL_ACTIVITIES_CACHE_KEY, key);
    }
}
