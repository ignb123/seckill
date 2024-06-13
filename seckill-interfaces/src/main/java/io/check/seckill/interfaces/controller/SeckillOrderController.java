package io.check.seckill.interfaces.controller;

import io.check.seckill.application.service.SeckillOrderService;
import io.check.seckill.domain.code.HttpCode;
import io.check.seckill.domain.model.dto.SeckillOrderDTO;
import io.check.seckill.domain.model.entity.SeckillOrder;
import io.check.seckill.domain.response.ResponseMessage;
import io.check.seckill.domain.response.ResponseMessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/order")
public class SeckillOrderController {

    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     * 保存秒杀订单
     */
    @RequestMapping(value = "/saveSeckillOrder", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<SeckillOrder> saveSeckillOrder(SeckillOrderDTO seckillOrderDTO){
        SeckillOrder seckillOrder = seckillOrderService.saveSeckillOrder(seckillOrderDTO);
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode());
    }

    /**
     * 获取用户维度的订单列表
     */
    @RequestMapping(value = "/getSeckillOrderByUserId", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<List<SeckillOrder>> getSeckillOrderByUserId(Long userId){
        List<SeckillOrder> seckillOrderList  = seckillOrderService.getSeckillOrderByUserId(userId);
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode(),seckillOrderList);
    }

    /**
     * 获取活动维度的订单列表
     */
    @RequestMapping(value = "/getSeckillOrderByActivityId", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<List<SeckillOrder>> getSeckillOrderByActivityId(Long activityId) {
        List<SeckillOrder> seckillOrderList = seckillOrderService.getSeckillOrderByActivityId(activityId);
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode(), seckillOrderList);
    }
}
