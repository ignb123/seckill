package io.check.seckill.common.model.message;

import java.math.BigDecimal;

/**
 * @author check
 * @version 1.0.0
 * @description 事务消息
 */
public class TxMessage extends ErrorMessage{

    //活动id
    private Long activityId;
    //商品版本号
    private Long version;
    //用户id
    private Long userId;
    //商品名称
    private String goodsName;
    //秒杀活动价格
    private BigDecimal activityPrice;

    public TxMessage() {
    }

    public TxMessage(String destination, Long txNo, Long goodsId, Integer quantity, Long activityId, Long version, Long userId, String goodsName,
                     BigDecimal activityPrice, String placeOrderType, Boolean exception) {
        super(destination, txNo, goodsId, quantity, placeOrderType, exception);
        this.activityId = activityId;
        this.version = version;
        this.userId = userId;
        this.goodsName = goodsName;
        this.activityPrice = activityPrice;
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

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getGoodsName() {
        return goodsName;
    }

    public void setGoodsName(String goodsName) {
        this.goodsName = goodsName;
    }

    public BigDecimal getActivityPrice() {
        return activityPrice;
    }

    public void setActivityPrice(BigDecimal activityPrice) {
        this.activityPrice = activityPrice;
    }
}
