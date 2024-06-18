package io.check.seckill.application.cache.service;


import io.check.seckill.common.cache.model.SeckillBusinessCache;
import io.check.seckill.common.cache.service.SeckillCacheService;
import io.check.seckill.goods.domain.model.entity.SeckillGoods;

/**
 * @author check
 * @version 1.0.0
 * @description 商品缓存服务接口
 */
public interface SeckillGoodsCacheService extends SeckillCacheService {

    /**
     * 获取商品信息
     */
    SeckillBusinessCache<SeckillGoods> getSeckillGoods(Long goodsId, Long version);

    /**
     * 更新缓存
     */
    SeckillBusinessCache<SeckillGoods> tryUpdateSeckillGoodsCacheByLock(Long goodsId, boolean doubleCheck);

    /**
     * 获取商品的可用库存
     */
    SeckillBusinessCache<Integer> getAvailableStock(Long goodsId, Long version);
}
