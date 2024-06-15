package io.check.seckill.user.application.service;

/**
 * @author check
 * @version 1.0.0
 * @description 用户
 */
public interface SeckillUserService {

    /**
     * 登录
     */
    String login(String userName, String password);
}
