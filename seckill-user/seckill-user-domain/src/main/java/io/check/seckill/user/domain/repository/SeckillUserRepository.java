package io.check.seckill.user.domain.repository;

import io.check.seckill.user.domain.model.entity.SeckillUser;

/**
 * @author check
 * @version 1.0.0
 * @description 用户
 */
public interface SeckillUserRepository {

    /**
     * 根据用户名获取用户信息
     */
    SeckillUser getSeckillUserByUserName(String userName);
}
