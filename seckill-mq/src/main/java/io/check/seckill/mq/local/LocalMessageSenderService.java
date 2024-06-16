package io.check.seckill.mq.local;

import com.alibaba.cola.event.EventBusI;
import io.check.seckill.common.model.event.SeckillBaseEvent;
import io.check.seckill.common.model.message.TopicMessage;
import io.check.seckill.mq.MessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author check
 * @version 1.0.0
 * @description 本地事件发布
 */
@Component
@ConditionalOnProperty(name = "message.mq.type", havingValue = "cola")
public class LocalMessageSenderService implements MessageSenderService {

    @Autowired
    private EventBusI eventBus;

    @Override
    public void send(TopicMessage message) {
        eventBus.fire(message);
    }

}
