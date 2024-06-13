package io.check.seckill.domain.service;

import io.check.seckill.domain.model.entity.SeckillOrder;

import java.util.List;

public interface SeckillOrderDomainService {

    /**
     * 保存订单
     */
    boolean saveSeckillOrder(SeckillOrder seckillOrder);

    /**
     * 根据用户id查询订单列表
     */
    List<SeckillOrder> getSeckillOrderByUserId(Long userId);

    /**
     * 根据活动id查询订单列表
     */
    List<SeckillOrder> getSeckillOrderByActivityId(Long activityId);
}
