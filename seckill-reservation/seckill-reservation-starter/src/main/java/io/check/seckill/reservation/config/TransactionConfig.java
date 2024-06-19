package io.check.seckill.reservation.config;

import io.check.seckill.common.config.RedisConfig;
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
@MapperScan(value = {"io.check.seckill.reservation.infrastructure.mapper"})
@ComponentScan(value = {"io.check.seckill", "com.alibaba.cola"})
@EnableTransactionManagement(proxyTargetClass = true)
@Import({RedisConfig.class})
@ServletComponentScan(basePackages = {"io.check.seckill"})
public class TransactionConfig {
}

