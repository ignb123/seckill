package io.check.seckill.order.interfaces.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.model.dto.order.SeckillOrderSubmitDTO;
import io.check.seckill.common.response.ResponseMessage;
import io.check.seckill.common.response.ResponseMessageBuilder;
import io.check.seckill.order.application.model.command.SeckillOrderCommand;
import io.check.seckill.order.application.service.SeckillOrderService;
import io.check.seckill.order.application.service.SeckillSubmitOrderService;
import io.check.seckill.order.domain.model.entity.SeckillOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author check
 * @version 1.0.0
 * @description 订单
 */

@RestController
@RequestMapping(value = "/order")
public class SeckillOrderController {
    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private SeckillSubmitOrderService seckillSubmitOrderService;

    /**
     * 保存秒杀订单
     */
    @RequestMapping(value = "/saveSeckillOrder", method = {RequestMethod.GET,RequestMethod.POST})
    @SentinelResource(value = "SAVE-DATA-FLOW")
    public ResponseMessage<SeckillOrderSubmitDTO> saveSeckillOrder(@RequestAttribute Long userId, SeckillOrderCommand seckillOrderCommand){
        SeckillOrderSubmitDTO seckillOrderSubmitDTO = seckillSubmitOrderService.saveSeckillOrder(userId, seckillOrderCommand);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillOrderSubmitDTO);
    }
    /**
     * 获取用户维度的订单列表
     */
    @RequestMapping(value = "/getSeckillOrderByUserId", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<List<SeckillOrder>> getSeckillOrderByUserId(Long userId){
        List<SeckillOrder> seckillOrderList = seckillOrderService.getSeckillOrderByUserId(userId);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillOrderList);
    }

    /**
     * 获取商品维度的订单列表
     */
    @RequestMapping(value = "/getSeckillOrderByGoodsId", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<List<SeckillOrder>> getSeckillOrderByGoodsId(Long goodsId){
        List<SeckillOrder> seckillOrderList = seckillOrderService.getSeckillOrderByGoodsId(goodsId);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillOrderList);
    }
}
