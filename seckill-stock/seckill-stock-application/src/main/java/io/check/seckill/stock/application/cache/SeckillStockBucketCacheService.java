package io.check.seckill.stock.application.cache;

import io.check.seckill.common.cache.model.SeckillBusinessCache;
import io.check.seckill.common.cache.service.SeckillCacheService;
import io.check.seckill.stock.application.model.dto.SeckillStockBucketDTO;

/**
 * @author check
 * @version 1.0.0
 * @description 缓存库存分桶
 */
public interface SeckillStockBucketCacheService extends SeckillCacheService {

    /**
     * 缓存库存分桶信息
     */
    SeckillBusinessCache<SeckillStockBucketDTO> getTotalStockBuckets(Long goodsId, Long version);


    /**
     * 更新缓存
     */
    SeckillBusinessCache<SeckillStockBucketDTO> tryUpdateSeckillStockBucketCacheByLock(Long goodsId, boolean doubleCheck);
}

