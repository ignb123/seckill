package io.check.seckill.order.application.place.impl;


import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.exception.SeckillException;
import io.check.seckill.common.model.dto.SeckillGoodsDTO;
import io.check.seckill.dubbo.interfaces.goods.SeckillGoodsDubboService;
import io.check.seckill.order.application.command.SeckillOrderCommand;
import io.check.seckill.order.application.place.SeckillPlaceOrderService;
import io.check.seckill.order.domain.model.entity.SeckillOrder;
import io.check.seckill.order.domain.service.SeckillOrderDomainService;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author check
 * @version 1.0.0
 * @description 分布式锁下单
 */
@Service
@ConditionalOnProperty(name = "place.order.type", havingValue = "db")
public class SeckillPlaceOrderDbService implements SeckillPlaceOrderService {

    @DubboReference(version = "1.0.0")
    private SeckillGoodsDubboService seckillGoodsDubboService;

    @Autowired
    private SeckillOrderDomainService seckillOrderDomainService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long placeOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        //获取商品
        SeckillGoodsDTO seckillGoods = seckillGoodsDubboService.getSeckillGoods(seckillOrderCommand.getGoodsId(), seckillOrderCommand.getVersion());
        //检测商品信息
        this.checkSeckillGoods(seckillOrderCommand, seckillGoods);
        //扣减库存不成功，则库存不足
        if (!seckillGoodsDubboService.updateAvailableStock(seckillOrderCommand.getQuantity(), seckillOrderCommand.getGoodsId())){
            throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
        }
        //构建订单
        SeckillOrder seckillOrder = this.buildSeckillOrder(userId, seckillOrderCommand, seckillGoods);
        //保存订单
        seckillOrderDomainService.saveSeckillOrder(seckillOrder);
        return seckillOrder.getId();
    }

}
