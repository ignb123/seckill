package io.check.seckill.application.service.impl;

import io.check.seckill.application.service.SeckillActivityService;
import io.check.seckill.domain.code.HttpCode;
import io.check.seckill.domain.dto.SeckillActivityDTO;
import io.check.seckill.domain.enums.SeckillActivityStatus;
import io.check.seckill.domain.exception.SeckillException;
import io.check.seckill.domain.model.SeckillActivity;
import io.check.seckill.domain.repository.SeckillActivityRepository;
import io.check.seckill.infrastructure.utils.beans.BeanUtil;
import io.check.seckill.infrastructure.utils.id.SnowFlakeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class SeckillActivityServiceImpl implements SeckillActivityService {

    @Autowired
    private SeckillActivityRepository seckillActivityRepository;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSeckillActivityDTO(SeckillActivityDTO seckillActivityDTO) {
        if (seckillActivityDTO == null){
            throw new SeckillException(HttpCode.PARAMS_INVALID);
        }
        SeckillActivity seckillActivity = new SeckillActivity();
        BeanUtil.copyProperties(seckillActivityDTO, seckillActivity);
        seckillActivity.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
        seckillActivity.setStatus(SeckillActivityStatus.PUBLISHED.getCode());
        seckillActivityRepository.saveSeckillActivity(seckillActivity);
    }

    @Override
    public List<SeckillActivity> getSeckillActivityList(Integer status) {
        return seckillActivityRepository.getSeckillActivityList(status);
    }
    @Override
    public List<SeckillActivity> getSeckillActivityListBetweenStartTimeAndEndTime(Date currentTime, Integer status) {
        return seckillActivityRepository.getSeckillActivityListBetweenStartTimeAndEndTime(currentTime, status);
    }
    @Override
    public SeckillActivity getSeckillActivityById(Long id) {
        return seckillActivityRepository.getSeckillActivityById(id);
    }
    @Override
    public int updateStatus(Integer status, Long id) {
        return seckillActivityRepository.updateStatus(status, id);
    }
}
