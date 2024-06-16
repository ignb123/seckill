package io.check.seckill.order.application.message;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import io.check.seckill.common.constants.SeckillConstants;
import io.check.seckill.common.model.message.ErrorMessage;
import io.check.seckill.order.application.service.SeckillOrderService;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RocketMQMessageListener(consumerGroup = SeckillConstants.TX_ORDER_CONSUMER_GROUP, topic = SeckillConstants.TOPIC_ERROR_MSG)
public class OrderErrorMessageListener implements RocketMQListener<String> {

    private final Logger logger = LoggerFactory.getLogger(OrderErrorMessageListener.class);
    @Autowired
    private SeckillOrderService seckillOrderService;

    @Override
    public void onMessage(String message) {
        logger.info("onMessage|秒杀订单微服务开始消费消息:{}", message);
        if (StrUtil.isEmpty(message)){
            return;
        }
        //删除数据库中对应的订单
        seckillOrderService.deleteOrder(this.getErrorMessage(message));
    }

    private ErrorMessage getErrorMessage(String msg){
        JSONObject jsonObject = JSONObject.parseObject(msg);
        String txStr = jsonObject.getString(SeckillConstants.MSG_KEY);
        return JSONObject.parseObject(txStr, ErrorMessage.class);
    }
}
