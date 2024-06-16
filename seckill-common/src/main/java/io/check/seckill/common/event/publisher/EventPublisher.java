package io.check.seckill.common.event.publisher;

import io.check.seckill.common.event.SeckillBaseEvent;

public interface EventPublisher {

    /**
     * 发布事件
     */
    void publish(SeckillBaseEvent domainEvent);
}
