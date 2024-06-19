package io.check.seckill.reservation.application.event;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.event.EventHandler;
import com.alibaba.cola.event.EventHandlerI;
import com.alibaba.fastjson.JSON;
import io.check.seckill.reservation.application.cache.SeckillReservationConfigCacheService;
import io.check.seckill.reservation.domain.event.SeckillReservationConfigEvent;
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
@ConditionalOnProperty(name = "message.mq.type", havingValue = "cola")
public class SeckillReservationConfigColaEventHandler implements EventHandlerI<Response, SeckillReservationConfigEvent> {
    private final Logger logger = LoggerFactory.getLogger(SeckillReservationConfigColaEventHandler.class);

    @Autowired
    private SeckillReservationConfigCacheService seckillReservationConfigCacheService;

    @Override
    public Response execute(SeckillReservationConfigEvent seckillReservationConfigEvent) {
        if (seckillReservationConfigEvent == null || seckillReservationConfigEvent.getId() == null){
            logger.info("cola|reservationConfigEvent|接收秒杀品预约配置事件参数错误");
            return Response.buildSuccess();
        }
        logger.info("cola|reservationConfigEvent|接收秒杀品预约配置事件|{}", JSON.toJSON(seckillReservationConfigEvent));
        seckillReservationConfigCacheService.tryUpdateSeckillReservationConfigCacheByLock(seckillReservationConfigEvent.getId(), false);
        seckillReservationConfigCacheService.tryUpdateSeckillReservationConfigListCacheByLock(false);
        return Response.buildSuccess();
    }
}
