package io.check.seckill.activity.application.service;



import io.check.seckill.activity.application.command.SeckillActivityCommand;
import io.check.seckill.activity.domain.model.entity.SeckillActivity;
import io.check.seckill.common.model.dto.SeckillActivityDTO;

import java.util.Date;
import java.util.List;

public interface SeckillActivityService {

    /**
     * 保存活动信息
     */
    void saveSeckillActivity(SeckillActivityCommand seckillActivityCommand);

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
    void updateStatus(Integer status, Long id);

    /**
     * 活动列表
     */
    List<SeckillActivityDTO> getSeckillActivityList(Integer status, Long version);

    /**
     * 获取活动信息，带有缓存
     */
    SeckillActivityDTO getSeckillActivity(Long id, Long version);

    /**
     * 获取正在进行中的活动列表
     */
    List<SeckillActivityDTO> getSeckillActivityListBetweenStartTimeAndEndTime(Date currentTime, Integer status, Long version);
}
