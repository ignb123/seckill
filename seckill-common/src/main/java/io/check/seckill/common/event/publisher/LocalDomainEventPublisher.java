package io.check.seckill.common.event.publisher;

import com.alibaba.cola.event.DomainEventI;
import com.alibaba.cola.event.EventBusI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author check
 * @version 1.0.0
 * @description 本地事件发布
 */
@Component
public class LocalDomainEventPublisher implements EventPublisher {
    @Autowired
    private EventBusI eventBus;
    @Override
    public void publish(DomainEventI domainEvent) {
        eventBus.fire(domainEvent);
    }
}
