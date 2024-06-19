package io.check.seckill.reservation.application.cache.impl;

import com.alibaba.fastjson.JSON;
import com.google.common.cache.Cache;
import io.check.seckill.common.cache.distribute.DistributedCacheService;
import io.check.seckill.common.cache.local.guava.LocalCacheFactory;
import io.check.seckill.common.cache.model.SeckillBusinessCache;
import io.check.seckill.common.constants.SeckillConstants;
import io.check.seckill.common.lock.DistributedLock;
import io.check.seckill.common.lock.factoty.DistributedLockFactory;
import io.check.seckill.common.model.enums.SeckillReservationUserStatus;
import io.check.seckill.common.utils.string.StringUtil;
import io.check.seckill.common.utils.time.SystemClock;
import io.check.seckill.reservation.application.builder.SeckillReservationConfigBuilder;
import io.check.seckill.reservation.application.cache.SeckillReservationConfigCacheService;
import io.check.seckill.reservation.domain.model.entity.SeckillReservationConfig;
import io.check.seckill.reservation.domain.service.SeckillReservationDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author check
 * @version 1.0.0
 * @description SeckillReservationConfigCacheServiceImpl
 */
@Service
public class SeckillReservationConfigCacheServiceImpl implements SeckillReservationConfigCacheService {
    private final static Logger logger = LoggerFactory.getLogger(SeckillReservationConfigCacheServiceImpl.class);
    //存储预约配置信息
    private static final Cache<Long, SeckillBusinessCache<SeckillReservationConfig>> localSeckillReservationConfigCacheService = LocalCacheFactory.getLocalCache();
    //存储预约配置列表
    private static final Cache<String, SeckillBusinessCache<List<SeckillReservationConfig>>> localSeckillReservationConfigListCacheService = LocalCacheFactory.getLocalCache();
    //更新预约配置时获取分布式锁使用
    private static final String SECKILL_RESERVATION_CONFIG_UPDATE_CACHE_LOCK_KEY = "SECKILL_RESERVATION_CONFIG_UPDATE_CACHE_LOCK_KEY_";
    //更新预约配置列表时获取分布式锁使用
    private static final String SECKILL_RESERVATION_CONFIG_LIST_UPDATE_CACHE_LOCK_KEY = "SECKILL_RESERVATION_CONFIG_LIST_UPDATE_CACHE_LOCK_KEY_LIST";
    //本地可重入锁
    private final Lock localCacheUpdatelock = new ReentrantLock();
    //本地列表可重入锁
    private final Lock localCacheListUpdatelock = new ReentrantLock();
    @Resource
    private DistributedCacheService distributedCacheService;
    @Resource
    private SeckillReservationDomainService seckillReservationDomainService;
    @Resource
    private DistributedLockFactory distributedLockFactory;


    @Override
    public SeckillBusinessCache<SeckillReservationConfig> getSeckillReservationConfig(Long goodsId, Long version) {
        //从本地缓存中获取数据
        SeckillBusinessCache<SeckillReservationConfig> seckillReservationConfigCache = localSeckillReservationConfigCacheService.getIfPresent(goodsId);
        if (seckillReservationConfigCache != null){
            //版本号为空，则直接返回本地缓存中的数据
            if (seckillReservationConfigCache.getVersion() == null){
                logger.info("SeckillReservationConfigCache|命中本地缓存|{}", goodsId);
                return seckillReservationConfigCache;
            }
            //传递的版本号小于等于缓存中的版本号，则说明缓存中的数据比客户端的数据新，直接返回本地缓存中的数据
            if (version.compareTo(seckillReservationConfigCache.getVersion()) <= 0){
                logger.info("SeckillReservationConfigCache|命中本地缓存|{}", goodsId);
                return seckillReservationConfigCache;
            }
            //传递的版本号大于缓存中的版本号，说明缓存中的数据比较落后，从分布式缓存获取数据并更新到本地缓存
            if (version.compareTo(seckillReservationConfigCache.getVersion()) > 0){
                return getSeckillReservationConfigDistributedCache(goodsId);
            }
        }
        return getSeckillReservationConfigDistributedCache(goodsId);
    }

