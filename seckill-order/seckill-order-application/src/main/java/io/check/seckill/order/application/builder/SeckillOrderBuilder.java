package io.check.seckill.order.application.builder;

import io.check.seckill.common.builder.SeckillCommonBuilder;
import io.check.seckill.common.utils.beans.BeanUtil;
import io.check.seckill.order.application.command.SeckillOrderCommand;
import io.check.seckill.order.domain.model.entity.SeckillOrder;

/**
 * @author check
 * @version 1.0.0
 * @description 订单对象转换类
 */
public class SeckillOrderBuilder extends SeckillCommonBuilder {

    public static SeckillOrder toSeckillOrder(SeckillOrderCommand seckillOrderCommand){
        if (seckillOrderCommand == null){
            return null;
        }
        SeckillOrder seckillOrder = new SeckillOrder();
        BeanUtil.copyProperties(seckillOrderCommand, seckillOrder);
        return seckillOrder;
    }
}
