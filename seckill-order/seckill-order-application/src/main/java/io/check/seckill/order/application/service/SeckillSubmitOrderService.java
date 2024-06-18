package io.check.seckill.order.application.service;

import io.check.seckill.common.model.dto.order.SeckillOrderSubmitDTO;
import io.check.seckill.order.application.model.task.SeckillOrderTask;
import io.check.seckill.order.application.model.command.SeckillOrderCommand;

/**
 * @author check
 * @version 1.0.0
 * @description 提交订单服务
 */
public interface SeckillSubmitOrderService {

    /**
     * 保存订单
     */
    SeckillOrderSubmitDTO saveSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand);


    /**
     * 处理订单任务
     */
    default void handlePlaceOrderTask(SeckillOrderTask seckillOrderTask){

    }

    /**
     * 实现基础校验功能
     */
    default void checkSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand){
    }
}
