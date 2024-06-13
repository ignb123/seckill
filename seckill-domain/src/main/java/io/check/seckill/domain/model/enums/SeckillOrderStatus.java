package io.check.seckill.domain.model.enums;

/**
 * @author check
 * @version 1.0.0
 * @description 订单状态
 */
public enum SeckillOrderStatus {

    CREATED(1),
    PAID(2),
    CANCELED(0),
    DELETED(-1);

    private final Integer code;

    SeckillOrderStatus(Integer code) {
        this.code = code;
    }

    public static boolean isCancled(Integer status) {
        return CANCELED.getCode().equals(status);
    }

    public static boolean isDeleted(Integer status) {
        return DELETED.getCode().equals(status);
    }

    public Integer getCode() {
        return code;
    }
}

