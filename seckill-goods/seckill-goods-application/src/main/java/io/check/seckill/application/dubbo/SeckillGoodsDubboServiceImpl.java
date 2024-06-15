package io.check.seckill.application.dubbo;

import io.check.seckill.application.service.SeckillGoodsService;
import io.check.seckill.common.model.dto.SeckillGoodsDTO;
import io.check.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author check
 * @version 1.0.0
 * @description 商品Dubbo服务实现类
 */
@Component
@DubboService(version = "1.0.0")
public class SeckillGoodsDubboServiceImpl implements SeckillGoodsDubboService {
    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Override
    public SeckillGoodsDTO getSeckillGoods(Long id, Long version) {
        return seckillGoodsService.getSeckillGoods(id, version);
    }

    @Override
    public boolean updateDbAvailableStock(Integer count, Long id) {
        return seckillGoodsService.updateDbAvailableStock(count, id);
    }

    @Override
    public boolean updateAvailableStock(Integer count, Long id) {
        return seckillGoodsService.updateAvailableStock(count, id);
    }

    @Override
    public Integer getAvailableStockById(Long goodsId) {
        return seckillGoodsService.getAvailableStockById(goodsId);
    }
}
