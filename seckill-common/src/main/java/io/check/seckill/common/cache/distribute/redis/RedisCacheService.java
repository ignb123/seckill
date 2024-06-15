package io.check.seckill.common.cache.distribute.redis;

import com.alibaba.fastjson.JSON;
import io.check.seckill.common.cache.distribute.DistributedCacheService;
import io.check.seckill.common.constants.SeckillConstants;
import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.exception.SeckillException;
import io.check.seckill.common.utils.serializer.ProtoStuffSerializerUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;


import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
@ConditionalOnProperty(name = "distributed.cache.type", havingValue = "redis")
public class RedisCacheService implements DistributedCacheService {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 初始化库存操作的Redis脚本。
     * 这些脚本用于对库存进行原子操作，确保在高并发环境下的库存操作准确性。
     * DECREASE_STOCK_SCRIPT: 用于减少库存的脚本。
     * INCREASE_STOCK_SCRIPT: 用于增加库存的脚本。
     * INIT_STOCK_SCRIPT: 用于初始化库存的脚本。
     */
    private static final DefaultRedisScript<Long> DECREASE_STOCK_SCRIPT;
    private static final DefaultRedisScript<Long> INCREASE_STOCK_SCRIPT;
    private static final DefaultRedisScript<Long> INIT_STOCK_SCRIPT;

    static {
        //扣减库存
        DECREASE_STOCK_SCRIPT = new DefaultRedisScript<>();
        DECREASE_STOCK_SCRIPT.setLocation(new ClassPathResource("lua/decrement_goods_stock.lua"));
        DECREASE_STOCK_SCRIPT.setResultType(Long.class);

        //增加库存
        INCREASE_STOCK_SCRIPT = new DefaultRedisScript<>();
        INCREASE_STOCK_SCRIPT.setLocation(new ClassPathResource("lua/increment_goods_stock.lua"));
        INCREASE_STOCK_SCRIPT.setResultType(Long.class);

        //初始化库存
        INIT_STOCK_SCRIPT = new DefaultRedisScript<>();
        INIT_STOCK_SCRIPT.setLocation(new ClassPathResource("lua/init_goods_stock.lua"));
        INIT_STOCK_SCRIPT.setResultType(Long.class);
    }

    @Override
    public void put(String key, String value) {
        if (StringUtils.isEmpty(key) || value == null) {
            return;
        }
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void put(String key, Object value) {
        if (StringUtils.isEmpty(key) || value == null) {
            return;
        }
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void put(String key, Object value, long timeout, TimeUnit unit) {
        if (StringUtils.isEmpty(key) || value == null) {
            return;
        }
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    @Override
    public void put(String key, Object value, long expireTime) {
        if (StringUtils.isEmpty(key) || value == null) {
            return;
        }
        redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
    }

    @Override
    public <T> T getObject(String key, Class<T> targetClass) {
        Object result = redisTemplate.opsForValue().get(key);
        if (result == null) {
            return null;
        }
        try {
            return JSON.parseObject(result.toString() , targetClass);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Object getObject(String key) {
        return redisTemplate.opsForValue().get(key);
    }


    @Override
    public String getString(String key) {
        Object result = redisTemplate.opsForValue().get(key);
        if (result == null) {
            return null;
        }
        return String.valueOf(result);
    }

    @Override
    public <T> List<T> getList(String key, Class<T> targetClass) {
        Object result = redisTemplate.execute((RedisCallback<Object>) connection ->
                connection.get(key.getBytes()));
        if (result == null) {
            return null;
        }
        return ProtoStuffSerializerUtils.deserializeList(String.valueOf(result).getBytes(), targetClass);
    }

    @Override
    public Boolean delete(String key) {
        if (StringUtils.isEmpty(key)) {
            return false;
        }
        return redisTemplate.delete(key);
    }

    @Override
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    @Override
    public Long decrement(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }
    @Override
    public Long increment(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    @Override
    public Long decrementByLua(String key, Integer quantity) {
        return redisTemplate.execute(DECREASE_STOCK_SCRIPT, Collections.singletonList(key), quantity);
    }

    @Override
    public Long incrementByLua(String key, Integer quantity) {
        return redisTemplate.execute(INCREASE_STOCK_SCRIPT, Collections.singletonList(key), quantity);
    }

    @Override
    public Long initByLua(String key, Integer quantity) {
        return redisTemplate.execute(INIT_STOCK_SCRIPT, Collections.singletonList(key), quantity);
    }

    @Override
    public void checkResult(Long result) {
        if (result == SeckillConstants.LUA_RESULT_GOODS_STOCK_NOT_EXISTS) {
            throw new SeckillException(ErrorCode.STOCK_IS_NULL);
        }
        if (result == SeckillConstants.LUA_RESULT_GOODS_STOCK_PARAMS_LT_ZERO){
            throw new SeckillException(ErrorCode.PARAMS_INVALID);
        }
        if (result == SeckillConstants.LUA_RESULT_GOODS_STOCK_LT_ZERO){
            throw new SeckillException(ErrorCode.STOCK_LT_ZERO);
        }
    }
}
