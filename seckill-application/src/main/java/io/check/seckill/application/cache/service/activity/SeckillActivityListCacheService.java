package io.check.seckill.application.cache.service.activity;

import io.check.seckill.application.cache.model.SeckillBusinessCache;
import io.check.seckill.application.service.common.SeckillCacheService;
import io.check.seckill.domain.model.SeckillActivity;

import java.util.List;

public interface SeckillActivityListCacheService extends SeckillCacheService {

    /**
     * 根据状态和版本号获取活动列表缓存模型数据。
     * @param status
     * @param version
     * @return
     */
    SeckillBusinessCache<List<SeckillActivity>> getCachedActivities(Integer status, Long version);

    /**
     * 尝试更新活动列表缓存模型数据。
     * @param status
     * @return
     */
    SeckillBusinessCache<List<SeckillActivity>> tryUpdateSeckillActivityCacheByLock(Integer status);
}
