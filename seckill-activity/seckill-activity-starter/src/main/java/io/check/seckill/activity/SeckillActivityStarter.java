package io.check.seckill.activity;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @author check
 * @version 1.0.0
 * @description 项目启动类
 */
@EnableDubbo
@EnableDiscoveryClient
@SpringBootApplication
public class SeckillActivityStarter {

    public static void main(String[] args) {
        System.setProperty("user.home", "/home/check/activity");
        SpringApplication.run(SeckillActivityStarter.class, args);
    }

}
