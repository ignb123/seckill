package io.check.seckill.reservation.application.command;

import java.io.Serializable;

/**
 * @author check
 * @version 1.0.0
 * @description 预约记录
 */
public class SeckillReservationUserCommand implements Serializable {
    //用户id
    private Long userId;
    //商品id
    private Long goodsId;

    public SeckillReservationUserCommand() {
    }

    public SeckillReservationUserCommand(Long userId, Long goodsId) {
        this.userId = userId;
        this.goodsId = goodsId;
    }

    public boolean isEmpty(){
        return this.userId == null
                || this.goodsId == null;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }
}
