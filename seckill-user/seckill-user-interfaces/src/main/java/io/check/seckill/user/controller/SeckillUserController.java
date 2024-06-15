package io.check.seckill.user.controller;

import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.model.dto.SeckillUserDTO;
import io.check.seckill.common.response.ResponseMessage;
import io.check.seckill.common.response.ResponseMessageBuilder;
import io.check.seckill.user.application.service.SeckillUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

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
}
