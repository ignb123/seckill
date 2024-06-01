package io.check.seckill.starter.config;

import com.alibaba.druid.pool.DruidDataSource;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.*;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author check
 * @version 1.0.0
 * @description Spring事务编程配置类
 */
@Configuration
@MapperScan(value = {"io.check.seckill.infrastructure.mapper"})
@ComponentScan(value = {"io.check.seckill"})
@PropertySource(value = {"classpath:properties/jdbc.properties", "classpath:properties/mybatis.properties"})
@Import({JdbcConfig.class, MyBatisConfig.class, RedisConfig.class})
@EnableTransactionManagement(proxyTargetClass = true)
public class TransactionConfig {

    @Bean
    public TransactionManager transactionManager(DruidDataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }
}

