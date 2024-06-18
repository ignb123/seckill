package io.check.seckill.dubbo.interfaces.goods;

import io.check.seckill.common.cache.model.SeckillBusinessCache;
import io.check.seckill.common.model.dto.goods.SeckillGoodsDTO;

/**
 * @author check
 * @version 1.0.0
 * @description 商品Dubbo服务接口
 */
public interface SeckillGoodsDubboService {

    /**
     * 根据id和版本号获取商品详情
     */
    SeckillGoodsDTO getSeckillGoods(Long id, Long version);

    /**
     * 扣减数据库库存
     */
    boolean updateDbAvailableStock(Integer count, Long id);

    /**
     * 扣减商品库存
     */
    boolean updateAvailableStock(Integer count, Long id);

    /**
     * 根据商品id获取可用库存
     */
    Integer getAvailableStockById(Long goodsId);

    /**
     * 获取商品的可用库存
     */
    SeckillBusinessCache<Integer> getAvailableStock(Long goodsId, Long version);
}
