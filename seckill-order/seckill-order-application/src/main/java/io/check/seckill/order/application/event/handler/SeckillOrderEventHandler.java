package io.check.seckill.order.application.event.handler;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.event.EventHandler;
import com.alibaba.cola.event.EventHandlerI;
import com.alibaba.fastjson.JSON;
import io.check.seckill.order.domain.event.SeckillOrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author check
 * @version 1.0.0
 * @description 订单事件处理器
 */
@EventHandler
public class SeckillOrderEventHandler implements EventHandlerI<Response, SeckillOrderEvent> {
    private final Logger logger = LoggerFactory.getLogger(SeckillOrderEventHandler.class);
    @Override
    public Response execute(SeckillOrderEvent seckillOrderEvent) {
        logger.info("orderEvent|接收订单事件|{}", JSON.toJSON(seckillOrderEvent));
        if (seckillOrderEvent.getId() == null){
            logger.info("orderEvent|订单参数错误");
            return Response.buildSuccess();
        }
        return Response.buildSuccess();
    }
}