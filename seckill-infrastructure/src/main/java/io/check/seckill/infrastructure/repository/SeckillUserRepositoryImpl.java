package io.check.seckill.infrastructure.repository;

import io.check.seckill.domain.model.entity.SeckillUser;
import io.check.seckill.domain.repository.SeckillUserRepository;
import io.check.seckill.infrastructure.mapper.SeckillUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author check
 * @version 1.0.0
 * @description 用户
 */
@Component
public class SeckillUserRepositoryImpl implements SeckillUserRepository {

    @Autowired
    private SeckillUserMapper seckillUserMapper;

    @Override
    public SeckillUser getSeckillUserByUserName(String userName) {
        return seckillUserMapper.getSeckillUserByUserName(userName);
    }
}

