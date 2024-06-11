package io.check.seckill.infrastructure.repository;

import io.check.seckill.domain.code.HttpCode;
import io.check.seckill.domain.exception.SeckillException;
import io.check.seckill.domain.model.SeckillOrder;
import io.check.seckill.domain.repository.SeckillOrderRepository;
import io.check.seckill.infrastructure.mapper.SeckillOrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SeckillOrderRepositoryImpl implements SeckillOrderRepository {

    @Autowired
    private SeckillOrderMapper seckillOrderMapper;

    @Override
    public int saveSeckillOrder(SeckillOrder seckillOrder) {
        if(seckillOrder == null){
            throw new SeckillException(HttpCode.PARAMS_INVALID);
        }
        return seckillOrderMapper.saveSeckillOrder(seckillOrder);
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
