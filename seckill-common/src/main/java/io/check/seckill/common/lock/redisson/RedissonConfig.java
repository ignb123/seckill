package io.check.seckill.common.lock.redisson;

import org.redisson.config.ClusterServersConfig;
import org.redisson.config.Config;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;

@Configuration
@ConditionalOnProperty(name = "distributed.lock.type", havingValue = "redisson")
public class RedissonConfig {

    @Value("${spring.redis.address}")
    private String redisAddress;

    /**
     * 单机方式部署
     * @return
     */
    @Bean(name = "redissonClient")
    @ConditionalOnProperty(name = "redis.arrange.type", havingValue = "single")
    public RedissonClient singleRedissonClient() {
        Config config = new Config();
        config.useSingleServer().setAddress(redisAddress).setDatabase(0);
        return Redisson.create(config);
    }

    /**
     * 集群方式部署
     * @return
     */
    @Bean(name = "redissonClient")
    @ConditionalOnProperty(name = "redis.arrange.type", havingValue = "cluster")
    public RedissonClient clusterRedissonClient(){
        Config config = new Config();
        ClusterServersConfig clusterServersConfig = config.useClusterServers();
        clusterServersConfig.setNodeAddresses(Arrays.asList(redisAddress));
        return Redisson.create(config);
    }
}
