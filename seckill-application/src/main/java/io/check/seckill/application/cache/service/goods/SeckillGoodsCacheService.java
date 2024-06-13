package io.check.seckill.application.cache.service.goods;

import io.check.seckill.application.cache.model.SeckillBusinessCache;
import io.check.seckill.application.service.common.SeckillCacheService;
import io.check.seckill.domain.model.SeckillGoods;

public interface SeckillGoodsCacheService extends SeckillCacheService {

    /**
     * 根据商品id和版本号获取秒杀商品列表缓存模型数据
     */
    SeckillBusinessCache<SeckillGoods> getSeckillGoods(Long goodsId, Long version);

    /**
     * 尝试更新缓存数据。
     */
    SeckillBusinessCache<SeckillGoods> tryUpdateSeckillGoodsCacheByLock(Long goodsId);
}
