package io.check.seckill.common.model.message;

import com.alibaba.cola.event.DomainEventI;


/**
 * @author check
 * @version 1.0.0
 * @description 基础消息
 */
public class TopicMessage implements DomainEventI {

    /**
     * 消息目的地，可以是消息主题
     */
    private String destination;

    public TopicMessage(){
    }

    public TopicMessage(String destination) {
        this.destination = destination;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

}
