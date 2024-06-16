package io.check.seckill.order.application.service.impl;

import io.check.seckill.order.application.command.SeckillOrderCommand;

public interface SeckillSubmitOrderService {
    SeckillOrderSubmitDTO saveSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand);
    default void handlePlaceOrderTask(SeckillOrderTask seckillOrderTask){}
    default void checkSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand){}
}
