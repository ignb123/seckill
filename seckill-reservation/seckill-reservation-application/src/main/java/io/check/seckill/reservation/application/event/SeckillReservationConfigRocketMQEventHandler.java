package io.check.seckill.reservation.application.event;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.check.seckill.common.constants.SeckillConstants;
import io.check.seckill.reservation.application.cache.SeckillReservationConfigCacheService;
import io.check.seckill.reservation.domain.event.SeckillReservationConfigEvent;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * @author check
 * @version 1.0.0
 * @description 基于RocketMQ的商品事件处理器
 */
@Component
@ConditionalOnProperty(name = "message.mq.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = SeckillConstants.EVENT_RESERVATION_CONFIG_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_EVENT_ROCKETMQ_RESERVATION_CONFIG)
public class SeckillReservationConfigRocketMQEventHandler implements RocketMQListener<String> {
    private final Logger logger = LoggerFactory.getLogger(SeckillReservationConfigRocketMQEventHandler.class);
    @Autowired
    private SeckillReservationConfigCacheService seckillReservationConfigCacheService;
    @Override
    public void onMessage(String message) {
        logger.info("rocketmq|reservationConfigEvent|接收秒杀品预约配置事件|{}", message);
        if (StrUtil.isEmpty(message)){
            logger.info("rocketmq|reservationConfigEvent|接收秒杀品预约配置事件参数错误");
            return;
        }
        SeckillReservationConfigEvent seckillReservationConfigEvent = this.getEventMessage(message);
        if (seckillReservationConfigEvent == null){
            logger.info("rocketmq|reservationConfigEvent|解析后的数据为空");
            return;
        }
        logger.info("rocketmq|reservationConfigEvent|接收秒杀品预约配置事件解析后的数据|{}", JSON.toJSONString(seckillReservationConfigEvent));
        seckillReservationConfigCacheService.tryUpdateSeckillReservationConfigCacheByLock(seckillReservationConfigEvent.getId(), false);
        seckillReservationConfigCacheService.tryUpdateSeckillReservationConfigListCacheByLock(false);
    }

    private SeckillReservationConfigEvent getEventMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String eventStr = jsonObject.getString(SeckillConstants.MSG_KEY);
        return JSONObject.parseObject(eventStr, SeckillReservationConfigEvent.class);
    }
}
