package io.check.seckill.common.model.enums;

/**
 * @author check
 * @version 1.0.0
 * @description 商品状态
 */
public enum SeckillGoodsStatus {

    PUBLISHED(0),
    ONLINE(1),
    OFFLINE(-1);

    private final Integer code;

    SeckillGoodsStatus(Integer code) {
        this.code = code;
    }

    public static boolean isOffline(Integer status) {
        return OFFLINE.getCode().equals(status);
    }

    public static boolean isOnline(Integer status) {
        return ONLINE.getCode().equals(status);
    }

    public Integer getCode() {
        return code;
    }
}

