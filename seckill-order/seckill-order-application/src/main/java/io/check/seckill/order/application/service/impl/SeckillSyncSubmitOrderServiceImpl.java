package io.check.seckill.order.application.service.impl;

import io.check.seckill.common.constants.SeckillConstants;
import io.check.seckill.common.model.dto.SeckillOrderSubmitDTO;
import io.check.seckill.order.application.model.command.SeckillOrderCommand;
import io.check.seckill.order.application.service.SeckillSubmitOrderService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@ConditionalOnProperty(name = "submit.order.type", havingValue = "sync")
public class SeckillSyncSubmitOrderServiceImpl extends SeckillBaseSubmitOrderServiceImpl implements SeckillSubmitOrderService {
    @Override
    @Transactional(rollbackFor = Exception.class)
    public SeckillOrderSubmitDTO saveSeckillOrder(Long userId, SeckillOrderCommand seckillOrderCommand) {
        //进行基本的检查
        this.checkSeckillOrder(userId, seckillOrderCommand);
        return new SeckillOrderSubmitDTO(String.valueOf(seckillPlaceOrderService.placeOrder(userId, seckillOrderCommand)),
                seckillOrderCommand.getGoodsId(), SeckillConstants.TYPE_ORDER);
    }
}
