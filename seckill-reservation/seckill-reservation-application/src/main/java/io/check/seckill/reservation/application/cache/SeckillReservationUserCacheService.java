package io.check.seckill.reservation.application.cache;

import io.check.seckill.common.cache.model.SeckillBusinessCache;
import io.check.seckill.common.cache.service.SeckillCacheService;
import io.check.seckill.reservation.domain.event.SeckillReservationUserEvent;
import io.check.seckill.reservation.domain.model.entity.SeckillReservationUser;

import java.util.List;

/**
 * @author check
 * @version 1.0.0
 * @description SeckillReservationUserCacheService
 */
public interface SeckillReservationUserCacheService extends SeckillCacheService {

    /**
     * 根据用户id和商品id获取用户预约信息
     */
    SeckillBusinessCache<SeckillReservationUser> getSeckillReservationUserCacheByUserIdAndGoodsId(Long userId, Long goodsId, Long version);

    /**
     * 根据用户id和商品id更新用户预约信息
     */
    SeckillBusinessCache<SeckillReservationUser> tryUpdateSeckillReservationUserCacheByUserIdAndGoodsId(Long userId, Long goodsId, boolean doubleCheck);

    /**
     * 根据商品id查看预约用户列表
     */
    SeckillBusinessCache<List<SeckillReservationUser>> getUserListCacheByGoodsId(Long goodsId, Long version);

    /**
     * 根据商品id更新预约用户列表
     */
    SeckillBusinessCache<List<SeckillReservationUser>> tryUpdatetUserListCacheByGoodsId(Long goodsId, boolean doubleCheck);

    /**
     * 根据用户id查看预约的商品列表
     */
    SeckillBusinessCache<List<SeckillReservationUser>> getGoodsListCacheByUserId(Long userId, Long version);

    /**
     * 根据用户id更新预约的商品列表
     */
    SeckillBusinessCache<List<SeckillReservationUser>> tryUpdateGoodsListCacheByUserId(Long userId, boolean doubleCheck);

    /**
     * 删除缓存中的数据
     */
    void deleteSeckillReservationUserFromCache(SeckillReservationUserEvent seckillReservationUserEvent);
}
