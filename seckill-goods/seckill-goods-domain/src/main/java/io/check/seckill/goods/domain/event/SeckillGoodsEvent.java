package io.check.seckill.goods.domain.event;

import io.check.seckill.common.event.SeckillBaseEvent;

/**
 * @author check
 * @version 1.0.0
 * @description 商品事件
 */
public class SeckillGoodsEvent extends SeckillBaseEvent {
   private Long activityId;


    public SeckillGoodsEvent(Long id, Long activityId, Integer status) {
        super(id, status);
        this.activityId = activityId;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }
}
