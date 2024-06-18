package io.check.seckill.reservation.application.builder;

import io.check.seckill.common.builder.SeckillCommonBuilder;
import io.check.seckill.common.utils.beans.BeanUtil;
import io.check.seckill.reservation.application.command.SeckillReservationConfigCommand;
import io.check.seckill.reservation.domain.model.entity.SeckillReservationConfig;

/**
 * @author check
 * @version 1.0.0
 * @description SeckillReservationConfigBuilder
 */
public class SeckillReservationConfigBuilder extends SeckillCommonBuilder {

    public static SeckillReservationConfig toSeckillReservationConfig(SeckillReservationConfigCommand seckillReservationConfigCommand){
        if (seckillReservationConfigCommand == null || seckillReservationConfigCommand.isEmpty()){
            return null;
        }
        SeckillReservationConfig seckillReservationConfig = new SeckillReservationConfig();
        BeanUtil.copyProperties(seckillReservationConfigCommand, seckillReservationConfig);
        return seckillReservationConfig;
    }
}

