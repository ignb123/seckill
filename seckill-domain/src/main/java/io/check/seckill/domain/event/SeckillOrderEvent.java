package io.check.seckill.domain.event;

public class SeckillOrderEvent extends SeckillBaseEvent {
    public SeckillOrderEvent(Long id, Integer status) {
        super(id, status);
    }
}