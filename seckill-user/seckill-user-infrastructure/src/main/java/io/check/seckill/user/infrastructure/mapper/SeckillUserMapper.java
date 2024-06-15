package io.check.seckill.user.infrastructure.mapper;

import io.check.seckill.user.domain.model.entity.SeckillUser;
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
