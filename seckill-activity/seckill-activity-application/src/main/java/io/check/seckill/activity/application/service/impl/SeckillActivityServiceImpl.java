package io.check.seckill.activity.application.service.impl;


import io.check.seckill.activity.application.builder.SeckillActivityBuilder;
import io.check.seckill.activity.application.cache.service.SeckillActivityCacheService;
import io.check.seckill.activity.application.cache.service.SeckillActivityListCacheService;
import io.check.seckill.activity.application.command.SeckillActivityCommand;
import io.check.seckill.activity.application.service.SeckillActivityService;
import io.check.seckill.activity.domain.model.entity.SeckillActivity;
import io.check.seckill.activity.domain.service.SeckillActivityDomainService;
import io.check.seckill.common.cache.model.SeckillBusinessCache;
import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.exception.SeckillException;
import io.check.seckill.common.model.dto.SeckillActivityDTO;
import io.check.seckill.common.model.enums.SeckillActivityStatus;
import io.check.seckill.common.utils.beans.BeanUtil;
import io.check.seckill.common.utils.id.SnowFlakeFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SeckillActivityServiceImpl implements SeckillActivityService {

    @Resource
    private SeckillActivityDomainService seckillActivityDomainService;

    @Resource
    private SeckillActivityListCacheService seckillActivityListCacheService;

    @Resource
    private SeckillActivityCacheService seckillActivityCacheService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSeckillActivity(SeckillActivityCommand seckillActivityCommand) {
        if (seckillActivityCommand == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillActivity seckillActivity =
                SeckillActivityBuilder.toSeckillActivity(seckillActivityCommand);
        seckillActivity.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
        seckillActivity.setStatus(SeckillActivityStatus.PUBLISHED.getCode());
        seckillActivityDomainService.saveSeckillActivity(seckillActivity);
    }

    @Override
    public List<SeckillActivity> getSeckillActivityList(Integer status) {
        return seckillActivityDomainService.getSeckillActivityList(status);
    }
    @Override
    public List<SeckillActivity> getSeckillActivityListBetweenStartTimeAndEndTime(Date currentTime, Integer status) {
        return seckillActivityDomainService.getSeckillActivityListBetweenStartTimeAndEndTime(currentTime, status);
    }
    @Override
    public SeckillActivity getSeckillActivityById(Long id) {
        return seckillActivityDomainService.getSeckillActivityById(id);
    }
    @Override
    public void updateStatus(Integer status, Long id) {
        seckillActivityDomainService.updateStatus(status, id);
    }

    @Override
    public List<SeckillActivityDTO> getSeckillActivityList(Integer status, Long version) {
        SeckillBusinessCache<List<SeckillActivity>> seckillActivitiyListCache =
                seckillActivityListCacheService.getCachedActivities(status, version);
        if (!seckillActivitiyListCache.isExist()){
            throw new SeckillException(ErrorCode.ACTIVITY_NOT_EXISTS);
        }
        //稍后再试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后再试
        if (seckillActivitiyListCache.isRetryLater()){
            throw new SeckillException(ErrorCode.RETRY_LATER);
        }
        List<SeckillActivityDTO> seckillActivityDTOList = seckillActivitiyListCache.getData()
                .stream()
                .map((seckillActivity) -> {
                    SeckillActivityDTO seckillActivityDTO = new SeckillActivityDTO();
                    BeanUtil.copyProperties(seckillActivity, seckillActivityDTO);
                    seckillActivityDTO.setVersion(seckillActivitiyListCache.getVersion());
                    return seckillActivityDTO;
                })
                .collect(Collectors.toList());
        return seckillActivityDTOList;
    }

    @Override
    public SeckillActivityDTO getSeckillActivity(Long id, Long version) {
        if (id == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        SeckillBusinessCache<SeckillActivity> seckillActivityCache =
                seckillActivityCacheService.getCachedSeckillActivity(id, version);
        //缓存中的活动数据不存在
        if (!seckillActivityCache.isExist()){
            throw new SeckillException(ErrorCode.ACTIVITY_NOT_EXISTS);
        }
        //稍后再试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后再试
        if (seckillActivityCache.isRetryLater()){
            throw new SeckillException(ErrorCode.RETRY_LATER);
        }
        SeckillActivityDTO seckillActivityDTO = SeckillActivityBuilder.toSeckillActivityDTO(seckillActivityCache.getData());
        seckillActivityDTO.setVersion(seckillActivityCache.getVersion());
        return seckillActivityDTO;
    }

    @Override
    public List<SeckillActivityDTO> getSeckillActivityListBetweenStartTimeAndEndTime(Date currentTime, Integer status, Long version) {
        SeckillBusinessCache<List<SeckillActivity>> seckillActivitiyListCache = seckillActivityListCacheService
                .getCachedActivities(currentTime, status, version);
        //稍后再试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后再试
        if (seckillActivitiyListCache.isRetryLater()){
            throw new SeckillException(ErrorCode.RETRY_LATER);
        }
        if (!seckillActivitiyListCache.isExist()){
            throw new SeckillException(ErrorCode.ACTIVITY_NOT_EXISTS);
        }
        List<SeckillActivityDTO> seckillActivityDTOList =
                seckillActivitiyListCache.getData().stream()
                .map((seckillActivity) -> {
            SeckillActivityDTO seckillActivityDTO = new SeckillActivityDTO();
            BeanUtil.copyProperties(seckillActivity, seckillActivityDTO);
            seckillActivityDTO.setVersion(seckillActivitiyListCache.getVersion());
            return seckillActivityDTO;
        }).collect(Collectors.toList());
        return seckillActivityDTOList;
    }
}
