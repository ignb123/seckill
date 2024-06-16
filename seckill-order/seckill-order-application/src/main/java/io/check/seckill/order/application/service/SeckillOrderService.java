package io.check.seckill.order.application.service;

import io.check.seckill.common.model.message.ErrorMessage;
import io.check.seckill.order.application.command.SeckillOrderCommand;
import io.check.seckill.order.domain.model.entity.SeckillOrder;

import java.util.List;

/**
 * @author check
 * @version 1.0.0
 * @description 订单
 */
public interface SeckillOrderService {

    /**
     * 保存订单
     */
    Long saveSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand);

    /**
     * 根据用户id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByUserId(Long userId);

    /**
     * 根据活动id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByActivityId(Long activityId);

    /**
     * 删除订单
     */
    void deleteOrder(ErrorMessage errorMessage);
}
