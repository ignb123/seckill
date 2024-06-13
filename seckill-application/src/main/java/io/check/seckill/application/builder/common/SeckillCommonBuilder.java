package io.check.seckill.application.builder.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.check.seckill.application.cache.model.SeckillBusinessCache;

import java.util.List;

/**
 * @author check
 * @version 1.0.0
 * @description
 */
public class SeckillCommonBuilder {
    /**
     * Json泛型化处理
     */
    public static <T> SeckillBusinessCache<T> getSeckillBusinessCache(Object object, Class<T> clazz){
        if (object == null){
            return null;
        }
        return JSON.parseObject(object.toString(), new TypeReference<SeckillBusinessCache<T>>(clazz){});
    }

    /**
     * Json泛型化处理
     */
    public static <T> SeckillBusinessCache<List<T>> getSeckillBusinessCacheList(Object object, Class<T> clazz){
        if (object == null){
            return null;
        }
        return JSON.parseObject(object.toString(), new TypeReference<SeckillBusinessCache<List<T>>>(clazz){});
    }
}
