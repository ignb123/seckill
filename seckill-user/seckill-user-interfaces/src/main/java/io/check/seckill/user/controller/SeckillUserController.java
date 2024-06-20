package io.check.seckill.user.controller;

import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.model.dto.user.SeckillUserDTO;
import io.check.seckill.common.response.ResponseMessage;
import io.check.seckill.common.response.ResponseMessageBuilder;
import io.check.seckill.ratelimiter.concurrent.annotation.ConcurrentRateLimiter;
import io.check.seckill.user.application.service.SeckillUserService;
import io.check.seckill.user.domain.model.entity.SeckillUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author check
 * @version 1.0.0
 * @description 用户登录
 */
@RestController
@RequestMapping(value = "/user")
public class SeckillUserController {

    @Autowired
    private SeckillUserService seckillUserService;
    /**
     * 登录系统
     */
    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseMessage<String> login(@RequestBody SeckillUserDTO seckillUserDTO){
       return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillUserService.login(seckillUserDTO.getUserName(), seckillUserDTO.getPassword()));
    }

    /**
     * 获取用户信息
     */
    @RequestMapping(value = "/get", method = {RequestMethod.GET, RequestMethod.POST})
//    @SeckillRateLimiter(permitsPerSecond = 1, timeout = 0)
    @ConcurrentRateLimiter(name = "checkRateLimiter", queueCapacity = 0)
    public ResponseMessage<SeckillUser> get(@RequestParam String username){
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillUserService.getSeckillUserByUserName(username));
    }

}
