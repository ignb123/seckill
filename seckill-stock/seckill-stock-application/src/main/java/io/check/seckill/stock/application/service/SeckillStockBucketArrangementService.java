package io.check.seckill.stock.application.service;

import io.check.seckill.stock.application.model.dto.SeckillStockBucketDTO;

/**
 * @author check
 * @version 1.0.0
 * @description 库存编排服务
 */
public interface SeckillStockBucketArrangementService {

    /**
     * 编码分桶库存
     * @param goodsId 商品id
     * @param stock 库存数量
     * @param bucketsQuantity 分桶数量
     * @param assignmentMode 编排模式, 1:总量模式; 2:增量模式
     */
    void arrangeStockBuckets(Long goodsId, Integer stock, Integer bucketsQuantity, Integer assignmentMode);

    /**
     * 通过商品id获取库存分桶信息
     */
    SeckillStockBucketDTO getSeckillStockBucketDTO(Long goodsId, Long version);
}