    @Override
    public SeckillBusinessCache<SeckillReservationConfig> updateSeckillReservationConfigCurrentUserCount(Long goodsId, Integer status, Long version) {
        logger.info("SeckillReservationConfigCache|更新分布式缓存当前预约人数|{}|{}", goodsId, status);
        SeckillBusinessCache<SeckillReservationConfig> seckillReservationConfigCache = this.getSeckillReservationConfig(goodsId, version);
        if (seckillReservationConfigCache.isRetryLater() || !seckillReservationConfigCache.isExist()){
            return seckillReservationConfigCache;
        }
        SeckillReservationConfig seckillReservationConfig = seckillReservationConfigCache.getData();
        if (seckillReservationConfig == null){
            return seckillReservationConfigCache;
        }
        int userCount = 0;
        if (SeckillReservationUserStatus.isNormal(status)){
            userCount = seckillReservationConfig.getReserveCurrentUserCount() + 1;
            userCount = Math.min(userCount, seckillReservationConfig.getReserveMaxUserCount());
        }else{
            userCount = seckillReservationConfig.getReserveCurrentUserCount() - 1;
            userCount = Math.max(userCount, 0);
        }
        seckillReservationConfig.setReserveCurrentUserCount(userCount);
        seckillReservationConfigCache = new SeckillBusinessCache<SeckillReservationConfig>().with(seckillReservationConfig).withVersion(SystemClock.millisClock().now());
        //将数据保存到分布式缓存
        distributedCacheService.put(this.getKey(SeckillConstants.SECKILL_RESERVATION_CONFIG_CACHE_KEY, goodsId), JSON.toJSONString(seckillReservationConfigCache), SeckillConstants.HOURS_24);
        logger.info("SeckillReservationConfigCache|分布式缓存已经更新|{}|{}", goodsId, status);
        return seckillReservationConfigCache;
    }

    /**
     * 从分布式缓存获取数据
     */
    private SeckillBusinessCache<SeckillReservationConfig> getSeckillReservationConfigDistributedCache(Long goodsId) {
        logger.info("SeckillReservationConfigCache|读取分布式缓存|{}", goodsId);
        //从分布式缓存中获取数据
        SeckillBusinessCache<SeckillReservationConfig> seckillReservationConfigCache = SeckillReservationConfigBuilder.getSeckillBusinessCache(distributedCacheService.getObject(this.getKey(SeckillConstants.SECKILL_RESERVATION_CONFIG_CACHE_KEY, goodsId)), SeckillReservationConfig.class);
        //分布式缓存中没有数据
        if (seckillReservationConfigCache == null){
            // 尝试更新分布式缓存中的数据，注意的是只用一个线程去更新分布式缓存中的数据
            seckillReservationConfigCache = tryUpdateSeckillReservationConfigCacheByLock(goodsId, true);
        }
        //获取的数据不为空，并且不需要重试
        if (seckillReservationConfigCache != null && !seckillReservationConfigCache.isRetryLater()){
            //获取本地锁，更新本地缓存
            if (localCacheUpdatelock.tryLock()){
                try {
                    localSeckillReservationConfigCacheService.put(goodsId, seckillReservationConfigCache);
                    logger.info("SeckillReservationConfigCache|本地缓存已经更新|{}", goodsId);
                }finally {
                    localCacheUpdatelock.unlock();
                }
            }
        }
        return seckillReservationConfigCache;
    }


