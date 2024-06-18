package io.check.seckill.dubbo.interfaces.stock;

import io.check.seckill.common.cache.model.SeckillBusinessCache;
import io.check.seckill.common.model.dto.stock.SeckillStockDTO;

/**
 * @author check
 * @version 1.0.0
 * @description 库存和商品都要实现的Dubbo接口
 */

public interface SeckillStockDubboService {
    /**
     * 获取商品的可用库存
     */
    SeckillBusinessCache<Integer> getAvailableStock(Long goodsId, Long version);

    /**
     * 获取商品的库存信息
     */
    SeckillBusinessCache<SeckillStockDTO> getSeckillStock(Long goodsId, Long version);
}

