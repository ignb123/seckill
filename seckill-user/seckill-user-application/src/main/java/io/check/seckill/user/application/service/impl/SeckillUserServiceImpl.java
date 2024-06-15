package io.check.seckill.user.application.service.impl;

import io.check.seckill.common.cache.distribute.DistributedCacheService;
import io.check.seckill.common.constants.SeckillConstants;
import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.exception.SeckillException;
import io.check.seckill.common.shiro.utils.CommonsUtils;
import io.check.seckill.common.shiro.utils.JwtUtils;
import io.check.seckill.user.application.service.SeckillUserService;
import io.check.seckill.user.domain.model.entity.SeckillUser;
import io.check.seckill.user.domain.repository.SeckillUserRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    private DistributedCacheService distributedCacheService;

    @Override
    public String login(String userName, String password) {
        if (StringUtils.isEmpty(userName)){
            throw new SeckillException(ErrorCode.USERNAME_IS_NULL);
        }
        if (StringUtils.isEmpty(password)){
            throw new SeckillException(ErrorCode.PASSWORD_IS_NULL);
        }
        SeckillUser seckillUser = seckillUserRepository.getSeckillUserByUserName(userName);
        if (seckillUser == null){
            throw new SeckillException(ErrorCode.USERNAME_IS_ERROR);
        }
        String paramsPassword = CommonsUtils.encryptPassword(password, userName);
        if (!paramsPassword.equals(seckillUser.getPassword())){
            throw new SeckillException(ErrorCode.PASSWORD_IS_ERROR);
        }
        String token = JwtUtils.sign(seckillUser.getId());
        String key = SeckillConstants.getKey(SeckillConstants.USER_KEY_PREFIX, String.valueOf(seckillUser.getId()));
        //缓存到Redis
        distributedCacheService.put(key, seckillUser);
        //返回Token
        return token;
    }

}