    @Override
    public SeckillBusinessCache<SeckillReservationConfig> tryUpdateSeckillReservationConfigCacheByLock(Long goodsId, boolean doubleCheck) {
        logger.info("SeckillReservationConfigCache|更新分布式缓存|{}", goodsId);
        //获取分布式锁，保证只有一个线程在更新分布式缓存
        DistributedLock lock = distributedLockFactory.getDistributedLock(SECKILL_RESERVATION_CONFIG_UPDATE_CACHE_LOCK_KEY.concat(String.valueOf(goodsId)));
        try {
            boolean isSuccess = lock.tryLock(2, 5, TimeUnit.SECONDS);
            //未获取到分布式锁的线程快速返回，不占用系统资源
            if (!isSuccess){
                return new SeckillBusinessCache<SeckillReservationConfig>().retryLater();
            }
            SeckillBusinessCache<SeckillReservationConfig> seckillReservationConfigCache;
            if (doubleCheck){
                //获取锁成功后，再次从缓存中获取数据，防止高并发下多个线程争抢锁的过程中，后续的线程再等待1秒的过程中，前面的线程释放了锁，后续的线程获取锁成功后再次更新分布式缓存数据
                seckillReservationConfigCache = SeckillReservationConfigBuilder.getSeckillBusinessCache(distributedCacheService.getObject(this.getKey(SeckillConstants.SECKILL_RESERVATION_CONFIG_CACHE_KEY, goodsId)), SeckillReservationConfig.class);
                if (seckillReservationConfigCache != null){
                    return seckillReservationConfigCache;
                }
            }
            SeckillReservationConfig seckillReservationConfig = seckillReservationDomainService.getConfigDetail(goodsId);
            if (seckillReservationConfig == null){
                seckillReservationConfigCache = new SeckillBusinessCache<SeckillReservationConfig>().notExist();
            }else {
                seckillReservationConfigCache = new SeckillBusinessCache<SeckillReservationConfig>().with(seckillReservationConfig).withVersion(SystemClock.millisClock().now());
            }
            //将数据保存到分布式缓存
            distributedCacheService.put(this.getKey(SeckillConstants.SECKILL_RESERVATION_CONFIG_CACHE_KEY, goodsId), JSON.toJSONString(seckillReservationConfigCache), SeckillConstants.HOURS_24);
            logger.info("SeckillReservationConfigCache|分布式缓存已经更新|{}", goodsId);
            return seckillReservationConfigCache;
        } catch (InterruptedException e) {
            logger.error("SeckillReservationConfigCache|更新分布式缓存失败|{}", goodsId);
            return new SeckillBusinessCache<SeckillReservationConfig>().retryLater();
        }finally {
            lock.unlock();
        }
    }

    @Override
    public SeckillBusinessCache<List<SeckillReservationConfig>> getSeckillReservationConfigList(Long version) {
        //从本地缓存中获取数据
        SeckillBusinessCache<List<SeckillReservationConfig>> seckillReservationConfigListCache = localSeckillReservationConfigListCacheService.getIfPresent(SeckillConstants.SECKILL_RESERVATION_CONFIG_LIST_CACHE_KEY);
        if (seckillReservationConfigListCache != null){
            //版本号为空，则直接返回本地缓存中的数据
            if (seckillReservationConfigListCache.getVersion() == null){
                logger.info("SeckillReservationConfigListCache|命中本地缓存");
                return seckillReservationConfigListCache;
            }
            //传递的版本号小于等于缓存中的版本号，则说明缓存中的数据比客户端的数据新，直接返回本地缓存中的数据
            if (version.compareTo(seckillReservationConfigListCache.getVersion()) <= 0){
                logger.info("SeckillReservationConfigListCache|命中本地缓存");
                return seckillReservationConfigListCache;
            }
            //传递的版本号大于缓存中的版本号，说明缓存中的数据比较落后，从分布式缓存获取数据并更新到本地缓存
            if (version.compareTo(seckillReservationConfigListCache.getVersion()) > 0){
                return getSeckillReservationConfigListDistributedCache();
            }
        }
        return getSeckillReservationConfigListDistributedCache();
    }

