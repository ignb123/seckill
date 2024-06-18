package io.check.seckill.reservation.infrastructure.repository;

import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.exception.SeckillException;
import io.check.seckill.reservation.domain.model.entity.SeckillReservationConfig;
import io.check.seckill.reservation.domain.model.entity.SeckillReservationUser;
import io.check.seckill.reservation.domain.repository.SeckillReservationRepository;
import io.check.seckill.reservation.infrastructure.mapper.SeckillReservationConfigMapper;
import io.check.seckill.reservation.infrastructure.mapper.SeckillReservationUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author check
 * @version 1.0.0
 * @description 预约服务Repository实现类
 */
@Component
public class SeckillReservationRepositoryImpl implements SeckillReservationRepository {

    @Autowired
    private SeckillReservationConfigMapper seckillReservationConfigMapper;

    @Autowired
    private SeckillReservationUserMapper seckillReservationUserMapper;

    @Override
    public boolean saveSeckillReservationConfig(SeckillReservationConfig seckillReservationConfig) {
        if (seckillReservationConfig == null || seckillReservationConfig.isEmpty()){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillReservationConfigMapper.saveSeckillReservationConfig(seckillReservationConfig) == 1;
    }

    @Override
    public boolean updateSeckillReservationConfig(SeckillReservationConfig seckillReservationConfig) {
        if (seckillReservationConfig == null || seckillReservationConfig.isEmpty()){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillReservationConfigMapper.updateSeckillReservationConfig(seckillReservationConfig) == 1;
    }

    @Override
    public boolean updateConfigStatus(Integer status, Long goodsId) {
        if (status == null || goodsId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillReservationConfigMapper.updateStatus(status, goodsId) == 1;

    }

    @Override
    public List<SeckillReservationConfig> getConfigList() {
        return seckillReservationConfigMapper.getConfigList();
    }

    @Override
    public SeckillReservationConfig getConfigDetail(Long goodsId) {
        if (goodsId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillReservationConfigMapper.getConfigDetail(goodsId);

    }

    @Override
    public List<SeckillReservationUser> getUserListByGoodsId(Long goodsId, Integer status) {
        if (goodsId == null || status == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillReservationUserMapper.getUserListByGoodsId(goodsId, status);

    }

    @Override
    public List<SeckillReservationUser> getGoodsListByUserId(Long userId, Integer status) {
        if (userId == null || status == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillReservationUserMapper.getGoodsListByUserId(userId, status);

    }

    @Override
    public boolean reserveGoods(SeckillReservationUser seckillReservationUser) {
        if (seckillReservationUser == null || seckillReservationUser.isEmpty()){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillReservationUserMapper.reserveGoods(seckillReservationUser) == 1;

    }

    @Override
    public boolean cancelReserveGoods(Long goodsId, Long userId) {
        if (goodsId == null || userId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillReservationUserMapper.cancelReserveGoods(goodsId, userId) == 1;

    }

    @Override
    public SeckillReservationUser getSeckillReservationUser(Long userId, Long goodsId, Integer status) {
        if (goodsId == null || userId == null || status == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillReservationUserMapper.getSeckillReservationUser(userId, goodsId, status);

    }
}
