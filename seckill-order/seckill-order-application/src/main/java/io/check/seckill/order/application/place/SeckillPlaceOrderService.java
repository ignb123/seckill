package io.check.seckill.order.application.place;


import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.exception.SeckillException;
import io.check.seckill.common.model.dto.SeckillGoodsDTO;
import io.check.seckill.common.model.enums.SeckillGoodsStatus;
import io.check.seckill.common.model.enums.SeckillOrderStatus;
import io.check.seckill.common.utils.beans.BeanUtil;
import io.check.seckill.common.utils.id.SnowFlakeFactory;
import io.check.seckill.order.application.command.SeckillOrderCommand;
import io.check.seckill.order.domain.model.entity.SeckillOrder;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author check
 * @version 1.0.0
 * @description 下单接口
 */
public interface SeckillPlaceOrderService {

    /**
     * 下单操作
     */
    Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand);

    /**
     * 构建订单
     */
    default SeckillOrder buildSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand, SeckillGoodsDTO seckillGoods){
        SeckillOrder seckillOrder = new SeckillOrder();
        BeanUtil.copyProperties(seckillOrderCommand, seckillOrder);
        seckillOrder.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
        seckillOrder.setGoodsName(seckillGoods.getGoodsName());
        seckillOrder.setUserId(userId);
        seckillOrder.setActivityPrice(seckillGoods.getActivityPrice());
        BigDecimal orderPrice = seckillGoods.getActivityPrice().multiply(BigDecimal.valueOf(seckillOrder.getQuantity()));
        seckillOrder.setOrderPrice(orderPrice);
        seckillOrder.setStatus(SeckillOrderStatus.CREATED.getCode());
        seckillOrder.setCreateTime(new Date());
        return seckillOrder;
    }

    /**
     * 检测商品信息
     */
    default void checkSeckillGoods(SeckillOrderCommand seckillOrderCommand, SeckillGoodsDTO seckillGoods){
        //商品不存在
        if (seckillGoods == null){
            throw new SeckillException(ErrorCode.GOODS_NOT_EXISTS);
        }
        //商品未上线
        if (seckillGoods.getStatus() == SeckillGoodsStatus.PUBLISHED.getCode()){
            throw new SeckillException(ErrorCode.GOODS_PUBLISH);
        }
        //商品已下架
        if (seckillGoods.getStatus() == SeckillGoodsStatus.OFFLINE.getCode()){
            throw new SeckillException(ErrorCode.GOODS_OFFLINE);
        }
        //触发限购
        if (seckillGoods.getLimitNum() < seckillOrderCommand.getQuantity()){
            throw new SeckillException(ErrorCode.BEYOND_LIMIT_NUM);
        }
        // 库存不足
        if (seckillGoods.getAvailableStock() == null || seckillGoods.getAvailableStock() <= 0 || seckillOrderCommand.getQuantity() > seckillGoods.getAvailableStock()){
            throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
        }
    }

}