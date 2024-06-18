package io.check.seckill.application.service;

import io.check.seckill.application.command.SeckillGoodsCommond;
import io.check.seckill.common.cache.model.SeckillBusinessCache;
import io.check.seckill.common.model.dto.goods.SeckillGoodsDTO;
import io.check.seckill.common.model.message.TxMessage;
import io.check.seckill.goods.domain.model.entity.SeckillGoods;

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
    void saveSeckillGoods(SeckillGoodsCommond seckillGoodsCommond);

    /**
     * 根据id获取商品详细信息
     */
    SeckillGoods getSeckillGoodsId(Long id);

    /**
     * 根据id获取商品详细信息（带缓存）
     */
    SeckillGoodsDTO getSeckillGoods(Long id, Long version);

    /**
     * 根据活动id获取商品列表
     */
    List<SeckillGoods> getSeckillGoodsByActivityId(Long activityId);

    /**
     * 根据活动id从缓存中获取数据
     */
    List<SeckillGoodsDTO> getSeckillGoodsList(Long activityId, Long version);

    /**
     * 修改商品状态
     */
    void updateStatus(Integer status, Long id);

    /**
     * 扣减库存
     */
    boolean updateAvailableStock(Integer count, Long id);

    boolean updateAvailableStock(TxMessage txMessage);

    /**
     * 扣减数据库库存
     */
    boolean updateDbAvailableStock(Integer count, Long id);

    /**
     * 获取当前可用库存
     */
    Integer getAvailableStockById(Long id);

    /**
     * 获取商品可用库存
     */
    SeckillBusinessCache<Integer> getAvailableStock(Long goodsId, Long version);
}
