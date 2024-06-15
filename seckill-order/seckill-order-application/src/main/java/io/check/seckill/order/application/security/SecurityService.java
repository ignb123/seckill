package io.check.seckill.order.application.security;

/**
 * @author binghe(微信 : hacker_binghe)
 * @version 1.0.0
 * @description 模拟风控服务
 */
public interface SecurityService {

    /**
     * 对用户进行风控处理
     */
    boolean securityPolicy(Long userId);
}
