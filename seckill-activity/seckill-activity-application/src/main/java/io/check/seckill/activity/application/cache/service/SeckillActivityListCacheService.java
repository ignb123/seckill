package io.check.seckill.activity.application.cache.service;

import io.check.seckill.activity.domain.model.entity.SeckillActivity;
import io.check.seckill.common.cache.model.SeckillBusinessCache;
import io.check.seckill.common.cache.service.SeckillCacheService;

import java.util.Date;
import java.util.List;

public interface SeckillActivityListCacheService extends SeckillCacheService {

    /**
     * 增加二级缓存的根据状态获取活动列表
     */
    SeckillBusinessCache<List<SeckillActivity>> getCachedActivities(Integer status, Long version);

    /**
     * 增加二级缓存的根据时间和状态获取活动列表
     */
    SeckillBusinessCache<List<SeckillActivity>>  getCachedActivities(Date currentTime, Integer status, Long version);

    /**
     * 更新缓存数据
     */
    SeckillBusinessCache<List<SeckillActivity>>  tryUpdateSeckillActivityCacheByLock(Integer status, boolean doubleCheck);

    /**
     * 更新缓存数据
     */
    SeckillBusinessCache<List<SeckillActivity>>  tryUpdateSeckillActivityCacheByLock(Date currentTime, Integer status, boolean doubleCheck);
}
