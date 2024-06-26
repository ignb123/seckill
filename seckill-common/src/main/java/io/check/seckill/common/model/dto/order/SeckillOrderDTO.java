package io.check.seckill.common.model.dto.order;

import java.io.Serializable;

public class SeckillOrderDTO implements Serializable {
    private static final long serialVersionUID = -3164396374622988886L;

    //订单id
    private Long id;

    //用户id
    private Long userId;

    //商品id
    private Long goodsId;

    //购买数量
    private Integer quantity;

    //活动id
    private Long activityId;

    //版本号
    private Long version;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Long getActivityId() {
        return activityId;
    }

    public void setActivityId(Long activityId) {
        this.activityId = activityId;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }
}
