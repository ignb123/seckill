package io.check.seckill.reservation.application.dubbo;

import io.check.seckill.common.cache.model.SeckillBusinessCache;
import io.check.seckill.common.model.enums.SeckillReservationConfigStatus;
import io.check.seckill.common.model.enums.SeckillReservationUserStatus;
import io.check.seckill.dubbo.interfaces.reservation.SeckillReservationDubboService;
import io.check.seckill.reservation.application.cache.SeckillReservationConfigCacheService;
import io.check.seckill.reservation.application.cache.SeckillReservationUserCacheService;
import io.check.seckill.reservation.domain.model.entity.SeckillReservationConfig;
import io.check.seckill.reservation.domain.model.entity.SeckillReservationUser;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author check
 * @version 1.0.0
 * @description 预约服务Dubbo接口
 */
@Component
@DubboService(version = "1.0.0")
public class SeckillReservationDubboServiceImpl implements SeckillReservationDubboService {

    @Autowired
    private SeckillReservationConfigCacheService seckillReservationConfigCacheService;
    @Autowired
    private SeckillReservationUserCacheService seckillReservationUserCacheService;

    @Override
    public boolean checkReservation(Long userId, Long goodsId) {
        //获取商品配置信息
        SeckillBusinessCache<SeckillReservationConfig> seckillReservationConfigCache = seckillReservationConfigCacheService.getSeckillReservationConfig(goodsId, 0L);
        //缓存中不存在配置数据
        if (!seckillReservationConfigCache.isExist()){
            return true;
        }
        //获取商品的具体配置信息
        SeckillReservationConfig seckillReservationConfig = seckillReservationConfigCache.getData();
        //商品配置为空，或者不是上线的状态，直接返回true，可以直接抢购下单
        if (seckillReservationConfig == null || !SeckillReservationConfigStatus.isOnline(seckillReservationConfig.getStatus())){
            return true;
        }
        //商品配置信息正常，则获取用户是否预约了商品
        SeckillBusinessCache<SeckillReservationUser> seckillReservationUserCache = seckillReservationUserCacheService.getSeckillReservationUserCacheByUserIdAndGoodsId(userId, goodsId, 0L);
        //用户预约的商品配置缓存为重试状态，或者不存在数据，则直接返回false
        if (seckillReservationUserCache.isRetryLater() || !seckillReservationUserCache.isExist()){
            return false;
        }
        //获取指定用户的商品预约信息
        SeckillReservationUser seckillReservationUser = seckillReservationUserCache.getData();
        return seckillReservationUser != null && SeckillReservationUserStatus.isNormal(seckillReservationUser.getStatus());
    }
}
