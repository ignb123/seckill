package io.check.seckill.domain.service.impl;

import com.alibaba.fastjson.JSON;
import io.check.seckill.domain.code.HttpCode;
import io.check.seckill.domain.event.SeckillOrderEvent;
import io.check.seckill.domain.event.publisher.EventPublisher;
import io.check.seckill.domain.exception.SeckillException;
import io.check.seckill.domain.model.entity.SeckillOrder;
import io.check.seckill.domain.model.enums.SeckillOrderStatus;
import io.check.seckill.domain.repository.SeckillOrderRepository;
import io.check.seckill.domain.service.SeckillOrderDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SeckillOrderDomainServiceImpl implements SeckillOrderDomainService {

    private static final Logger logger = LoggerFactory.getLogger(SeckillOrderDomainServiceImpl.class);

    @Autowired
    private SeckillOrderRepository seckillOrderRepository;

    @Autowired
    private EventPublisher eventPublisher;

    @Override
    public boolean saveSeckillOrder(SeckillOrder seckillOrder) {
        logger.info("saveSeckillOrder|下单|{}", JSON.toJSONString(seckillOrder));
        if (seckillOrder == null){
            throw new SeckillException(HttpCode.PARAMS_INVALID);
        }
        seckillOrder.setStatus(SeckillOrderStatus.CREATED.getCode());
        boolean saveSuccess = seckillOrderRepository.saveSeckillOrder(seckillOrder);
        if(saveSuccess){
            logger.info("saveSeckillOrder|创建订单成功|{}", JSON.toJSONString(seckillOrder));
            SeckillOrderEvent seckillOrderEvent =
                    new SeckillOrderEvent(seckillOrder.getId(), SeckillOrderStatus.CREATED.getCode());
            eventPublisher.publish(seckillOrderEvent);
        }
        return saveSuccess;
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByUserId(Long userId) {
        if (userId == null){
            throw new SeckillException(HttpCode.PARAMS_INVALID);
        }
        return seckillOrderRepository.getSeckillOrderByUserId(userId);
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByActivityId(Long activityId) {
        if (activityId == null){
            throw new SeckillException(HttpCode.PARAMS_INVALID);
        }
        return seckillOrderRepository.getSeckillOrderByActivityId(activityId);
    }
}
