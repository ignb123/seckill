package io.check.seckill.domain.event;

public class SeckillActivityEvent extends SeckillBaseEvent {
    public SeckillActivityEvent(Long id, Integer status) {
        super(id, status);
    }
}
