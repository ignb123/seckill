package io.check.seckill.order.infrastructure.repository;

import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.exception.SeckillException;
import io.check.seckill.order.domain.model.entity.SeckillOrder;
import io.check.seckill.order.domain.repository.SeckillOrderRepository;
import io.check.seckill.order.infrastructure.mapper.SeckillGoodsOrderMapper;
import io.check.seckill.order.infrastructure.mapper.SeckillUserOrderMapper;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author check
 * @version 1.0.0
 * @description 订单
 */
@Component
public class SeckillOrderRepositoryImpl implements SeckillOrderRepository {
    @Resource
    private SeckillUserOrderMapper seckillUserOrderMapper;
    @Resource
    private SeckillGoodsOrderMapper seckillGoodsOrderMapper;


    @Override
    public boolean saveSeckillOrder(SeckillOrder seckillOrder) {
        if (seckillOrder == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        int userResult = seckillUserOrderMapper.saveSeckillOrder(seckillOrder);
        int goodsResult = seckillGoodsOrderMapper.saveSeckillOrder(seckillOrder);
        return userResult == 1 && goodsResult == 1;
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByUserId(Long userId) {
        if (userId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillUserOrderMapper.getSeckillOrderByUserId(userId);
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByGoodsId(Long goodsId) {
        if (goodsId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillGoodsOrderMapper.getSeckillOrderByGoodsId(goodsId);
    }

    @Override
    public void deleteOrderShardingUserId(Long orderId, Long userId) {
        if (orderId == null || userId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        seckillUserOrderMapper.deleteOrder(userId, orderId);
    }

    @Override
    public void deleteOrderShardingGoodsId(Long orderId, Long goodsId) {
        if (goodsId == null || orderId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        seckillGoodsOrderMapper.deleteOrder(goodsId, orderId);
    }

}
