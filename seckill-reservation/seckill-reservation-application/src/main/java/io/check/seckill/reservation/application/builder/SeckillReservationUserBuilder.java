package io.check.seckill.reservation.application.builder;

import io.check.seckill.common.builder.SeckillCommonBuilder;
import io.check.seckill.common.utils.beans.BeanUtil;
import io.check.seckill.reservation.application.command.SeckillReservationUserCommand;
import io.check.seckill.reservation.domain.model.entity.SeckillReservationUser;


/**
 * @author check
 * @version 1.0.0
 * @description SeckillReservationUserBuilder
 */
public class SeckillReservationUserBuilder extends SeckillCommonBuilder {

    public static SeckillReservationUser toSeckillReservationUser(SeckillReservationUserCommand seckillReservationUserCommand){
        if (seckillReservationUserCommand == null || seckillReservationUserCommand.isEmpty()){
            return null;
        }
        SeckillReservationUser seckillReservationUser = new SeckillReservationUser();
        BeanUtil.copyProperties(seckillReservationUserCommand, seckillReservationUser);
        return seckillReservationUser;
    }

}
