package io.check.seckill.activity.application.dubbo;

import io.check.seckill.activity.application.service.SeckillActivityService;
import io.check.seckill.common.model.dto.activity.SeckillActivityDTO;
import io.check.seckill.dubbo.interfaces.activity.SeckillActivityDubboService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author check
 * @version 1.0.0
 * @description Dubbo服务
 */
@Component
@DubboService(version = "1.0.0")
public class SeckillActivityDubboServiceImpl implements SeckillActivityDubboService {
    @Autowired
    private SeckillActivityService seckillActivityService;

    @Override
    public SeckillActivityDTO getSeckillActivity(Long id, Long version) {
        return seckillActivityService.getSeckillActivity(id, version);
    }
}
