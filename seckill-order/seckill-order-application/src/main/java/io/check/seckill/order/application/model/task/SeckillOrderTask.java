package io.check.seckill.order.application.model.task;

import cn.hutool.core.util.StrUtil;
import io.check.seckill.common.model.message.TopicMessage;
import io.check.seckill.order.application.model.command.SeckillOrderCommand;

/**
 * @author check
 * @version 1.0.0
 * @description 异步下单提交的订单任务
 */
public class SeckillOrderTask extends TopicMessage {

    //订单任务id
    private String orderTaskId;

    //用户id
    private Long userId;

    //提交的订单数据
    private SeckillOrderCommand seckillOrderCommand;

    public SeckillOrderTask() {
    }

    public SeckillOrderTask(String destination, String orderTaskId, Long userId, SeckillOrderCommand seckillOrderCommand) {
        super(destination);
        this.orderTaskId = orderTaskId;
        this.userId = userId;
        this.seckillOrderCommand = seckillOrderCommand;
    }

    public String getOrderTaskId() {
        return orderTaskId;
    }

    public void setOrderTaskId(String orderTaskId) {
        this.orderTaskId = orderTaskId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public SeckillOrderCommand getSeckillOrderCommand() {
        return seckillOrderCommand;
    }

    public void setSeckillOrderCommand(SeckillOrderCommand seckillOrderCommand) {
        this.seckillOrderCommand = seckillOrderCommand;
    }

    public boolean isEmpty(){
        return StrUtil.isEmpty(this.getDestination())
                || StrUtil.isEmpty(orderTaskId)
                || userId == null
                || seckillOrderCommand == null;
    }
}
