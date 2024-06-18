package io.check.seckill.order.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.*;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author check
 * @version 1.0.0
 * @description Spring事务编程配置类
 */
@Configuration
@MapperScan(value = {"io.check.seckill.order.infrastructure.mapper"})
@ComponentScan(value = {"io.check.seckill", "com.alibaba.cola"})
@Import({RedisConfig.class})
@EnableTransactionManagement(proxyTargetClass = true)
@ServletComponentScan(basePackages = {"io.check.seckill"})
public class TransactionConfig {

}
