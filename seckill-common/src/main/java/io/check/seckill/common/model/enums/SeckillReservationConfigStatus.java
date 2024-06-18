package io.check.seckill.common.model.enums;

/**
 * @author check
 * @version 1.0.0
 * @description 预约配置状态
 */
public enum SeckillReservationConfigStatus {

    /**
     * 文档已发布状态。
     * 值为0，表示文档处于已发布状态，可能对公众可见。
     */
    PUBLISHED(0),

    /**
     * 文档在线状态。
     * 值为1，表示文档处于在线状态，可能意味着它正在被使用或访问。
     */
    ONLINE(1),

    /**
     * 文档离线状态。
     * 值为-1，表示文档处于离线状态，可能不可访问或已被隐藏。
     */
    OFFLINE(-1);

    private final Integer code;

    SeckillReservationConfigStatus(Integer code) {
        this.code = code;
    }

    public static boolean isOffline(Integer status) {
        return OFFLINE.getCode().equals(status);
    }

    public static boolean isPublished(Integer status) {
        return PUBLISHED.getCode().equals(status);
    }

    public static boolean isOnline(Integer status) {
        return ONLINE.getCode().equals(status);
    }

    public Integer getCode() {
        return code;
    }
}

