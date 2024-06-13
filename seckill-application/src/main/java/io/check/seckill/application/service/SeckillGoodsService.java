package io.check.seckill.application.service;


import io.check.seckill.domain.dto.SeckillGoodsDTO;
import io.check.seckill.domain.model.SeckillGoods;

import java.util.List;

/**
 * @author check
 * @version 1.0.0
 * @description 商品
 */
public interface SeckillGoodsService {

    /**
     * 保存商品信息
     */
    int saveSeckillGoods(SeckillGoodsDTO seckillGoodsDTO);

    /**
     * 根据id获取商品详细信息
     */
    SeckillGoods getSeckillGoodsId(Long id);

    /**
     * 根据活动id获取商品列表
     */
    List<SeckillGoods> getSeckillGoodsByActivityId(Long activityId);

    /**
     * 修改商品状态
     */
    int updateStatus(Integer status, Long id);

    /**
     * 扣减库存
     */
    int updateAvailableStock(Integer count, Long id);

    /**
     * 获取当前可用库存
     */
    Integer getAvailableStockById(Long id);

    /**
     * 根据活动id从缓存中获取数据
     */
    List<SeckillGoodsDTO> getSeckillGoodsList(Long activityId, Long version);

    /**
     * 根据id获取商品详细信息（带缓存）
     */
    SeckillGoodsDTO getSeckillGoods(Long id, Long version);
}

