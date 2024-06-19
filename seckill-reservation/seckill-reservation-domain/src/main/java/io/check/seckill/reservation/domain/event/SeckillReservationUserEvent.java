package io.check.seckill.reservation.domain.event;

import com.alibaba.fastjson.annotation.JSONField;
import io.check.seckill.common.model.event.SeckillBaseEvent;

/**
 * @author check
 * @version 1.0.0
 * @description 预约记录事件
 */
public class SeckillReservationUserEvent extends SeckillBaseEvent {
    //商品id
    private Long goodsId;

    public SeckillReservationUserEvent(Long id, Long goodsId, Integer status, @JSONField(name = "destination") String topicEvent) {
        super(id, status, topicEvent);
        this.goodsId = goodsId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }
}
