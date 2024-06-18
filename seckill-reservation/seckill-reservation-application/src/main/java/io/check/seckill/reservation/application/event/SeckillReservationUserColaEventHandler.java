package io.check.seckill.reservation.application.event;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.event.EventHandler;
import com.alibaba.cola.event.EventHandlerI;
import com.alibaba.fastjson.JSON;
import io.check.seckill.common.model.enums.SeckillReservationUserStatus;
import io.check.seckill.reservation.application.cache.SeckillReservationUserCacheService;
import io.check.seckill.reservation.domain.event.SeckillReservationUserEvent;
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
public class SeckillReservationUserColaEventHandler implements EventHandlerI<Response, SeckillReservationUserEvent> {
    private final Logger logger = LoggerFactory.getLogger(SeckillReservationUserColaEventHandler.class);
    @Autowired
    private SeckillReservationUserCacheService seckillReservationUserCacheService;
    @Override
    public Response execute(SeckillReservationUserEvent seckillReservationUserEvent) {
        if (seckillReservationUserEvent == null || seckillReservationUserEvent.getId() == null || seckillReservationUserEvent.getGoodsId() == null){
            logger.info("cola|reservationUserEvent|接收秒杀品预约事件参数错误");
            return Response.buildSuccess();
        }
        logger.info("cola|reservationUserEvent|接收秒杀品预约事件|{}", JSON.toJSON(seckillReservationUserEvent));

        if (seckillReservationUserEvent.getStatus() != null && SeckillReservationUserStatus.isDeleted(seckillReservationUserEvent.getStatus())){
            logger.info("cola|reservationUserEvent|删除缓存中的数据|{}", JSON.toJSONString(seckillReservationUserEvent));
            seckillReservationUserCacheService.deleteSeckillReservationUserFromCache(seckillReservationUserEvent);
        }else{
            logger.info("cola|reservationUserEvent|更新缓存中的数据|{}", JSON.toJSONString(seckillReservationUserEvent));
            seckillReservationUserCacheService.tryUpdateSeckillReservationUserCacheByUserIdAndGoodsId(seckillReservationUserEvent.getId(), seckillReservationUserEvent.getGoodsId(), false);
            seckillReservationUserCacheService.tryUpdateGoodsListCacheByUserId(seckillReservationUserEvent.getId(), false);
            seckillReservationUserCacheService.tryUpdatetUserListCacheByGoodsId(seckillReservationUserEvent.getGoodsId(), false);
        }
        return Response.buildSuccess();
    }
}
