package io.check.seckill.dubbo.interfaces.activity;

import io.check.seckill.common.model.dto.SeckillActivityDTO;

/**
 * @author check
 * @version 1.0.0
 * @description 活动相关的Dubbo服务
 */
public interface SeckillActivityDubboService {

    /**
     * 获取活动信息
     */
    SeckillActivityDTO getSeckillActivity(Long id, Long version);
}
