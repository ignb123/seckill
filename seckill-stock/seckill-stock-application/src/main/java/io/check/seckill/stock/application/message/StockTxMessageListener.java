package io.check.seckill.stock.application.message;

import cn.hutool.core.util.BooleanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import io.check.seckill.common.constants.SeckillConstants;
import io.check.seckill.common.model.message.TxMessage;
import io.check.seckill.stock.application.service.SeckillStockBucketService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author check
 * @version 1.0.0
 * @description 商品微服务事务消息
 */
@Component
@RocketMQMessageListener(consumerGroup = SeckillConstants.TX_STOCK_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_BUCKET_TX_MSG)
public class StockTxMessageListener implements RocketMQListener<String> {
    private final Logger logger = LoggerFactory.getLogger(StockTxMessageListener.class);
    @Autowired
    private SeckillStockBucketService seckillStockBucketService;

    @Override
    public void onMessage(String message) {
        if (StrUtil.isEmpty(message)){
            return;
        }
        logger.info("秒杀库存微服务开始消费事务消息:{}", message);
        TxMessage txMessage = this.getTxMessage(message);
        //如果表示异常信息字段为false，订单微服务没有抛出异常，则处理库存信息
        if (BooleanUtil.isFalse(txMessage.getException())){
            seckillStockBucketService.decreaseStock(txMessage);
        }
    }

    private TxMessage getTxMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String txStr = jsonObject.getString(SeckillConstants.MSG_KEY);
        return JSONObject.parseObject(txStr, TxMessage.class);
    }

}
