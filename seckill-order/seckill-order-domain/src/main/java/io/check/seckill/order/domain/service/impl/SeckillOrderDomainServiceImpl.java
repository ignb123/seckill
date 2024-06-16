package io.check.seckill.order.domain.service.impl;

import com.alibaba.fastjson.JSON;
import io.check.seckill.common.constants.SeckillConstants;
import io.check.seckill.common.model.message.TopicMessage;
import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.exception.SeckillException;
import io.check.seckill.common.model.enums.SeckillOrderStatus;
import io.check.seckill.mq.MessageSenderService;
import io.check.seckill.order.domain.event.SeckillOrderEvent;
import io.check.seckill.order.domain.model.entity.SeckillOrder;
import io.check.seckill.order.domain.repository.SeckillOrderRepository;
import io.check.seckill.order.domain.service.SeckillOrderDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author check
 * @version 1.0.0
 * @description 订单领域层业务服务
 */
@Service
public class SeckillOrderDomainServiceImpl implements SeckillOrderDomainService {
    private static final Logger logger = LoggerFactory.getLogger(SeckillOrderDomainServiceImpl.class);

    @Autowired
    private SeckillOrderRepository seckillOrderRepository;
    @Autowired
    private MessageSenderService messageSenderService;

    @Value("${message.mq.type}")
    private String eventType;

    @Override
    public boolean saveSeckillOrder(SeckillOrder seckillOrder) {
        logger.info("saveSeckillOrder|下单|{}", JSON.toJSONString(seckillOrder));
        if (seckillOrder == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        seckillOrder.setStatus(SeckillOrderStatus.CREATED.getCode());
        boolean saveSuccess = seckillOrderRepository.saveSeckillOrder(seckillOrder);
        if (saveSuccess){
            logger.info("saveSeckillOrder|创建订单成功|{}", JSON.toJSONString(seckillOrder));
            SeckillOrderEvent seckillOrderEvent = new SeckillOrderEvent(seckillOrder.getId(),
                    SeckillOrderStatus.CREATED.getCode(), getTopicEvent());
            messageSenderService.send(seckillOrderEvent);
        }
        return saveSuccess;
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByUserId(Long userId) {
        if (userId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillOrderRepository.getSeckillOrderByUserId(userId);
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByActivityId(Long activityId) {
        if (activityId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillOrderRepository.getSeckillOrderByActivityId(activityId);
    }

    @Override
    public void deleteOrder(Long orderId) {
        seckillOrderRepository.deleteOrder(orderId);
    }

    /**
     * 获取主题事件
     */
    private String getTopicEvent(){
        return SeckillConstants.EVENT_PUBLISH_TYPE_ROCKETMQ.equals(eventType) ?
                SeckillConstants.TOPIC_EVENT_ROCKETMQ_ORDER : SeckillConstants.TOPIC_EVENT_COLA;
    }

}
