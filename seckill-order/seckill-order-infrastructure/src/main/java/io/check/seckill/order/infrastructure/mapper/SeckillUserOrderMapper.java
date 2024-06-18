package io.check.seckill.order.infrastructure.mapper;

import io.check.seckill.order.domain.model.entity.SeckillOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author check
 * @version 1.0.0
 * @description 订单
 */
public interface SeckillUserOrderMapper {

    /**
     * 保存订单
     */
    int saveSeckillOrder(SeckillOrder seckillOrder);

    /**
     * 根据用户id获取订单列表
     */
    List<SeckillOrder> getSeckillOrderByUserId(@Param("userId") Long userId);

    /**
     * 删除订单数据
     */
    void deleteOrder(@Param("userId") Long userId, @Param("orderId") Long orderId);

}