    /**
     * 从分布式缓存获取数据
     */
    private SeckillBusinessCache<List<SeckillReservationConfig>> getSeckillReservationConfigListDistributedCache() {
        logger.info("SeckillReservationConfigListCache|读取分布式缓存");
        //从分布式缓存中获取数据
        SeckillBusinessCache<List<SeckillReservationConfig>> seckillReservationConfigListCache = SeckillReservationConfigBuilder.getSeckillBusinessCacheList(distributedCacheService.getObject(SeckillConstants.SECKILL_RESERVATION_CONFIG_LIST_CACHE_KEY), SeckillReservationConfig.class);
        //分布式缓存中没有数据
        if (seckillReservationConfigListCache == null){
            // 尝试更新分布式缓存中的数据，注意的是只用一个线程去更新分布式缓存中的数据
            seckillReservationConfigListCache = tryUpdateSeckillReservationConfigListCacheByLock(true);
        }
        //获取的数据不为空，并且不需要重试
        if (seckillReservationConfigListCache != null && !seckillReservationConfigListCache.isRetryLater()){
            //获取本地锁，更新本地缓存
            if (localCacheListUpdatelock.tryLock()){
                try {
                    localSeckillReservationConfigListCacheService.put(SeckillConstants.SECKILL_RESERVATION_CONFIG_LIST_CACHE_KEY, seckillReservationConfigListCache);
                    logger.info("SeckillReservationConfigListCache|本地缓存已经更新");
                }finally {
                    localCacheListUpdatelock.unlock();
                }
            }
        }
        return seckillReservationConfigListCache;
    }

    @Override
    public SeckillBusinessCache<List<SeckillReservationConfig>> tryUpdateSeckillReservationConfigListCacheByLock(boolean doubleCheck) {
        logger.info("SeckillReservationConfigListCache|更新分布式缓存");
        //获取分布式锁，保证只有一个线程在更新分布式缓存
        DistributedLock lock = distributedLockFactory.getDistributedLock(SECKILL_RESERVATION_CONFIG_LIST_UPDATE_CACHE_LOCK_KEY);
        try {
            boolean isSuccess = lock.tryLock(2, 5, TimeUnit.SECONDS);
            //未获取到分布式锁的线程快速返回，不占用系统资源
            if (!isSuccess){
                return new SeckillBusinessCache<List<SeckillReservationConfig>>().retryLater();
            }
            SeckillBusinessCache<List<SeckillReservationConfig>> seckillReservationConfigListCache;
            if (doubleCheck){
                //获取锁成功后，再次从缓存中获取数据，防止高并发下多个线程争抢锁的过程中，后续的线程再等待1秒的过程中，前面的线程释放了锁，后续的线程获取锁成功后再次更新分布式缓存数据
                seckillReservationConfigListCache = SeckillReservationConfigBuilder.getSeckillBusinessCacheList(distributedCacheService.getObject(SeckillConstants.SECKILL_RESERVATION_CONFIG_LIST_CACHE_KEY), SeckillReservationConfig.class);
                if (seckillReservationConfigListCache != null){
                    return seckillReservationConfigListCache;
                }
            }
            List<SeckillReservationConfig> seckillReservationConfigList = seckillReservationDomainService.getConfigList();
            if (seckillReservationConfigList == null || seckillReservationConfigList.isEmpty()){
                seckillReservationConfigListCache = new SeckillBusinessCache<List<SeckillReservationConfig>>().notExist();
            }else {
                seckillReservationConfigListCache = new SeckillBusinessCache<List<SeckillReservationConfig>>().with(seckillReservationConfigList).withVersion(SystemClock.millisClock().now());
            }
            //将数据保存到分布式缓存
            distributedCacheService.put(SeckillConstants.SECKILL_RESERVATION_CONFIG_LIST_CACHE_KEY, JSON.toJSONString(seckillReservationConfigListCache), SeckillConstants.HOURS_24);
            logger.info("SeckillReservationConfigListCache|分布式缓存已经更新");
            return seckillReservationConfigListCache;
        } catch (InterruptedException e) {
            logger.error("SeckillReservationConfigListCache|更新分布式缓存失败");
            return new SeckillBusinessCache<List<SeckillReservationConfig>>().retryLater();
        }finally {
            lock.unlock();
        }
    }

    private String getKey(String keyPrefix, Long goodsIs){
        return StringUtil.append(keyPrefix, goodsIs);
    }
}
