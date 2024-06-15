package io.check.seckill.application.builder;

import io.check.seckill.application.command.SeckillGoodsCommond;
import io.check.seckill.common.builder.SeckillCommonBuilder;
import io.check.seckill.common.model.dto.SeckillGoodsDTO;
import io.check.seckill.common.utils.beans.BeanUtil;
import io.check.seckill.goods.domain.model.entity.SeckillGoods;

/**
 * @author check
 * @version 1.0.0
 * @description 秒杀商品转化类
 */
public class SeckillGoodsBuilder extends SeckillCommonBuilder {

    public static SeckillGoods toSeckillGoods(SeckillGoodsCommond seckillGoodsCommond){
        if (seckillGoodsCommond == null){
            return null;
        }
        SeckillGoods seckillGoods = new SeckillGoods();
        BeanUtil.copyProperties(seckillGoodsCommond, seckillGoods);
        return seckillGoods;
    }

    public static SeckillGoodsDTO toSeckillGoodsDTO(SeckillGoods seckillGoods){
        if (seckillGoods == null){
            return null;
        }
        SeckillGoodsDTO seckillGoodsDTO = new SeckillGoodsDTO();
        BeanUtil.copyProperties(seckillGoods, seckillGoodsDTO);
        return seckillGoodsDTO;
    }
}
