package io.check.seckill.stock.application.event.handler;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import io.check.seckill.common.constants.SeckillConstants;
import io.check.seckill.stock.application.cache.SeckillStockBucketCacheService;
import io.check.seckill.stock.domain.event.SeckillStockBucketEvent;
import io.check.seckill.stock.domain.model.enums.SeckillStockBucketEventType;
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
 * @description 基于RocketMQ的库存事件处理器
 */
@Component
@ConditionalOnProperty(name = "message.mq.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = SeckillConstants.EVENT_STOCK_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_EVENT_ROCKETMQ_STOCK)
public class SeckillStockRocketMQEventHandler implements RocketMQListener<String> {

    private final Logger logger = LoggerFactory.getLogger(SeckillStockRocketMQEventHandler.class);

    @Autowired
    private SeckillStockBucketCacheService seckillStockBucketCacheService;

    @Override
    public void onMessage(String message) {
        if (StrUtil.isEmpty(message)){
            logger.info("rocketmq|stockEvent|接收库存事件为空");
            return;
        }
        SeckillStockBucketEvent seckillStockBucketEvent = this.getEventMessage(message);
        if (seckillStockBucketEvent.getId() == null){
            logger.info("rocketmq|stockEvent|订单参数错误");
        }
        logger.info("rocketmq|stockEvent|接收订单事件|{}", JSON.toJSON(seckillStockBucketEvent));
        //开启了库存分桶，就更新缓存数据
        if (SeckillStockBucketEventType.ENABLED.getCode().equals(seckillStockBucketEvent.getStatus())){
            seckillStockBucketCacheService.tryUpdateSeckillStockBucketCacheByLock(seckillStockBucketEvent.getId(), false);
        }
    }

    private SeckillStockBucketEvent getEventMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String eventStr = jsonObject.getString(SeckillConstants.MSG_KEY);
        return JSONObject.parseObject(eventStr, SeckillStockBucketEvent.class);
    }
}
