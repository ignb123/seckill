package io.check.seckill.activity.infrastructure.repository;

import io.check.seckill.activity.domain.model.entity.SeckillActivity;
import io.check.seckill.activity.domain.repository.SeckillActivityRepository;
import io.check.seckill.activity.infrastructure.mapper.SeckillActivityMapper;
import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.exception.SeckillException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Component
public class SeckillActivityRepositoryImpl implements SeckillActivityRepository {
    @Resource
    private SeckillActivityMapper seckillActivityMapper;

    @Override
    public int saveSeckillActivity(SeckillActivity seckillActivity) {
        if (seckillActivity == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillActivityMapper.saveSeckillActivity(seckillActivity);
    }
    @Override
    public List<SeckillActivity> getSeckillActivityList(Integer status) {
        return seckillActivityMapper.getSeckillActivityList(status);
    }
    @Override
    public List<SeckillActivity> getSeckillActivityListBetweenStartTimeAndEndTime(Date currentTime, Integer status) {
        return seckillActivityMapper.getSeckillActivityListBetweenStartTimeAndEndTime(currentTime, status);
    }
    @Override
    public SeckillActivity getSeckillActivityById(Long id) {
        return seckillActivityMapper.getSeckillActivityById(id);
    }
    @Override
    public int updateStatus(Integer status, Long id) {
        return seckillActivityMapper.updateStatus(status, id);
    }
}