package io.check.seckill.reservation;

import org.apache.dubbo.config.spring.context.annotation.EnableDubbo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author check
 * @version 1.0.0
 * @description 预约系统启动类
 */
@EnableDubbo
@SpringBootApplication
public class SeckillReservationStarter {
    public static void main(String[] args) {
        System.setProperty("user.home", "/home/check/reservation");
        SpringApplication.run(SeckillReservationStarter.class, args);
    }

}
