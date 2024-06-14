package io.check.seckill.infrastructure.cache.local.guava;

import com.google.common.cache.Cache;
import io.check.seckill.infrastructure.cache.local.LocalCacheService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Service
/**
 * 只有当属性local.cache.type的值为"guava"时，该方法才会生效。
 * 这种条件注解使得我们可以根据配置动态地启用或禁用特定的缓存策略，
 * 提高了系统的灵活性和可配置性。
 */
@ConditionalOnProperty(name = "local.cache.type", havingValue = "guava")
public class GuavaLocalCacheService<K,V> implements LocalCacheService<K,V> {

    //本地缓存，基于Guava实现
    private final Cache<K, V> cache = LocalCacheFactory.getLocalCache();
    @Override
    public void put(K key, V value) {
        cache.put(key, value);
    }

    @Override
    public V getIfPresent(Object key) {
        return cache.getIfPresent(key);
    }

    @Override
    public void delete(Object key) {
        cache.invalidate(key);
    }

}
