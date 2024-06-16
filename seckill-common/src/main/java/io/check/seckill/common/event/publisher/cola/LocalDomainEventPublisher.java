package io.check.seckill.common.event.publisher.cola;

import com.alibaba.cola.event.DomainEventI;
import com.alibaba.cola.event.EventBusI;
import io.check.seckill.common.event.SeckillBaseEvent;
import io.check.seckill.common.event.publisher.EventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author check
 * @version 1.0.0
 * @description 本地事件发布
 */
@Component
@ConditionalOnProperty(name = "event.publish.type", havingValue = "cola")
public class LocalDomainEventPublisher implements EventPublisher {
    @Autowired
    private EventBusI eventBus;
    @Override
    public void publish(SeckillBaseEvent domainEvent) {
        eventBus.fire(domainEvent);
    }
}
