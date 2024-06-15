package io.check.seckill.activity.application.cache.service;

import io.check.seckill.activity.domain.model.entity.SeckillActivity;
import io.check.seckill.common.cache.model.SeckillBusinessCache;
import io.check.seckill.common.cache.service.SeckillCacheService;

public interface SeckillActivityCacheService extends SeckillCacheService {

    /**
     * 根据id获取活动信息
     */
    SeckillBusinessCache<SeckillActivity> getCachedSeckillActivity(Long activityId, Long version);

    /**
     * 更新缓存数据
     */
    SeckillBusinessCache<SeckillActivity> tryUpdateSeckillActivityCacheByLock(Long activityId, boolean doubleCheck);
}