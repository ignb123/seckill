package io.check.seckill.common.event.publisher.rocketmq;

import com.alibaba.fastjson.JSONObject;
import io.check.seckill.common.constants.SeckillConstants;
import io.check.seckill.common.event.SeckillBaseEvent;
import io.check.seckill.common.event.publisher.EventPublisher;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "event.publish.type", havingValue = "rocketmq")
public class RocketMQDomainEventPublisher implements EventPublisher {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;


    @Override
    public void publish(SeckillBaseEvent domainEvent) {
        //发送失败消息给订单微服务
        rocketMQTemplate.send(domainEvent.getTopicEvent(), getEventMessage(domainEvent));
    }

    private Message<String> getEventMessage(SeckillBaseEvent domainEvent){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(SeckillConstants.EVENT_MSG_KEY, domainEvent);
        return MessageBuilder.withPayload(jsonObject.toJSONString()).build();
    }
}
