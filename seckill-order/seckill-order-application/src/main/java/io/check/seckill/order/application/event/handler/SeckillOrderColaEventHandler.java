package io.check.seckill.order.application.event.handler;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.event.EventHandler;
import com.alibaba.cola.event.EventHandlerI;
import com.alibaba.fastjson.JSON;
import io.check.seckill.order.domain.event.SeckillOrderEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * @author check
 * @version 1.0.0
 * @description 订单事件处理器
 */
@EventHandler
@ConditionalOnProperty(name = "message.mq.type", havingValue = "cola")
public class SeckillOrderColaEventHandler implements EventHandlerI<Response, SeckillOrderEvent> {
    private final Logger logger = LoggerFactory.getLogger(SeckillOrderColaEventHandler.class);
    @Override
    public Response execute(SeckillOrderEvent seckillOrderEvent) {
        logger.info("cola|orderEvent|接收订单事件|{}", JSON.toJSON(seckillOrderEvent));
        if (seckillOrderEvent == null || seckillOrderEvent.getId() == null){
            logger.info("cola|orderEvent|订单参数错误");
            return Response.buildSuccess();
        }
        return Response.buildSuccess();
    }
}
