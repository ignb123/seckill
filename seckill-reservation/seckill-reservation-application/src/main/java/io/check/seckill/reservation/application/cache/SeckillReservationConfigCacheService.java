package io.check.seckill.reservation.application.cache;

import io.check.seckill.common.cache.model.SeckillBusinessCache;
import io.check.seckill.common.cache.service.SeckillCacheService;
import io.check.seckill.reservation.domain.model.entity.SeckillReservationConfig;

import java.util.List;

/**
 * @author check
 * @version 1.0.0
 * @description SeckillReservationConfigCacheService
 */
public interface SeckillReservationConfigCacheService extends SeckillCacheService {

    /**
     * 根据商品id和版本号获取商品预约配置信息
     */
    SeckillBusinessCache<SeckillReservationConfig> getSeckillReservationConfig(Long goodsId, Long version);

    /**
     * 更新商品预约配置缓存
     */
    SeckillBusinessCache<SeckillReservationConfig> tryUpdateSeckillReservationConfigCacheByLock(Long goodsId, boolean doubleCheck);

    /**
     * 获取预约配置列表
     */
    SeckillBusinessCache<List<SeckillReservationConfig>> getSeckillReservationConfigList(Long version);

    /**
     * 更新预约配置列表
     */
    SeckillBusinessCache<List<SeckillReservationConfig>> tryUpdateSeckillReservationConfigListCacheByLock(boolean doubleCheck);
}

