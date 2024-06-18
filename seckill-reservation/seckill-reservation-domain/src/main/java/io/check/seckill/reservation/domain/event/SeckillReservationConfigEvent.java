package io.check.seckill.reservation.domain.event;


import com.alibaba.fastjson.annotation.JSONField;
import io.check.seckill.common.model.event.SeckillBaseEvent;

/**
 * @author check
 * @version 1.0.0
 * @description 预约事件
 */
public class SeckillReservationConfigEvent extends SeckillBaseEvent {

    public SeckillReservationConfigEvent(Long id, Integer status,  @JSONField(name = "destination") String topicEvent) {
        super(id, status, topicEvent);
    }
}
