package io.check.seckill.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author check
 * @version 1.0.0
 * @description 用户服务启动类
 */
@SpringBootApplication
public class SeckillUserStarter {

    public static void main(String[] args) {
        System.setProperty("user.home", "/home/check/user");
        SpringApplication.run(SeckillUserStarter.class, args);
    }
}
