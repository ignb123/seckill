package io.check.seckill.stock.infrastructure.repository;

import cn.hutool.core.collection.CollectionUtil;
import io.check.seckill.common.model.enums.SeckillStockBucketStatus;
import io.check.seckill.stock.domain.model.entity.SeckillStockBucket;
import io.check.seckill.stock.domain.repository.SeckillStockBucketRepository;
import io.check.seckill.stock.infrastructure.mapper.SeckillStockBucketMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

/**
 * @author check
 * @version 1.0.0
 * @description 分桶库存Repository实现类
 */
@Component
public class SeckillStockBucketRepositoryImpl implements SeckillStockBucketRepository {

    @Autowired
    private SeckillStockBucketMapper seckillStockBucketMapper;

    @Override
    public boolean submitBuckets(Long goodsId, List<SeckillStockBucket> buckets) {
        if (goodsId == null || CollectionUtil.isEmpty(buckets)){
            return false;
        }
        seckillStockBucketMapper.deleteByGoodsId(goodsId);
        seckillStockBucketMapper.insertBatch(buckets);
        return true;
    }

    @Override
    public boolean increaseStock(Integer quantity, Integer serialNo, Long goodsId) {
        if (quantity == null || serialNo == null || goodsId == null){
            return false;
        }
        seckillStockBucketMapper.increaseStock(quantity, serialNo, goodsId);
        return true;
    }

    @Override
    public boolean decreaseStock(Integer quantity, Integer serialNo, Long goodsId) {
        if (quantity == null || serialNo == null || goodsId == null){
            return false;
        }
        seckillStockBucketMapper.decreaseStock(quantity, serialNo, goodsId);
        return true;
    }

    @Override
    public List<SeckillStockBucket> getBucketsByGoodsId(Long goodsId) {
        if (goodsId == null){
            return Collections.emptyList();
        }
        return seckillStockBucketMapper.getBucketsByGoodsId(goodsId);
    }

    @Override
    public boolean suspendBuckets(Long goodsId) {
        if (goodsId == null){
            return false;
        }
        seckillStockBucketMapper.updateStatusByGoodsId(SeckillStockBucketStatus.DISABLED.getCode(), goodsId);
        return true;
    }

    @Override
    public boolean resumeBuckets(Long goodsId) {
        if (goodsId == null){
            return false;
        }
        seckillStockBucketMapper.updateStatusByGoodsId(SeckillStockBucketStatus.ENABLED.getCode(), goodsId);
        return true;

    }
}
