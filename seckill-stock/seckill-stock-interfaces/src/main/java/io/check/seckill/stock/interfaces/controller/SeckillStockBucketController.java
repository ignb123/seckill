package io.check.seckill.stock.interfaces.controller;

import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.response.ResponseMessage;
import io.check.seckill.common.response.ResponseMessageBuilder;
import io.check.seckill.stock.application.model.command.SeckillStockBucketWrapperCommand;
import io.check.seckill.stock.application.model.dto.SeckillStockBucketDTO;
import io.check.seckill.stock.application.service.SeckillStockBucketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author check
 * @version 1.0.0
 * @description 分桶库存
 */
@RestController
@RequestMapping(value = "/stock/bucket")
public class SeckillStockBucketController {
    @Autowired
    private SeckillStockBucketService seckillStockBucketService;

    /**
     * 库存分桶
     */
    @RequestMapping(value = "/arrangeStockBuckets", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<String> arrangeStockBuckets(@RequestAttribute Long userId, @RequestBody SeckillStockBucketWrapperCommand seckillStockCommond){
        seckillStockBucketService.arrangeStockBuckets(userId, seckillStockCommond);
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode());
    }

    /**
     * 获取库存分桶数据
     */
    @RequestMapping(value = "/getTotalStockBuckets", method = {RequestMethod.GET,RequestMethod.POST})
    public ResponseMessage<SeckillStockBucketDTO> getTotalStockBuckets(Long goodsId, Long version){
        return ResponseMessageBuilder.build(ErrorCode.SUCCESS.getCode(), seckillStockBucketService.getTotalStockBuckets(goodsId, version));
    }

}
