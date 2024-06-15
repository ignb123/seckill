package io.check.seckill.goods.infrastructure.repository;

import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.exception.SeckillException;
import io.check.seckill.goods.domain.model.entity.SeckillGoods;
import io.check.seckill.goods.domain.repository.SeckillGoodsRepository;
import io.check.seckill.goods.infrastructure.mapper.SeckillGoodsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author check
 * @version 1.0.0
 * @description 商品
 */
@Component
public class SeckillGoodsRepositoryImpl implements SeckillGoodsRepository {

    @Autowired
    private SeckillGoodsMapper seckillGoodsMapper;

    @Override
    public int saveSeckillGoods(SeckillGoods seckillGoods) {
        if (seckillGoods == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillGoodsMapper.saveSeckillGoods(seckillGoods);
    }

    @Override
    public SeckillGoods getSeckillGoodsId(Long id) {
        return seckillGoodsMapper.getSeckillGoodsId(id);
    }

    @Override
    public List<SeckillGoods> getSeckillGoodsByActivityId(Long activityId) {
        return seckillGoodsMapper.getSeckillGoodsByActivityId(activityId);
    }

    @Override
    public int updateStatus(Integer status, Long id) {
        return seckillGoodsMapper.updateStatus(status, id);
    }

    @Override
    public int updateAvailableStock(Integer count, Long id) {
        return seckillGoodsMapper.updateAvailableStock(count, id);
    }

    @Override
    public Integer getAvailableStockById(Long id) {
        return seckillGoodsMapper.getAvailableStockById(id);
    }
}
