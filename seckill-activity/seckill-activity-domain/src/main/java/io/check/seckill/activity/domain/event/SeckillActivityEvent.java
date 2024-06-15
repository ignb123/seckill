package io.check.seckill.activity.domain.event;

import io.check.seckill.common.event.SeckillBaseEvent;

public class SeckillActivityEvent extends SeckillBaseEvent {
    public SeckillActivityEvent(Long id, Integer status) {
        super(id, status);
    }
}