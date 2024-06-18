package io.check.seckill.goods.interfaces.controller;

import io.check.seckill.application.command.SeckillGoodsCommond;
import io.check.seckill.application.service.SeckillGoodsService;
import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.model.dto.goods.SeckillGoodsDTO;
import io.check.seckill.common.response.ResponseMessage;
import io.check.seckill.common.response.ResponseMessageBuilder;
import io.check.seckill.goods.domain.model.entity.SeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author check
 * @version 1.0.0
 * @description 商品接口
 */
@RestController
@RequestMapping(value = "/goods")
public class SeckillGoodsController /*extends BaseController*/ {

    @Autowired
    private SeckillGoodsService seckillGoodsService;
    /**
     * 保存秒杀商品
     */
    @RequestMapping(value = "/saveSeckillGoods", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<String> saveSeckillActivityDTO(SeckillGoodsCommond seckillGoodsCommond){
        seckillGoodsService.saveSeckillGoods(seckillGoodsCommond);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode());
    }

    /**
     * 获取商品详情
     */
    @RequestMapping(value = "/getSeckillGoodsId", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<SeckillGoods> getSeckillGoodsId(Long id){
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillGoodsService.getSeckillGoodsId(id));
    }

    /**
     * 获取商品详情（带缓存）
     */
    @RequestMapping(value = "/getSeckillGoods", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<SeckillGoodsDTO> getSeckillGoods(Long id, Long version){
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillGoodsService.getSeckillGoods(id, version));
    }

    /**
     * 获取商品列表
     */
    @RequestMapping(value = "/getSeckillGoodsByActivityId", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<List<SeckillGoods>> getSeckillGoodsByActivityId(Long activityId){
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillGoodsService.getSeckillGoodsByActivityId(activityId));
    }
    /**
     * 获取商品列表(带缓存)
     */
    @RequestMapping(value = "/getSeckillGoodsList", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<List<SeckillGoodsDTO>> getSeckillGoodsByActivityId(Long activityId, Long version){
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillGoodsService.getSeckillGoodsList(activityId, version));
    }

    /**
     * 更新商品状态
     */
    @RequestMapping(value = "/updateStatus", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<String> updateStatus(Integer status, Long id){
        seckillGoodsService.updateStatus(status, id);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode());
    }
}
