package io.check.seckill.application.service.impl;

import io.check.seckill.application.command.SeckillOrderCommand;
import io.check.seckill.application.order.place.SeckillPlaceOrderService;
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
    private SeckillOrderDomainService seckillOrderDomainService;

    @Autowired
    private SeckillPlaceOrderService seckillPlaceOrderService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        if (seckillOrderCommand == null){
            throw new SeckillException(HttpCode.PARAMS_INVALID);
        }
        return seckillPlaceOrderService.placeOrder(userId, seckillOrderCommand);
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
