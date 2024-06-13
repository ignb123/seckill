package io.check.seckill.application.cache.service.activity;

import io.check.seckill.application.cache.model.SeckillBusinessCache;
import io.check.seckill.application.service.common.SeckillCacheService;
import io.check.seckill.domain.model.SeckillActivity;

public interface SeckillActivityCacheService extends SeckillCacheService {

    /**
     * 根据状态和版本号获取秒杀活动详情缓存模型数据。
     */
    SeckillBusinessCache<SeckillActivity> getCachedSeckillActivity(Long activityId, Long version);

    /**
     * 尝试更新缓存数据
     */
    SeckillBusinessCache<SeckillActivity> tryUpdateSeckillActivityCacheByLock(Long activityId);
}