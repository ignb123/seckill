package io.check.seckill.order.application.service;

/**
 * @author check
 * @version 1.0.0
 * @description 生成订单下单请求标识
 */
public interface OrderTaskGenerateService {

    /**
     * 生成下单标识
     * @param userId 用户id
     * @param goodsId 商品id
     * @return 下单标识
     */
    String generatePlaceOrderTaskId(Long userId, Long goodsId);
}
