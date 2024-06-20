package io.check.seckill.ratelimiter.concurrent.annotation;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;


/**
 * @author check
 * @version 1.0.0
 * @description 并发数限流注解
 */
@Inherited
@Documented
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConcurrentRateLimiter {


    /**
     * 限流器名称，如果不设置，默认是类名加方法名。如果多个接口设置了同一个名称，则使用同一个限流器
     */
    String name();

    /**
     * 核心线程数
     */
    int corePoolSize() default 1;

    /**
     * 最大线程数
     */
    int maximumPoolSize() default 1;

    /**
     * 队列容量
     */
    int queueCapacity() default 1;

    /**
     * 空闲线程存活时间
     */
    long keepAliveTime() default 30;

    /**
     * 空闲线程存活时间单位
     */
    TimeUnit keepAliveTimeUnit() default TimeUnit.SECONDS;

    /**
     * 超时时间
     */
    long timeout() default 1;

    /**
     * 超时时间单位
     */
    TimeUnit timeoutUnit() default TimeUnit.SECONDS;
}
