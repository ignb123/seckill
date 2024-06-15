package io.check.seckill.common.model.enums;



/**
 * @author check
 * @version 1.0.0
 * @description 用户状态
 */

public enum SeckillUserStatus {
    NORMAL(1),
    FREEZE(2);

    private final Integer code;

    SeckillUserStatus(Integer code) {
        this.code = code;
    }

    public static boolean isNormal(Integer status) {
        return NORMAL.getCode().equals(status);
    }

    public Integer getCode() {
        return code;
    }
}
