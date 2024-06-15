package io.check.seckill.order.domain.service;

import io.check.seckill.order.domain.model.entity.SeckillOrder;

import java.util.List;

/**
 * @author check
 * @version 1.0.0
 * @description 订单领域层接口
 */
public interface SeckillOrderDomainService {

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
