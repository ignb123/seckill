package io.check.seckill.goods.config;

import com.alibaba.druid.pool.DruidDataSource;
import io.check.seckill.common.config.JdbcConfig;
import io.check.seckill.common.config.MyBatisConfig;
import io.check.seckill.common.config.RedisConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
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
@MapperScan(value = {"io.check.seckill.goods.infrastructure.mapper"})
@ComponentScan(value = {"io.check.seckill", "com.alibaba.cola"})
@PropertySource(value = {"classpath:properties/mysql.properties", "classpath:properties/mybatis.properties"})
@Import({JdbcConfig.class, RedisConfig.class, MyBatisConfig.class})
@EnableTransactionManagement(proxyTargetClass = true)
@ServletComponentScan(basePackages = {"io.check.seckill"})
public class TransactionConfig {

    @Bean
    public TransactionManager transactionManager(DruidDataSource dataSource){
        return new DataSourceTransactionManager(dataSource);
    }
}
