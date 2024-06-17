package io.check.seckill.stock.domain.event;

import io.check.seckill.common.model.event.SeckillBaseEvent;

/**
 * @author check
 * @version 1.0.0
 * @description 商品库存事件
 */
public class SeckillStockBucketEvent extends SeckillBaseEvent {

    public SeckillStockBucketEvent(Long id, Integer status, String topicEvent) {
        super(id, status, topicEvent);
    }
}

