package io.check.seckill.stock.application.service;

import io.check.seckill.common.cache.model.SeckillBusinessCache;
import io.check.seckill.common.model.dto.stock.SeckillStockDTO;
import io.check.seckill.common.model.message.TxMessage;
import io.check.seckill.stock.application.model.command.SeckillStockBucketWrapperCommand;
import io.check.seckill.stock.application.model.dto.SeckillStockBucketDTO;

/**
 * @author check
 * @version 1.0.0
 * @description 商品库存服务
 */
public interface SeckillStockBucketService {

    /**
     * 编排库存
     */
    void arrangeStockBuckets(Long userId, SeckillStockBucketWrapperCommand stockBucketWrapperCommand);

    /**
     * 获取库存分桶
     */
    SeckillStockBucketDTO getTotalStockBuckets(Long goodsId, Long version);

    /**
     * 获取商品可用库存
     */
    SeckillBusinessCache<Integer> getAvailableStock(Long goodsId, Long version);

    /**
     * 获取商品的库存信息
     */
    SeckillBusinessCache<SeckillStockDTO> getSeckillStock(Long goodsId, Long version);

    /**
     * 扣减商品库存
     */
    boolean decreaseStock(TxMessage txMessage);

}

