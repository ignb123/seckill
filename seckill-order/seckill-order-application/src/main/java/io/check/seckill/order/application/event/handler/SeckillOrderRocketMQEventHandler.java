package io.check.seckill.order.application.event.handler;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.check.seckill.common.constants.SeckillConstants;
import io.check.seckill.order.domain.event.SeckillOrderEvent;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "event.publish.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = SeckillConstants.EVENT_ORDER_CONSUMER_GROUP,
        topic = SeckillConstants.TOPIC_EVENT_ROCKETMQ_ORDER)
public class SeckillOrderRocketMQEventHandler implements RocketMQListener<String> {

    private final Logger logger = LoggerFactory.getLogger(SeckillOrderRocketMQEventHandler.class);

    @Override
    public void onMessage(String message) {
        if (StrUtil.isEmpty(message)){
            logger.info("rocketmq|orderEvent|接收订单事件为空");
            return;
        }
        SeckillOrderEvent seckillOrderEvent = this.getEventMessage(message);
        if (seckillOrderEvent.getId() == null){
            logger.info("rocketmq|orderEvent|订单参数错误");
        }
        logger.info("rocketmq|orderEvent|接收订单事件|{}", JSON.toJSON(seckillOrderEvent));
    }

    private SeckillOrderEvent getEventMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String eventStr = jsonObject.getString(SeckillConstants.EVENT_MSG_KEY);
        return JSONObject.parseObject(eventStr, SeckillOrderEvent.class);
    }
}
