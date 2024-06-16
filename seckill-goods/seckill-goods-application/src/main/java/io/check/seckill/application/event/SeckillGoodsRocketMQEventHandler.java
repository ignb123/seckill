package io.check.seckill.application.event;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import io.check.seckill.application.cache.service.SeckillGoodsCacheService;
import io.check.seckill.application.cache.service.SeckillGoodsListCacheService;
import io.check.seckill.common.constants.SeckillConstants;
import io.check.seckill.goods.domain.event.SeckillGoodsEvent;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "message.mq.type", havingValue = "rocketmq")
@RocketMQMessageListener(consumerGroup = SeckillConstants.EVENT_GOODS_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_EVENT_ROCKETMQ_GOODS)
public class SeckillGoodsRocketMQEventHandler implements RocketMQListener<String> {

    private final Logger logger = LoggerFactory.getLogger(SeckillGoodsRocketMQEventHandler.class);
    @Autowired
    private SeckillGoodsCacheService seckillGoodsCacheService;
    @Autowired
    private SeckillGoodsListCacheService seckillGoodsListCacheService;

    @Override
    public void onMessage(String message) {
        logger.info("rocketmq|goodsEvent|接收秒杀品事件|{}", message);
        if (StrUtil.isEmpty(message)){
            logger.info("rocketmq|goodsEvent|接收秒杀品事件参数错误" );
            return;
        }
        SeckillGoodsEvent seckillGoodsEvent = this.getEventMessage(message);
        seckillGoodsCacheService.tryUpdateSeckillGoodsCacheByLock(seckillGoodsEvent.getId(), false);
        seckillGoodsListCacheService.tryUpdateSeckillGoodsCacheByLock(seckillGoodsEvent.getActivityId(), false);
    }

    private SeckillGoodsEvent getEventMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String eventStr = jsonObject.getString(SeckillConstants.MSG_KEY);
        return JSONObject.parseObject(eventStr, SeckillGoodsEvent.class);
    }
}
