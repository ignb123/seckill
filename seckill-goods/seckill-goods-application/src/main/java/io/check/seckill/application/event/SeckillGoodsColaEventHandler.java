package io.check.seckill.application.event;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.event.EventHandler;
import com.alibaba.cola.event.EventHandlerI;
import com.alibaba.fastjson.JSON;
import io.check.seckill.application.cache.service.SeckillGoodsCacheService;
import io.check.seckill.application.cache.service.SeckillGoodsListCacheService;
import io.check.seckill.goods.domain.event.SeckillGoodsEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * @author check
 * @version 1.0.0
 * @description 商品事件处理器
 */
@EventHandler
@ConditionalOnProperty(name = "event.publish.type", havingValue = "cola")
public class SeckillGoodsColaEventHandler implements EventHandlerI<Response, SeckillGoodsEvent> {
    private final Logger logger = LoggerFactory.getLogger(SeckillGoodsColaEventHandler.class);

    @Autowired
    private SeckillGoodsCacheService seckillGoodsCacheService;

    @Autowired
    private SeckillGoodsListCacheService seckillGoodsListCacheService;

    @Override
    public Response execute(SeckillGoodsEvent seckillGoodsEvent) {
        logger.info("cola|goodsEvent|接收秒杀品事件|{}", JSON.toJSON(seckillGoodsEvent));
        if (seckillGoodsEvent.getId() == null){
            logger.info("cola|goodsEvent|接收秒杀品事件参数错误");
            return Response.buildSuccess();
        }
        seckillGoodsCacheService.tryUpdateSeckillGoodsCacheByLock(seckillGoodsEvent.getId(), false);
        seckillGoodsListCacheService.tryUpdateSeckillGoodsCacheByLock(seckillGoodsEvent.getActivityId(), false);
        return Response.buildSuccess();
    }
}
