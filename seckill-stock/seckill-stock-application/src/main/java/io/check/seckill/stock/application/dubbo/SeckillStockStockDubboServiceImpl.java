package io.check.seckill.stock.application.dubbo;

import io.check.seckill.common.cache.model.SeckillBusinessCache;
import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.exception.SeckillException;
import io.check.seckill.common.model.dto.stock.SeckillStockDTO;
import io.check.seckill.dubbo.interfaces.stock.SeckillStockDubboService;
import io.check.seckill.stock.application.service.SeckillStockBucketService;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author check
 * @version 1.0.0
 * @description 库存Dubbo服务实现类
 */
@Component
@DubboService(version = "1.0.0")
public class SeckillStockStockDubboServiceImpl implements SeckillStockDubboService {
    private final Logger logger = LoggerFactory.getLogger(SeckillStockStockDubboServiceImpl.class);
    @Autowired
    private SeckillStockBucketService seckillStockBucketService;

    @Override
    public SeckillBusinessCache<Integer> getAvailableStock(Long goodsId, Long version) {
        if (goodsId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillStockBucketService.getAvailableStock(goodsId, version);

    }

    @Override
    public SeckillBusinessCache<SeckillStockDTO> getSeckillStock(Long goodsId, Long version) {
        if (goodsId == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        return seckillStockBucketService.getSeckillStock(goodsId, version);

    }
}
