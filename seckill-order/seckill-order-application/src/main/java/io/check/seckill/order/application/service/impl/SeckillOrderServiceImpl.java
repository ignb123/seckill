package io.check.seckill.order.application.service.impl;

import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.exception.SeckillException;
import io.check.seckill.order.application.command.SeckillOrderCommand;
import io.check.seckill.order.application.place.SeckillPlaceOrderService;
import io.check.seckill.order.application.security.SecurityService;
import io.check.seckill.order.application.service.SeckillOrderService;
import io.check.seckill.order.domain.model.entity.SeckillOrder;
import io.check.seckill.order.domain.service.SeckillOrderDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author check
 * @version 1.0.0
 * @description 订单业务
 */
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private SeckillOrderDomainService seckillOrderDomainService;
    @Autowired
    private SeckillPlaceOrderService seckillPlaceOrderService;
    @Autowired
    private SecurityService securityService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        if (userId == null || seckillOrderCommand == null){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        //模拟风控
        if (!securityService.securityPolicy(userId)){
            throw new SeckillException(ErrorCode.USER_INVALID);
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
