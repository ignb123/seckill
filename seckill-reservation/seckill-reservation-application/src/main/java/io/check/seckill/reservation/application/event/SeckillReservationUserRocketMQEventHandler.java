package io.check.seckill.reservation.application.event;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.check.seckill.common.constants.SeckillConstants;
import io.check.seckill.common.model.enums.SeckillReservationUserStatus;
import io.check.seckill.reservation.application.cache.SeckillReservationUserCacheService;
import io.check.seckill.reservation.domain.event.SeckillReservationUserEvent;
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
@RocketMQMessageListener(consumerGroup = SeckillConstants.EVENT_RESERVATION_USER_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_EVENT_ROCKETMQ_RESERVATION_USER)
public class SeckillReservationUserRocketMQEventHandler implements RocketMQListener<String> {
    private final Logger logger = LoggerFactory.getLogger(SeckillReservationUserRocketMQEventHandler.class);
    @Autowired
    private SeckillReservationUserCacheService seckillReservationUserCacheService;
    @Override
    public void onMessage(String message) {
        logger.info("rocketmq|reservationUserEvent|接收秒杀品预约事件|{}", message);
        if (StrUtil.isEmpty(message)){
            logger.info("rocketmq|reservationUserEvent|接收秒杀品预约事件参数错误");
            return;
        }
        SeckillReservationUserEvent seckillReservationUserEvent = this.getEventMessage(message);
        //获取的数据为空
        if (seckillReservationUserEvent == null || seckillReservationUserEvent.getId() == null || seckillReservationUserEvent.getGoodsId() == null){
            logger.info("rocketmq|reservationUserEvent|接收秒杀品预约事件参数错误");
            return;
        }
        logger.info("rocketmq|reservationUserEvent|接收秒杀品预约事件解析后的数据|{}", JSON.toJSONString(seckillReservationUserEvent));
        if (seckillReservationUserEvent.getStatus() != null && SeckillReservationUserStatus.isDeleted(seckillReservationUserEvent.getStatus())){
            logger.info("rocketmq|reservationUserEvent|删除缓存中的数据|{}", JSON.toJSONString(seckillReservationUserEvent));
            seckillReservationUserCacheService.deleteSeckillReservationUserFromCache(seckillReservationUserEvent);
        }else{
            logger.info("rocketmq|reservationUserEvent|更新缓存中的数据|{}", JSON.toJSONString(seckillReservationUserEvent));
            seckillReservationUserCacheService.tryUpdateSeckillReservationUserCacheByUserIdAndGoodsId(seckillReservationUserEvent.getId(), seckillReservationUserEvent.getGoodsId(), false);
            seckillReservationUserCacheService.tryUpdateGoodsListCacheByUserId(seckillReservationUserEvent.getId(), false);
            seckillReservationUserCacheService.tryUpdatetUserListCacheByGoodsId(seckillReservationUserEvent.getGoodsId(), false);
        }
    }

    private SeckillReservationUserEvent getEventMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String eventStr = jsonObject.getString(SeckillConstants.MSG_KEY);
        return JSONObject.parseObject(eventStr, SeckillReservationUserEvent.class);
    }
}

