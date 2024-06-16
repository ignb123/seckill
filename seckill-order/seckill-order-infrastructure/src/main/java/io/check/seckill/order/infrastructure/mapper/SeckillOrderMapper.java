package io.check.seckill.order.infrastructure.mapper;

import io.check.seckill.order.domain.model.entity.SeckillOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author check
 * @version 1.0.0
 * @description 订单
 */
public interface SeckillOrderMapper {

    /**
     * 保存订单
     */
    int saveSeckillOrder(SeckillOrder seckillOrder);

    /**
     * 根据用户id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByUserId(@Param("userId") Long userId);

    /**
     * 根据活动id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByActivityId(@Param("activityId") Long activityId);

    /**
     * 删除订单数据
     */
    void deleteOrder(@Param("orderId") Long orderId);
}
