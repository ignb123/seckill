package io.check.seckill.order.infrastructure.repository;

import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.exception.SeckillException;
import io.check.seckill.order.domain.model.entity.SeckillOrder;
import io.check.seckill.order.domain.repository.SeckillOrderRepository;
import io.check.seckill.order.infrastructure.mapper.SeckillOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author check
 * @version 1.0.0
 * @description 订单
 */
@Component
public class SeckillOrderRepositoryImpl implements SeckillOrderRepository {
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Override
    public boolean saveSeckillOrder(SeckillOrder seckillOrder) {
        if (seckillOrder == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillOrderMapper.saveSeckillOrder(seckillOrder) == 1;
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByUserId(Long userId) {
        return seckillOrderMapper.getSeckillOrderByUserId(userId);
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByActivityId(Long activityId) {
        return seckillOrderMapper.getSeckillOrderByActivityId(activityId);
    }
}
