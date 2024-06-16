package io.check.seckill.order.application.service;

import io.check.seckill.order.application.model.task.SeckillOrderTask;

/**
 * 提交订单任务
 */
public interface PlaceOrderTaskService {
    boolean submitOrderTask(SeckillOrderTask seckillOrderTask);
}

