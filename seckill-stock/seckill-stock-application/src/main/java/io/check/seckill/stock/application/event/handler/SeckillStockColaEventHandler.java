package io.check.seckill.stock.application.event.handler;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.event.EventHandler;
import com.alibaba.cola.event.EventHandlerI;
import com.alibaba.fastjson.JSON;
import io.check.seckill.stock.application.cache.SeckillStockBucketCacheService;
import io.check.seckill.stock.domain.event.SeckillStockBucketEvent;
import io.check.seckill.stock.domain.model.enums.SeckillStockBucketEventType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * @author check
 * @version 1.0.0
 * @description 基于Cola的库存事件处理器
 */
@EventHandler
@ConditionalOnProperty(name = "message.mq.type", havingValue = "cola")
public class SeckillStockColaEventHandler implements EventHandlerI<Response, SeckillStockBucketEvent> {
    private final Logger logger = LoggerFactory.getLogger(SeckillStockColaEventHandler.class);
    @Autowired
    private SeckillStockBucketCacheService seckillStockBucketCacheService;

    @Override
    public Response execute(SeckillStockBucketEvent seckillStockBucketEvent) {
        logger.info("cola|stockEvent|接收库存事件|{}", JSON.toJSON(seckillStockBucketEvent));
        if (seckillStockBucketEvent == null || seckillStockBucketEvent.getId() == null){
            logger.info("cola|stockEvent|订单参数错误");
            return Response.buildSuccess();
        }
        //开启了库存分桶，就更新缓存数据
        if (SeckillStockBucketEventType.ENABLED.getCode().equals(seckillStockBucketEvent.getStatus())){
            seckillStockBucketCacheService.tryUpdateSeckillStockBucketCacheByLock(seckillStockBucketEvent.getId(), false);
        }

        return Response.buildSuccess();
    }
}
