package io.check.seckill.order.application.security;

import org.springframework.stereotype.Service;

/**
 * @author check
 * @version 1.0.0
 * @description 模拟风控
 */
@Service
public class DefaultSecurityService implements SecurityService{
    @Override
    public boolean securityPolicy(Long userId) {
        return true;
    }
}
