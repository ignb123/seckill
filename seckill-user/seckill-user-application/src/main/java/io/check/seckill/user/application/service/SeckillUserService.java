package io.check.seckill.user.application.service;

import io.check.seckill.user.domain.model.entity.SeckillUser;

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

    /**
     * 根据用户名获取用户信息
     */
    SeckillUser getSeckillUserByUserName(String userName);

}
