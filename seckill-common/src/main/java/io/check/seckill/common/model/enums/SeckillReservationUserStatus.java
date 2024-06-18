package io.check.seckill.common.model.enums;


/**
 * @author check
 * @version 1.0.0
 * @description 预约配置状态
 */
public enum SeckillReservationUserStatus {

    NORMAL(1),
    DELETE(0);


    private final Integer code;

    public static boolean isNormal(Integer status) {
        return NORMAL.getCode().equals(status);
    }

    public static boolean isDeleted(Integer status) {
        return DELETE.getCode().equals(status);
    }

    SeckillReservationUserStatus(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }

}
