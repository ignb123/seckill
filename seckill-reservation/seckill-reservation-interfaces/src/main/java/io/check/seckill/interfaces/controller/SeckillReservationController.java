package io.check.seckill.interfaces.controller;

import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.response.ResponseMessage;
import io.check.seckill.common.response.ResponseMessageBuilder;
import io.check.seckill.reservation.application.command.SeckillReservationConfigCommand;
import io.check.seckill.reservation.application.command.SeckillReservationUserCommand;
import io.check.seckill.reservation.application.service.SeckillReservationService;
import io.check.seckill.reservation.domain.model.entity.SeckillReservationConfig;
import io.check.seckill.reservation.domain.model.entity.SeckillReservationUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author check
 * @version 1.0.0
 * @description SeckillReservationController
 */

@RestController
@RequestMapping(value = "/reservation")
public class SeckillReservationController {

    @Autowired
    private SeckillReservationService seckillReservationService;

    /**
     * 保存预约配置信息
     */
    @RequestMapping(value = "/config/saveSeckillReservationConfig", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<String> saveSeckillReservationConfig(@RequestBody SeckillReservationConfigCommand seckillReservationConfigCommand){
        seckillReservationService.saveSeckillReservationConfig(seckillReservationConfigCommand);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode());
    }

    /**
     * 更新预约配置信息
     */
    @RequestMapping(value = "/config/updateSeckillReservationConfig", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<String> updateSeckillReservationConfig(@RequestBody SeckillReservationConfigCommand seckillReservationConfigCommand){
        seckillReservationService.updateSeckillReservationConfig(seckillReservationConfigCommand);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode());
    }

    /**
     * 更新预约配置状态
     */
    @RequestMapping(value = "/config/updateConfigStatus", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<String> updateConfigStatus(Integer status, Long goodsId){
        seckillReservationService.updateConfigStatus(status, goodsId);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode());
    }

    /**
     * 获取预约配置列表
     */
    @RequestMapping(value = "/config/getConfigList", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<List<SeckillReservationConfig>> getConfigList(Long version){
        List<SeckillReservationConfig> serviceConfigList = seckillReservationService.getConfigList(version);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), serviceConfigList);
    }

    /**
     * 获取预约配置详情
     */
    @RequestMapping(value = "/config/getConfigDetail", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<SeckillReservationConfig> getConfigDetail(Long goodsId, Long version){
        SeckillReservationConfig serviceConfigDetail = seckillReservationService.getConfigDetail(goodsId, version);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), serviceConfigDetail);
    }

    /**
     * 根据商品id查看预约用户列表
     */
    @RequestMapping(value = "/user/getUserListByGoodsId", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<List<SeckillReservationUser>> getUserListByGoodsId(Long goodsId, Long version){
        List<SeckillReservationUser> serviceUserList = seckillReservationService.getUserListByGoodsId(goodsId, version);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), serviceUserList);
    }

    /**
     * 根据用户id查看预约的商品列表
     */
    @RequestMapping(value = "/user/getGoodsListByUserId", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<List<SeckillReservationUser>> getGoodsListByUserId(Long userId, Long version){
        List<SeckillReservationUser> serviceUserList = seckillReservationService.getGoodsListByUserId(userId, version);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), serviceUserList);
    }

    /**
     * 预约秒杀商品
     */
    @RequestMapping(value = "/user/reserveGoods", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<String> reserveGoods(@RequestBody SeckillReservationUserCommand seckillReservationUserCommand){
        seckillReservationService.reserveGoods(seckillReservationUserCommand);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode());
    }
    /**
     * 取消预约秒杀商品
     */
    @RequestMapping(value = "/user/cancelReserveGoods", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<String> cancelReserveGoods(@RequestBody SeckillReservationUserCommand seckillReservationUserCommand){
        seckillReservationService.cancelReserveGoods(seckillReservationUserCommand);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode());
    }

    /**
     * 获取用户预约的某个商品信息
     */
    @RequestMapping(value = "/user/getSeckillReservationUser", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<SeckillReservationUser> getSeckillReservationUser(@RequestBody SeckillReservationUserCommand seckillReservationUserCommand){
        SeckillReservationUser seckillReservationUser = seckillReservationService.getSeckillReservationUser(seckillReservationUserCommand);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillReservationUser);
    }
}
