package io.check.seckill.stock.application.service.impl;

import com.alibaba.fastjson.JSON;
import io.check.seckill.common.constants.SeckillConstants;
import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.exception.SeckillException;
import io.check.seckill.common.lock.DistributedLock;
import io.check.seckill.common.lock.factoty.DistributedLockFactory;
import io.check.seckill.stock.application.model.command.SeckillStockBucketWrapperCommand;
import io.check.seckill.stock.application.model.dto.SeckillStockBucketDTO;
import io.check.seckill.stock.application.service.SeckillStockBucketArrangementService;
import io.check.seckill.stock.application.service.SeckillStockBucketService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author check
 * @version 1.0.0
 * @description 分桶库存服务
 */
@Service
public class SeckillStockBucketServiceImpl implements SeckillStockBucketService {

    private static final Logger logger = LoggerFactory.getLogger(SeckillStockBucketServiceImpl.class);

    @Autowired
    private DistributedLockFactory distributedLockFactory;

    @Autowired
    private SeckillStockBucketArrangementService seckillStockBucketArrangementService;

    @Override
    public void arrangeStockBuckets(Long userId, SeckillStockBucketWrapperCommand stockBucketWrapperCommand) {
        if (userId == null || stockBucketWrapperCommand == null) {
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        stockBucketWrapperCommand.setUserId(userId);
        if (stockBucketWrapperCommand.isEmpty()){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        logger.info("arrangeBuckets|编排库存分桶|{}", JSON.toJSON(stockBucketWrapperCommand));
        String lockKey = SeckillConstants.getKey(SeckillConstants.getKey(SeckillConstants.GOODS_BUCKET_ARRANGEMENT_KEY,
                String.valueOf(stockBucketWrapperCommand.getUserId())), String.valueOf(stockBucketWrapperCommand.getGoodsId()));
        DistributedLock lock = distributedLockFactory.getDistributedLock(lockKey);
        try {
            boolean isLock = lock.tryLock();
            if (!isLock){
                throw new SeckillException(ErrorCode.FREQUENTLY_ERROR);
            }
            //获取到锁，编排库存
            seckillStockBucketArrangementService.arrangeStockBuckets(stockBucketWrapperCommand.getGoodsId(),
                    stockBucketWrapperCommand.getStockBucketCommand().getTotalStock(),
                    stockBucketWrapperCommand.getStockBucketCommand().getBucketsQuantity(),
                    stockBucketWrapperCommand.getStockBucketCommand().getArrangementMode());
            logger.info("arrangeStockBuckets|库存编排完成|{}", stockBucketWrapperCommand.getGoodsId());
        }catch (SeckillException e){
            logger.error("arrangeStockBuckets|库存编排失败|{}", stockBucketWrapperCommand.getGoodsId(), e);
            throw e;
        }catch (Exception e){
            logger.error("arrangeStockBuckets|库存编排错误|{}", stockBucketWrapperCommand.getGoodsId(), e);
            throw new SeckillException(ErrorCode.BUCKET_CREATE_FAILED);
        }finally {
            lock.unlock();
        }
    }

    @Override
    public SeckillStockBucketDTO getTotalStockBuckets(Long goodsId, Long version) {
        if (goodsId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        logger.info("stockBucketsSummary|获取库存分桶数据|{}", goodsId);
        return seckillStockBucketArrangementService.getSeckillStockBucketDTO(goodsId, version);
    }
}
