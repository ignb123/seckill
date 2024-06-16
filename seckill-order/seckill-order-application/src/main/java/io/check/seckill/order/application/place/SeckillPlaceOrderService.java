package io.check.seckill.order.application.place;


import com.alibaba.fastjson.JSONObject;
import io.check.seckill.common.constants.SeckillConstants;
import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.exception.SeckillException;
import io.check.seckill.common.model.dto.SeckillGoodsDTO;
import io.check.seckill.common.model.enums.SeckillGoodsStatus;
import io.check.seckill.common.model.enums.SeckillOrderStatus;
import io.check.seckill.common.model.message.TxMessage;
import io.check.seckill.common.utils.beans.BeanUtil;
import io.check.seckill.common.utils.id.SnowFlakeFactory;
import io.check.seckill.order.application.command.SeckillOrderCommand;
import io.check.seckill.order.domain.model.entity.SeckillOrder;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;

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
     * 本地事务执行保存订单操作
     */
    void saveOrderInTransaction(TxMessage txMessage);

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
     * 构建订单
     */
    default SeckillOrder buildSeckillOrder(TxMessage txMessage){
        SeckillOrder seckillOrder = new SeckillOrder();
        seckillOrder.setId(txMessage.getTxNo());
        seckillOrder.setUserId(txMessage.getUserId());
        seckillOrder.setGoodsId(txMessage.getGoodsId());
        seckillOrder.setGoodsName(txMessage.getGoodsName());
        seckillOrder.setActivityPrice(txMessage.getActivityPrice());
        seckillOrder.setQuantity(txMessage.getQuantity());
        BigDecimal orderPrice = txMessage.getActivityPrice().multiply(BigDecimal.valueOf(seckillOrder.getQuantity()));
        seckillOrder.setOrderPrice(orderPrice);
        seckillOrder.setActivityId(txMessage.getActivityId());
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

    /**
     * 事务消息
     */
    default Message<String> getTxMessage(Long txNo, Long userId, String placeOrderType, Boolean exception,
                                         SeckillOrderCommand seckillOrderCommand, SeckillGoodsDTO seckillGoods){
        //构建事务消息
        TxMessage txMessage = new TxMessage(txNo, seckillOrderCommand.getGoodsId(), seckillOrderCommand.getQuantity(),
                seckillOrderCommand.getActivityId(), seckillOrderCommand.getVersion(), userId, seckillGoods.getGoodsName(),
                seckillGoods.getActivityPrice(), placeOrderType, exception);
        JSONObject jsonObject = new JSONObject();
        jsonObject.put(SeckillConstants.TX_MSG_KEY, txMessage);
        return MessageBuilder.withPayload(jsonObject.toJSONString()).build();
    }

}
