package io.check.seckill.order.domain.event;


import com.alibaba.fastjson.annotation.JSONField;
import io.check.seckill.common.model.event.SeckillBaseEvent;

/**
 * @author check
 * @version 1.0.0
 * @description 订单事件
 */
public class SeckillOrderEvent extends SeckillBaseEvent {

    public SeckillOrderEvent(Long id, Integer status, @JSONField(name = "destination") String topicEvent) {
        super(id, status, topicEvent);
    }

}
