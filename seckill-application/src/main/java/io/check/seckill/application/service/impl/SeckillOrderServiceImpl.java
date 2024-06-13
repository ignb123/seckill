package io.check.seckill.application.service.impl;

import io.check.seckill.application.service.SeckillGoodsService;
import io.check.seckill.application.service.SeckillOrderService;
import io.check.seckill.domain.code.HttpCode;
import io.check.seckill.domain.model.dto.SeckillOrderDTO;
import io.check.seckill.domain.model.enums.SeckillGoodsStatus;
import io.check.seckill.domain.model.enums.SeckillOrderStatus;
import io.check.seckill.domain.exception.SeckillException;
import io.check.seckill.domain.model.entity.SeckillGoods;
import io.check.seckill.domain.model.entity.SeckillOrder;
import io.check.seckill.domain.repository.SeckillOrderRepository;
import io.check.seckill.domain.service.SeckillOrderDomainService;
import io.check.seckill.infrastructure.utils.beans.BeanUtil;
import io.check.seckill.infrastructure.utils.id.SnowFlakeFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    @Autowired
    private SeckillOrderDomainService seckillOrderDomainService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeckillOrder saveSeckillOrder(SeckillOrderDTO seckillOrderDTO) {
        // 验证参数是否为空
        if (seckillOrderDTO == null){
            throw new SeckillException(HttpCode.PARAMS_INVALID);
        }
        // 根据商品ID获取秒杀商品信息
        // 获取商品
        SeckillGoods seckillGoods = seckillGoodsService.getSeckillGoodsId(seckillOrderDTO.getGoodsId());
        // 检查秒杀商品是否存在
        // 商品不存在
        if(seckillGoods == null){
            throw new SeckillException(HttpCode.GOODS_NOT_EXISTS);
        }
        // 检查秒杀商品状态，是否已下架或未发布
        // 商品未上线
        if(seckillGoods.getStatus() == SeckillGoodsStatus.PUBLISHED.getCode()){
            throw new SeckillException(HttpCode.GOODS_PUBLISH);
        }
        // 商品已下架
        if(seckillGoods.getStatus() == SeckillGoodsStatus.OFFLINE.getCode()){
            throw new SeckillException(HttpCode.GOODS_OFFLINE);
        }
        // 检查购买数量是否超过商品限购数量
        // 触发限购
        if(seckillGoods.getLimitNum() < seckillOrderDTO.getQuantity()){
            throw new SeckillException(HttpCode.BEYOND_LIMIT_NUM);
        }
        // 检查秒杀商品库存是否充足
        //库存不足
        if(seckillGoods.getAvailableStock() == null || seckillGoods.getAvailableStock()<=0 ||
                seckillOrderDTO.getQuantity() > seckillGoods.getAvailableStock()){
            throw new SeckillException(HttpCode.STOCK_LT_ZERO);
        }
        // 创建秒杀订单对象，并设置相关信息
        SeckillOrder seckillOrder = new SeckillOrder();
        BeanUtil.copyProperties(seckillOrderDTO,seckillOrder);
        seckillOrder.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
        seckillOrder.setGoodsName(seckillGoods.getGoodsName());
        seckillOrder.setActivityPrice(seckillGoods.getActivityPrice());
        // 计算订单总价
        BigDecimal orderPrice =
                seckillGoods.getActivityPrice().multiply(BigDecimal.valueOf(seckillOrder.getQuantity()));
        seckillOrder.setOrderPrice(orderPrice);
        seckillOrder.setStatus(SeckillOrderStatus.CREATED.getCode());
        seckillOrder.setCreateTime(new Date());
        //保存订单
        seckillOrderDomainService.saveSeckillOrder(seckillOrder);
        //扣减库存
        seckillGoodsService.updateAvailableStock(seckillOrderDTO.getQuantity(),seckillOrderDTO.getGoodsId());
        return seckillOrder;

    }

    @Override
    public List<SeckillOrder> getSeckillOrderByUserId(Long userId) {
        return seckillOrderDomainService.getSeckillOrderByUserId(userId);
    }

    @Override
    public List<SeckillOrder> getSeckillOrderByActivityId(Long activityId) {
        return seckillOrderDomainService.getSeckillOrderByActivityId(activityId);
    }
}
