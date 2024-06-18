package io.check.seckill.reservation.infrastructure.mapper;


import io.check.seckill.reservation.domain.model.entity.SeckillReservationConfig;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author check
 * @version 1.0.0
 * @description SeckillReservationConfigMapper接口
 */

public interface SeckillReservationConfigMapper {

    /**
     * 保存预约配置
     */
    int saveSeckillReservationConfig(SeckillReservationConfig seckillReservationConfig);

    /**
     * 更新预约配置
     */
    int updateSeckillReservationConfig(SeckillReservationConfig seckillReservationConfig);

    /**
     * 更新状态
     */
    int updateStatus(@Param("status") Integer status, @Param("goodsId") Long goodsId);

    /**
     * 获取配置列表
     */
    List<SeckillReservationConfig> getConfigList();

    /**
     * 获取配置详情
     */
    SeckillReservationConfig getConfigDetail(@Param("goodsId") Long goodsId);
}
