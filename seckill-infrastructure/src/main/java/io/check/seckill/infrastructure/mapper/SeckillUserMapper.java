package io.check.seckill.infrastructure.mapper;

import io.check.seckill.domain.model.SeckillUser;
import org.apache.ibatis.annotations.Param;

/**
 * @author check
 * @version 1.0.0
 * @description 用户
 */
public interface SeckillUserMapper {

    /**
     * 根据用户名获取用户信息
     */
    SeckillUser getSeckillUserByUserName(@Param("userName") String userName);
}

