package io.check.seckill.application.service.impl;

import io.check.seckill.application.service.RedisService;
import io.check.seckill.application.service.SeckillUserService;
import io.check.seckill.domain.code.HttpCode;
import io.check.seckill.domain.constants.SeckillConstants;
import io.check.seckill.domain.exception.SeckillException;
import io.check.seckill.domain.model.SeckillUser;
import io.check.seckill.domain.repository.SeckillUserRepository;
import io.check.seckill.infrastructure.shiro.utils.CommonsUtils;
import io.check.seckill.infrastructure.shiro.utils.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author check
 * @version 1.0.0
 * @description 用户Service
 */
@Service
public class SeckillUserServiceImpl implements SeckillUserService {

    @Autowired
    private SeckillUserRepository seckillUserRepository;

    @Autowired
    private RedisService redisService;

    @Override
    public SeckillUser getSeckillUserByUserName(String userName) {
        return seckillUserRepository.getSeckillUserByUserName(userName);
    }

    @Override
    public String login(String userName, String password) {
        if (StringUtils.isEmpty(userName)){
            throw new SeckillException(HttpCode.USERNAME_IS_NULL);
        }
        if (StringUtils.isEmpty(password)){
            throw new SeckillException(HttpCode.PASSWORD_IS_NULL);
        }
        SeckillUser seckillUser = seckillUserRepository.getSeckillUserByUserName(userName);
        if (seckillUser == null){
            throw new SeckillException(HttpCode.USERNAME_IS_ERROR);
        }
        String paramsPassword = CommonsUtils.encryptPassword(password, userName);
        if (!paramsPassword.equals(seckillUser.getPassword())){
            throw new SeckillException(HttpCode.PASSWORD_IS_ERROR);
        }
        String token = JwtUtils.sign(seckillUser.getId());
        String key = SeckillConstants.getKey(
                SeckillConstants.USER_KEY_PREFIX, String.valueOf(seckillUser.getId()));
        //缓存到Redis
        redisService.set(key, seckillUser);
        return token;
    }
}

