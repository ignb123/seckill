package io.check.seckill.order.domain.event;


import io.check.seckill.common.event.SeckillBaseEvent;

/**
 * @author check
 * @version 1.0.0
 * @description 订单事件
 */
public class SeckillOrderEvent extends SeckillBaseEvent {

    public SeckillOrderEvent(Long id, Integer status) {
        super(id, status);
    }
}
