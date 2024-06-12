package io.check.seckill.infrastructure.cache.local.guava;

import com.google.common.cache.CacheBuilder;

import java.util.concurrent.TimeUnit;
import com.google.common.cache.Cache;

public class LocalCacheFactory {
    public static <K, V> Cache<K, V> getLocalCache(){
        return CacheBuilder.newBuilder()
                .initialCapacity(15)
                .concurrencyLevel(5)
                .expireAfterWrite(5, TimeUnit.SECONDS)
                .build();
    }
}
