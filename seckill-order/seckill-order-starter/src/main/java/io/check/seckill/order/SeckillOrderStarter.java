package io.check.seckill.order;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author check
 * @version 1.0.0
 * @description 订单服务启动类
 */
@EnableDubbo
@SpringBootApplication
public class SeckillOrderStarter {
    public static void main(String[] args) {
        System.setProperty("user.home", "/home/check/order");
        SpringApplication.run(SeckillOrderStarter.class, args);
    }
}
