package io.check.seckill.application.service;

import io.check.seckill.domain.model.SeckillUser;

/**
 * @author check
 * @version 1.0.0
 * @description 用户
 */
public interface SeckillUserService {

    /**
     * 根据用户名获取用户信息
     */
    SeckillUser getSeckillUserByUserName(String userName);

    /**
     * 登录方法
     * @param userName
     * @param password
     * @return
     */
    String login(String userName, String password);
}
