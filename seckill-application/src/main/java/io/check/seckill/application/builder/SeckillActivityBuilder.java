package io.check.seckill.application.builder;

import io.check.seckill.application.builder.common.SeckillCommonBuilder;
import io.check.seckill.application.command.SeckillActivityCommand;
import io.check.seckill.domain.dto.SeckillActivityDTO;
import io.check.seckill.domain.model.SeckillActivity;
import io.check.seckill.infrastructure.utils.beans.BeanUtil;

/**
 * @author check
 * @version 1.0.0
 * @description 秒杀活动构建类
 */
public class SeckillActivityBuilder extends SeckillCommonBuilder {

    public static SeckillActivity toSeckillActivity(SeckillActivityCommand seckillActivityCommand){
        if (seckillActivityCommand == null){
            return null;
        }
        SeckillActivity seckillActivity = new SeckillActivity();
        BeanUtil.copyProperties(seckillActivityCommand, seckillActivity);
        return seckillActivity;
    }

    public static SeckillActivityDTO toSeckillActivityDTO(SeckillActivity seckillActivity){
        if (seckillActivity == null){
            return null;
        }
        SeckillActivityDTO seckillActivityDTO = new SeckillActivityDTO();
        BeanUtil.copyProperties(seckillActivity, seckillActivityDTO);
        return seckillActivityDTO;
    }
}
