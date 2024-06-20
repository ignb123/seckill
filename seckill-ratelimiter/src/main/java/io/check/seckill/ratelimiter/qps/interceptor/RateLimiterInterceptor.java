package io.check.seckill.ratelimiter.qps.interceptor;

import cn.hutool.core.util.StrUtil;
import com.google.common.util.concurrent.RateLimiter;
import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.exception.SeckillException;
import io.check.seckill.ratelimiter.qps.annotation.SeckillRateLimiter;
import io.check.seckill.ratelimiter.qps.bean.CHECKRateLimiter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;


/**
 * @author check
 * @version 1.0.0
 * @description 限流切面
 */
@Aspect
@Component
@ConditionalOnProperty(prefix = "rate.limit.local", name = "enabled", havingValue = "true")
public class RateLimiterInterceptor implements EnvironmentAware {

    private final Logger logger = LoggerFactory.getLogger(RateLimiterInterceptor.class);

    /**
     * 存储检查率限制器的映射，键为资源标识符，值为检查率限制器实例。
     * 使用ConcurrentHashMap以确保线程安全，在并发场景下高效地访问和更新映射内容。
     */
    private static final Map<String, CHECKRateLimiter> CHECK_RATE_LIMITER_MAP = new ConcurrentHashMap<>();

    /**
     * 环境对象，用于获取应用程序的配置属性。
     */
    private Environment environment;

    /**
     * 默认每秒允许的请求数量，用于没有明确配置的情况。
     * 通过Spring的@Value注解从应用程序属性文件中动态获取，默认值为1000。
     */
    @Value("${rate.limit.local.default.permitsPerSecond:1000}")
    private double defaultPermitsPerSecond;

    /**
     * 默认超时时间，用于没有明确配置的情况。
     * 通过Spring的@Value注解从应用程序属性文件中动态获取，默认值为1秒。
     */
    @Value("${rate.limit.local.default.timeout:1}")
    private long defaultTimeout;

    /**
     * 定义切点，用于拦截标记了SeckillRateLimiter注解的方法。
     */
    @Pointcut("@annotation(seckillRateLimiter)")
    public void pointCut(SeckillRateLimiter seckillRateLimiter){

    }

    /**
     * 基于AOP的秒杀限流处理方法。
     * 在指定的切点（pointCut(seckillRateLimiter)定义的逻辑）上执行，对方法执行前后的流程进行控制。
     * 主要功能是限制秒杀操作的频率，以保护系统免受过多请求的影响。
     */
    @Around(value = "pointCut(seckillRateLimiter)")
    public Object around(ProceedingJoinPoint pjp, SeckillRateLimiter seckillRateLimiter) throws Throwable {
        // 获取方法签名信息，用于构建限流器的名称。
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        // 获取目标类的简单名称。
        String className = pjp.getTarget().getClass().getSimpleName();
        // 获取目标方法的名称。
        String methodName = signature.getName();
        // 解析限流器的名称，如果解析失败或包含占位符，则使用类名-方法名作为默认名称。
        String rateLimitName = environment.resolvePlaceholders(seckillRateLimiter.name());
        if (StrUtil.isEmpty(rateLimitName) || rateLimitName.contains("${")) {
            rateLimitName = className + "-" + methodName;
        }
        // 根据限流器名称和配置获取具体的限流器实例。
        CHECKRateLimiter rateLimiter = this.getRateLimiter(rateLimitName, seckillRateLimiter);
        // 尝试获取限流器的许可，如果成功则允许继续执行目标方法。
        boolean success = rateLimiter.tryAcquire();
        if (success){
            // 如果获取许可成功，则继续执行目标方法。
            return pjp.proceed();
        }
        // 如果获取许可失败，则记录错误日志并抛出秒杀异常，提示用户稍后再试。
        logger.error("around|访问接口过于频繁|{}|{}", className, methodName);
        throw new SeckillException(ErrorCode.RETRY_LATER);
    }


    /**
     * 获取CHECKRateLimiter对象
     */
    private CHECKRateLimiter getRateLimiter(String rateLimitName, SeckillRateLimiter seckillRateLimiter) {
        //先从Map缓存中获取
        CHECKRateLimiter checkRateLimiter = CHECK_RATE_LIMITER_MAP.get(rateLimitName);
        //如果获取的checkRateLimiter为空，则创建checkRateLimiter，注意并发，创建的时候需要加锁
        if (checkRateLimiter == null){
            final String finalRateLimitName = rateLimitName.intern();
            synchronized (finalRateLimitName){
                //double check
                checkRateLimiter = CHECK_RATE_LIMITER_MAP.get(rateLimitName);
                //获取的checkRateLimiter再次为空
                if (checkRateLimiter == null){
                    double permitsPerSecond = seckillRateLimiter.permitsPerSecond() <= 0 ? defaultPermitsPerSecond : seckillRateLimiter.permitsPerSecond();
                    long timeout = seckillRateLimiter.timeout() <= 0 ? defaultTimeout : seckillRateLimiter.timeout();
                    TimeUnit timeUnit = seckillRateLimiter.timeUnit();
                    checkRateLimiter = new CHECKRateLimiter(RateLimiter.create(permitsPerSecond), timeout, timeUnit);
                    CHECK_RATE_LIMITER_MAP.putIfAbsent(rateLimitName, checkRateLimiter);
                }
            }
        }
        return checkRateLimiter;
    }


    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }


}
