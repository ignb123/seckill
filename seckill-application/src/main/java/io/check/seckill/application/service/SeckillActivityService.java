package io.check.seckill.application.service;

import io.check.seckill.domain.dto.SeckillActivityDTO;
import io.check.seckill.domain.model.SeckillActivity;

import java.util.Date;
import java.util.List;

public interface SeckillActivityService {

    /**
     * 保存活动信息
     */
    void saveSeckillActivityDTO(SeckillActivityDTO seckillActivityDTO);

    /**
     * 根据状态获取活动列表
     */
    List<SeckillActivity> getSeckillActivityList(Integer status);

    /**
     * 根据时间和状态获取活动列表
     */
    List<SeckillActivity> getSeckillActivityListBetweenStartTimeAndEndTime(Date currentTime, Integer status);

    /**
     * 根据id获取活动信息
     */
    SeckillActivity getSeckillActivityById(Long id);

    /**
     * 修改状态
     */
    int updateStatus(Integer status, Long id);
}
