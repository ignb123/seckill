package io.check.seckill.common.model.enums;

/**
 * @author check
 * @version 1.0.0
 * @description 分桶库存状态
 */
public enum SeckillStockBucketStatus {

    ENABLED(1),
    DISABLED(0);

    private final Integer code;

    SeckillStockBucketStatus(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}

