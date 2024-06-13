package io.check.seckill.domain.service.impl;

import com.alibaba.fastjson.JSON;
import io.check.seckill.domain.code.HttpCode;
import io.check.seckill.domain.model.enums.SeckillActivityStatus;
import io.check.seckill.domain.event.SeckillActivityEvent;
import io.check.seckill.domain.event.publisher.EventPublisher;
import io.check.seckill.domain.exception.SeckillException;
import io.check.seckill.domain.model.entity.SeckillActivity;
import io.check.seckill.domain.repository.SeckillActivityRepository;
import io.check.seckill.domain.service.SeckillActivityDomainService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SeckillActivityDomainServiceImpl implements SeckillActivityDomainService {

    private static final Logger logger = LoggerFactory.getLogger(SeckillActivityDomainServiceImpl.class);

    @Autowired
    private SeckillActivityRepository seckillActivityRepository;

    @Autowired
    private EventPublisher eventPublisher;

    @Override
    public void saveSeckillActivity(SeckillActivity seckillActivity) {
        logger.info("activityPublish|发布秒杀活动|{}", JSON.toJSON(seckillActivity));
        if (seckillActivity == null || !seckillActivity.validateParams()){
            throw new SeckillException(HttpCode.PARAMS_INVALID);
        }
        seckillActivity.setStatus(SeckillActivityStatus.PUBLISHED.getCode());
        seckillActivityRepository.saveSeckillActivity(seckillActivity);
        logger.info("activityPublish|秒杀活动已发布|{}", seckillActivity.getId());

        SeckillActivityEvent seckillActivityEvent =
                new SeckillActivityEvent(seckillActivity.getId(), seckillActivity.getStatus());
        eventPublisher.publish(seckillActivityEvent);
        logger.info("activityPublish|秒杀活动事件已发布|{}", JSON.toJSON(seckillActivityEvent));
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
        if (id == null){
            throw new SeckillException(HttpCode.PASSWORD_IS_NULL);
        }
        return seckillActivityRepository.getSeckillActivityById(id);
    }

    @Override
    public void updateStatus(Integer status, Long id) {
        logger.info("activityPublish|更新秒杀活动状态|{},{}", status, id);
        if (status == null || id == null){
            throw new SeckillException(HttpCode.PARAMS_INVALID);
        }
        seckillActivityRepository.updateStatus(status, id);
        logger.info("activityPublish|发布秒杀活动状态事件|{},{}", status, id);
        SeckillActivityEvent seckillActivityEvent = new SeckillActivityEvent(id, status);
        eventPublisher.publish(seckillActivityEvent);
        logger.info("activityPublish|秒杀活动事件已发布|{}", id);
    }
}
