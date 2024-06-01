package io.check.seckill.interfaces.controller;


import io.check.seckill.application.service.SeckillUserService;
import io.check.seckill.domain.code.HttpCode;
import io.check.seckill.domain.dto.SeckillUserDTO;
import io.check.seckill.domain.model.SeckillUser;
import io.check.seckill.domain.response.ResponseMessage;
import io.check.seckill.domain.response.ResponseMessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author check
 * @version 1.0.0
 * @description 用户相关的接口
 */
@RestController
@RequestMapping(value = "/user")
/**
 * 为控制器或方法添加跨源资源共享（CORS）配置的注解。
 * 允许所有来源的跨域请求，并允许所有请求头和凭据。
 *
 * @param allowCredentials 指定是否允许用户代理发送和接收凭据（如cookies、HTTP认证及Web Storage等）。
 *                         设置为"true"表示服务器允许请求携带凭证。
 * @param allowedHeaders 指定在请求或响应中能够被客户端浏览器暴露的头部。
 *                       设置为"*"表示允许所有头部信息。
 * @param originPatterns 指定允许请求来源的模式。可以使用通配符 "*" 匹配多个来源。
 *                       设置为"*"表示允许所有来源的请求。
 */
@CrossOrigin(allowCredentials = "true", allowedHeaders = "*", originPatterns = "*")

public class SeckillUserController {

    @Autowired
    private SeckillUserService seckillUserService;
    /**
     * 测试系统
     */
    @RequestMapping(value = "/get", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseMessage<SeckillUser> getUser(@RequestParam(value = "username") String userName){
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode(),
                seckillUserService.getSeckillUserByUserName(userName));
    }

    @RequestMapping(value = "/login", method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseMessage<String> login(@RequestBody SeckillUserDTO seckillUserDTO){
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode(), seckillUserService.login(seckillUserDTO.getUserName(), seckillUserDTO.getPassword()));
    }
}

