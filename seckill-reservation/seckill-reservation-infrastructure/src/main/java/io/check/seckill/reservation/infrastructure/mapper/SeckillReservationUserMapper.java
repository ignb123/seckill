package io.check.seckill.reservation.infrastructure.mapper;

import io.check.seckill.reservation.domain.model.entity.SeckillReservationUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author check
 * @version 1.0.0
 * @description SeckillReservationUserMapper接口
 */
public interface SeckillReservationUserMapper {

    /**
     * 根据商品id查看预约用户列表
     */
    List<SeckillReservationUser> getUserListByGoodsId(@Param("goodsId") Long goodsId, @Param("status") Integer status);

    /**
     * 根据用户id查看预约的商品列表
     */
    List<SeckillReservationUser> getGoodsListByUserId(@Param("userId") Long userId, @Param("status") Integer status);

    /**
     * 预约秒杀商品
     */
    int reserveGoods(SeckillReservationUser seckillReservationUser);

    /**
     * 取消预约秒杀商品
     */
    int cancelReserveGoods(@Param("goodsId") Long goodsId, @Param("userId") Long userId);

    /**
     * 获取用户预约的某个商品信息
     */
    SeckillReservationUser getSeckillReservationUser(@Param("userId") Long userId, @Param("goodsId") Long goodsId, @Param("status") Integer status);
}
