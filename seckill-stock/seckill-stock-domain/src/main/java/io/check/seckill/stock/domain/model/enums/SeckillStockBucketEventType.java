package io.check.seckill.stock.domain.model.enums;

/**
 * @author check
 * @version 1.0.0
 * @description 库存分桶事件类型
 */
public enum SeckillStockBucketEventType {

    /**
     * 定义了三种状态枚举，分别表示禁用、启用和已安排。
     * 这些枚举值用于表示特定对象的不同状态，从而影响对象的行为或逻辑。
     */
    DISABLED(0),
    ENABLED(1),
    ARRANGED(2);


    public Integer getCode() {
        return code;
    }

    private final Integer code;

    SeckillStockBucketEventType(Integer code) {
        this.code = code;
    }
}

