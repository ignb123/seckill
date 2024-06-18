package io.check.seckill.common.model.dto.order;


/**
 * @author check
 * @version 1.0.0
 * @description 订单提交后返回的数据
 */
public class SeckillOrderSubmitDTO {

    /**
     * 同步下单时，为订单id
     * 异步下单时，为许可id
     */
    private String id;

    /**
     * 商品id
     */
    private Long goodsId;

    /**
     * 类型
     * type_order：id为订单号
     * type_task：id为下单许可号
     */
    private String type;

    public SeckillOrderSubmitDTO() {
    }

    public SeckillOrderSubmitDTO(String id, Long goodsId, String type) {
        this.id = id;
        this.type = type;
        this.goodsId = goodsId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Long getGoodsId() {
        return goodsId;
    }

    public void setGoodsId(Long goodsId) {
        this.goodsId = goodsId;
    }
}
