package io.check.seckill.goods.domain.event;

import com.alibaba.fastjson.annotation.JSONField;
import io.check.seckill.common.model.event.SeckillBaseEvent;

/**
 * @author check
 * @version 1.0.0
 * @description 商品事件
 */
public class SeckillGoodsEvent extends SeckillBaseEvent {
   private Long activityId;


    public SeckillGoodsEvent(Long id, Long activityId, Integer status, @JSONField(name = "destination") String topicEvent) {
        super(id, status, topicEvent);
        this.activityId = activityId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
}
