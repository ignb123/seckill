package io.check.seckill.activity.application.event.handler;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import io.check.seckill.activity.application.cache.service.SeckillActivityCacheService;
import io.check.seckill.activity.application.cache.service.SeckillActivityListCacheService;
import io.check.seckill.activity.domain.event.SeckillActivityEvent;
import io.check.seckill.common.constants.SeckillConstants;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "message.mq.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = SeckillConstants.EVENT_ACTIVITY_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_EVENT_ROCKETMQ_ACTIVITY)
public class SeckillActivityRocketMQEventHandler implements RocketMQListener<String> {

    private final Logger logger = LoggerFactory.getLogger(SeckillActivityRocketMQEventHandler.class);

    @Autowired
    private SeckillActivityCacheService seckillActivityCacheService;

    @Autowired
    private SeckillActivityListCacheService seckillActivityListCacheService;
    @Override
    public void onMessage(String message) {
        logger.info("rocketmq|activityEvent|接收活动事件|{}", message);
        if (StrUtil.isEmpty(message)){
            logger.info("activityEvent|事件参数错误" );
            return;
        }
        SeckillActivityEvent seckillActivityEvent = this.getEventMessage(message);
        seckillActivityCacheService.tryUpdateSeckillActivityCacheByLock(seckillActivityEvent.getId(), false);
        seckillActivityListCacheService.tryUpdateSeckillActivityCacheByLock(seckillActivityEvent.getStatus(), false);
    }

    private SeckillActivityEvent getEventMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String eventStr = jsonObject.getString(SeckillConstants.MSG_KEY);
        return JSONObject.parseObject(eventStr, SeckillActivityEvent.class);
    }
}
