package io.check.seckill.domain.repository;

import io.check.seckill.domain.model.entity.SeckillOrder;

import java.util.List;

public interface SeckillOrderRepository {

    /**
     * 保存订单
     */
    boolean saveSeckillOrder(SeckillOrder seckillOrder);

    /**
     * 根据用户id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByUserId(Long userId);

    /**
     * 根据活动id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByActivityId(Long activityId);
}
