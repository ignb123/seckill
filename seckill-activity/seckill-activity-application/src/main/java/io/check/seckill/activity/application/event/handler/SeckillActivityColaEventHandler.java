package io.check.seckill.activity.application.event.handler;

import com.alibaba.cola.dto.Response;
import com.alibaba.cola.event.EventHandler;
import com.alibaba.cola.event.EventHandlerI;
import com.alibaba.fastjson.JSON;
import io.check.seckill.activity.application.cache.service.SeckillActivityCacheService;
import io.check.seckill.activity.application.cache.service.SeckillActivityListCacheService;
import io.check.seckill.activity.domain.event.SeckillActivityEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

@EventHandler
@ConditionalOnProperty(name = "message.mq.type", havingValue = "cola")
public class SeckillActivityColaEventHandler implements EventHandlerI<Response, SeckillActivityEvent> {

    private final Logger logger = LoggerFactory.getLogger(SeckillActivityColaEventHandler.class);

    @Autowired
    private SeckillActivityCacheService seckillActivityCacheService;

    @Autowired
    private SeckillActivityListCacheService seckillActivityListCacheService;

    @Override
    public Response execute(SeckillActivityEvent seckillActivityEvent) {
        logger.info("cola|activityEvent|接收活动事件|{}", JSON.toJSON(seckillActivityEvent));
        if(seckillActivityEvent == null){
            logger.info("cola|activityEvent|事件参数错误" );
            return Response.buildSuccess();
        }
        seckillActivityCacheService.tryUpdateSeckillActivityCacheByLock(seckillActivityEvent.getId(), false);
        seckillActivityListCacheService.tryUpdateSeckillActivityCacheByLock(seckillActivityEvent.getStatus(), false);
        return Response.buildSuccess();
    }
}
