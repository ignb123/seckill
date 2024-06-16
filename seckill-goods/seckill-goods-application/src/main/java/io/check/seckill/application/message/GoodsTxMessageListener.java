package io.check.seckill.application.message;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import io.check.seckill.application.service.SeckillGoodsService;
import io.check.seckill.common.constants.SeckillConstants;
import io.check.seckill.common.model.message.TxMessage;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(consumerGroup = SeckillConstants.TX_GOODS_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_TX_MSG)
public class GoodsTxMessageListener implements RocketMQListener<String> {

    private final Logger logger = LoggerFactory.getLogger(GoodsTxMessageListener.class);
    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Override
    public void onMessage(String message) {
        if (StrUtil.isEmpty(message)){
            return;
        }
        logger.info("秒杀商品微服务开始消费事务消息:{}", message);
        TxMessage txMessage = this.getTxMessage(message);
        //如果协调的异常信息字段为false，订单微服务没有抛出异常，则处理库存信息
        if (BooleanUtil.isFalse(txMessage.getException())){
            seckillGoodsService.updateAvailableStock(txMessage);
        }
    }

    private TxMessage getTxMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String txStr = jsonObject.getString(SeckillConstants.TX_MSG_KEY);
        return JSONObject.parseObject(txStr, TxMessage.class);
    }
}
