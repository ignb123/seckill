package io.check.seckill.order.application.place.impl;

import io.check.seckill.common.cache.distribute.DistributedCacheService;
import io.check.seckill.common.constants.SeckillConstants;
import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.exception.SeckillException;
import io.check.seckill.common.model.dto.SeckillGoodsDTO;
import io.check.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import io.check.seckill.order.application.command.SeckillOrderCommand;
import io.check.seckill.order.application.place.SeckillPlaceOrderService;
import io.check.seckill.order.domain.model.entity.SeckillOrder;
import io.check.seckill.order.domain.service.SeckillOrderDomainService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

/**
 * @author check
 * @version 1.0.0
 * @description 同步下单
 */
@Service
@ConditionalOnProperty(name = "place.order.type", havingValue = "lua")
public class SeckillPlaceOrderLuaService implements SeckillPlaceOrderService {
    private final Logger logger = LoggerFactory.getLogger(SeckillPlaceOrderLuaService.class);
    @Autowired
    private SeckillOrderDomainService seckillOrderDomainService;

    @DubboReference(version = "1.0.0")
    private SeckillGoodsDubboService seckillGoodsDubboService;
    @Autowired
    private DistributedCacheService distributedCacheService;

    @Override
    public Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        SeckillGoodsDTO seckillGoods = seckillGoodsDubboService.getSeckillGoods(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
        //检测商品
        this.checkSeckillGoods(seckillOrderCommand, seckillGoods);
        //获取商品限购信息
        Object limitObj = distributedCacheService.getObject(SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_LIMIT_KEY_PREFIX, String.valueOf(seckillOrderCommand.getGoodsId())));
        //如果从Redis获取到的限购信息为null，则说明商品已经下线
        if (limitObj == null){
            throw new SeckillException(ErrorCode.GOODS_OFFLINE);
        }

        if (Integer.parseInt(String.valueOf(limitObj)) < seckillOrderCommand.getQuantity()){
            throw new SeckillException(ErrorCode.BEYOND_LIMIT_NUM);
        }
        String key = SeckillConstants.getKey(SeckillConstants.GOODS_ITEM_STOCK_KEY_PREFIX, String.valueOf(seckillOrderCommand.getGoodsId()));
        Long result = distributedCacheService.decrementByLua(key, seckillOrderCommand.getQuantity());
        this.checkResult(result);
        try{
            SeckillOrder seckillOrder = this.buildSeckillOrder(userId, seckillOrderCommand, seckillGoods);
            seckillOrderDomainService.saveSeckillOrder(seckillOrder);
            seckillGoodsDubboService.updateAvailableStock(seckillOrderCommand.getQuantity(), seckillOrderCommand.getGoodsId());
            // 测试出现分布式事务问题
            // int i = 1 / 0;
            return seckillOrder.getId();
        }catch (Exception e){
            logger.error("placeOrder|基于Lua脚本下单异常|{}", e.getMessage());
            //将内存中的库存增加回去
            distributedCacheService.incrementByLua(key, seckillOrderCommand.getQuantity());
            throw e;
        }
    }

    private void checkResult(Long result){
        if (result == SeckillConstants.LUA_RESULT_GOODS_STOCK_NOT_EXISTS) {
            throw new SeckillException(ErrorCode.STOCK_IS_NULL);
        }
        if (result == SeckillConstants.LUA_RESULT_GOODS_STOCK_PARAMS_LT_ZERO){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        if (result == SeckillConstants.LUA_RESULT_GOODS_STOCK_LT_ZERO){
            throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
        }
    }
}
