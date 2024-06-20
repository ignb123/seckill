package io.check.seckill.ratelimiter.concurrent.policy;

import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.exception.SeckillException;

import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author check
 * @version 1.0.0
 * @description 限流拒绝策略
 */
public class ConcurrentRateLimiterPolicy implements RejectedExecutionHandler {
    @Override
    public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
        throw new SeckillException(ErrorCode.RETRY_LATER);
    }
}