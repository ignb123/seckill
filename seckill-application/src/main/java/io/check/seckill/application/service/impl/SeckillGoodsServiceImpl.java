package io.check.seckill.application.service.impl;

import io.check.seckill.application.builder.SeckillGoodsBuilder;
import io.check.seckill.application.cache.model.SeckillBusinessCache;
import io.check.seckill.application.cache.service.goods.SeckillGoodsCacheService;
import io.check.seckill.application.cache.service.goods.SeckillGoodsListCacheService;
import io.check.seckill.application.service.SeckillGoodsService;
import io.check.seckill.domain.code.HttpCode;
import io.check.seckill.domain.model.dto.SeckillGoodsDTO;
import io.check.seckill.domain.model.enums.SeckillGoodsStatus;
import io.check.seckill.domain.exception.SeckillException;
import io.check.seckill.domain.model.entity.SeckillActivity;
import io.check.seckill.domain.model.entity.SeckillGoods;
import io.check.seckill.domain.service.SeckillActivityDomainService;
import io.check.seckill.domain.service.SeckillGoodsDomainService;
import io.check.seckill.infrastructure.utils.beans.BeanUtil;
import io.check.seckill.infrastructure.utils.id.SnowFlakeFactory;
import io.check.seckill.infrastructure.utils.time.SystemClock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author check
 * @version 1.0.0
 * @description 商品服务
 */
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {
    @Autowired
    private SeckillGoodsDomainService seckillGoodsDomainService;
    @Autowired
    private SeckillActivityDomainService seckillActivityDomainService;

    @Autowired
    private SeckillGoodsListCacheService seckillGoodsListCacheService;

    @Autowired
    private SeckillGoodsCacheService seckillGoodsCacheService;
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveSeckillGoods(SeckillGoodsDTO seckillGoodsDTO) {
        if (seckillGoodsDTO == null){
            throw new SeckillException(HttpCode.PARAMS_INVALID);
        }
        SeckillActivity seckillActivity = seckillActivityDomainService
                .getSeckillActivityById(seckillGoodsDTO.getActivityId());
        if (seckillActivity == null){
            throw new SeckillException(HttpCode.ACTIVITY_NOT_EXISTS);
        }
        SeckillGoods seckillGoods = new SeckillGoods();
        BeanUtil.copyProperties(seckillGoodsDTO, seckillGoods);
        seckillGoods.setStartTime(seckillActivity.getStartTime());
        seckillGoods.setEndTime(seckillActivity.getEndTime());
        seckillGoods.setAvailableStock(seckillGoodsDTO.getInitialStock());
        seckillGoods.setId(SnowFlakeFactory.getSnowFlakeFromCache().nextId());
        seckillGoods.setStatus(SeckillGoodsStatus.PUBLISHED.getCode());
        seckillGoodsDomainService.saveSeckillGoods(seckillGoods);
    }

    @Override
    public SeckillGoods getSeckillGoodsId(Long id) {
        return seckillGoodsDomainService.getSeckillGoodsId(id);
    }


    @Override
    public List<SeckillGoods> getSeckillGoodsByActivityId(Long activityId) {
        return seckillGoodsDomainService.getSeckillGoodsByActivityId(activityId);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateStatus(Integer status, Long id) {
        seckillGoodsDomainService.updateStatus(status, id);
    }

    @Override
    public void updateAvailableStock(Integer count, Long id) {
        seckillGoodsDomainService.updateAvailableStock(count, id);
    }

    @Override
    public Integer getAvailableStockById(Long id) {
        return seckillGoodsDomainService.getAvailableStockById(id);
    }

    @Override
    public List<SeckillGoodsDTO> getSeckillGoodsList(Long activityId, Long version) {
        if(activityId == null){
            throw new SeckillException(HttpCode.ACTIVITY_NOT_EXISTS);
        }
        SeckillBusinessCache<List<SeckillGoods>> seckillGoodsListCache =
                seckillGoodsListCacheService.getCachedGoodsList(activityId, version);
        if(!seckillGoodsListCache.isExist()){
            throw new SeckillException(HttpCode.ACTIVITY_NOT_EXISTS);
        }
        //稍后再试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后再试
        if (seckillGoodsListCache.isRetryLater()){
            throw new SeckillException(HttpCode.RETRY_LATER);
        }
        List<SeckillGoodsDTO> seckillActivityDTOList = seckillGoodsListCache.getData().stream()
                .map(seckillGoods -> {
                    SeckillGoodsDTO seckillGoodsDTO = new SeckillGoodsDTO();
                    BeanUtil.copyProperties(seckillGoods, seckillGoodsDTO);
                    seckillGoodsDTO.setVersion(seckillGoodsListCache.getVersion());
                    return seckillGoodsDTO;
                })
                .collect(Collectors.toList());

        return seckillActivityDTOList;
    }

    @Override
    public SeckillGoodsDTO getSeckillGoods(Long id, Long version) {
        if (id == null){
            throw new SeckillException(HttpCode.PARAMS_INVALID);
        }
        SeckillBusinessCache<SeckillGoods> seckillGoodsCache = seckillGoodsCacheService.getSeckillGoods(id, version);
        //缓存中不存在商品数据
        if (!seckillGoodsCache.isExist()){
            throw new SeckillException(HttpCode.ACTIVITY_NOT_EXISTS);
        }
        //稍后再试，前端需要对这个状态做特殊处理，即不去刷新数据，静默稍后再试
        if (seckillGoodsCache.isRetryLater()){
            throw new SeckillException(HttpCode.RETRY_LATER);
        }
        SeckillGoodsDTO seckillGoodsDTO = SeckillGoodsBuilder.toSeckillGoodsDTO(seckillGoodsCache.getData());
        seckillGoodsDTO.setVersion(SystemClock.millisClock().now());
        return seckillGoodsDTO;
    }
}

