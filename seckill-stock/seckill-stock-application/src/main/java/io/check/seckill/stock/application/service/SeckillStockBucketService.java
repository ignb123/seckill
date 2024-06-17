package io.check.seckill.stock.application.service;

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
}

