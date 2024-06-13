package io.check.seckill.interfaces.controller;

import io.check.seckill.application.service.SeckillGoodsService;
import io.check.seckill.domain.code.HttpCode;
import io.check.seckill.domain.model.dto.SeckillGoodsDTO;
import io.check.seckill.domain.model.entity.SeckillGoods;
import io.check.seckill.domain.response.ResponseMessage;
import io.check.seckill.domain.response.ResponseMessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(value = "/goods")
public class SeckillGoodsController {
    @Autowired
    private SeckillGoodsService seckillGoodsService;

    /**
     * 保存秒杀商品
     */
    @RequestMapping(value = "/saveSeckillGoods", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<String> saveSeckillActivityDTO(SeckillGoodsDTO seckillGoodsDTO){
        seckillGoodsService.saveSeckillGoods(seckillGoodsDTO);
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode());
    }

    /**
     * 获取商品详情
     */
    @RequestMapping(value = "/getSeckillGoodsId", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<SeckillGoods> getSeckillGoodsId(Long id) {
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode(), seckillGoodsService.getSeckillGoodsId(id));
    }

    /**
     * 获取商品详情(带缓存)
     */
    @RequestMapping(value = "/getSeckillGoods", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<SeckillGoodsDTO> getSeckillGoods(Long id, Long version){
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode(), seckillGoodsService.getSeckillGoods(id, version));
    }

    /**
     * 获取商品列表
     */
    @RequestMapping(value = "/getSeckillGoodsByActivityId", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<List<SeckillGoods>> getSeckillGoodsByActivityId(Long activityId) {
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode(),
                seckillGoodsService.getSeckillGoodsByActivityId(activityId));
    }

    /**
     * 获取商品列表(带缓存)
     */
    @RequestMapping(value = "/getSeckillGoodsList", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<List<SeckillGoodsDTO>> getSeckillGoodsByActivityId(Long activityId, Long version){
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode(), seckillGoodsService.getSeckillGoodsList(activityId, version));
    }

    /**
     * 更新商品状态
     */
    @RequestMapping(value = "/updateStatus", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<String> updateStatus(Integer status, Long id) {
        seckillGoodsService.updateStatus(status, id);
        return ResponseMessageBuilder.build(HttpCode.SUCCESS.getCode());
    }
}
