/**
 * Copyright 2022-9999 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.check.seckill.application.cache.service;



import io.check.seckill.common.cache.model.SeckillBusinessCache;
import io.check.seckill.common.cache.service.SeckillCacheService;
import io.check.seckill.goods.domain.model.entity.SeckillGoods;

import java.util.List;

/**
 * @author check
 * @version 1.0.0
 * @description 商品缓存服务接口
 */
public interface SeckillGoodsListCacheService extends SeckillCacheService {

    /**
     * 获取缓存中的商品列表
     */
    SeckillBusinessCache<List<SeckillGoods>> getCachedGoodsList(Long activityId, Long version);


    /**
     * 更新缓存数据
     */
    SeckillBusinessCache<List<SeckillGoods>> tryUpdateSeckillGoodsCacheByLock(Long activityId, boolean doubleCheck);
}
