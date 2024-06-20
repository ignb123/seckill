package io.check.seckill.ratelimiter.concurrent.bean;

import io.check.seckill.common.exception.ErrorCode;
import io.check.seckill.common.exception.SeckillException;

import java.util.concurrent.Callable;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author check
 * @version 1.0.0
 * @description 封装的线程池并发数限流器
 */
public class CHECKConcurrentRateLimiter {

    //线程池对象
    private ThreadPoolExecutor executor;

    //超时时间
    private long timeout;

    //超时时间单位
    private TimeUnit timeoutUnit;

    public CHECKConcurrentRateLimiter() {
    }

    public CHECKConcurrentRateLimiter(ThreadPoolExecutor executor, long timeout, TimeUnit timeoutUnit) {
        this.executor = executor;
        this.timeout = timeout;
        this.timeoutUnit = timeoutUnit;
    }

    public ThreadPoolExecutor getExecutor() {
        return executor;
    }

    public void setExecutor(ThreadPoolExecutor executor) {
        this.executor = executor;
    }

    public long getTimeout() {
        return timeout;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }

    public TimeUnit getTimeoutUnit() {
        return timeoutUnit;
    }

    public void setTimeoutUnit(TimeUnit timeoutUnit) {
        this.timeoutUnit = timeoutUnit;
    }

    public void execute(Runnable command){
        executor.execute(command);
    }

    public <T> T submit(Callable<T> task) {
        try {
            return executor.submit(task).get();
        }catch (Exception e){
            if (e instanceof SeckillException){
                SeckillException se = (SeckillException) e;
                throw se;
            }
            throw new SeckillException(ErrorCode.SERVER_EXCEPTION);
        }
    }

    public <T> T submitWithTimeout(Callable<T> task) {
        try {
            return executor.submit(task).get(timeout,timeoutUnit);
        }catch (Exception e){
            if (e instanceof SeckillException){
                SeckillException se = (SeckillException) e;
                throw se;
            }
            throw new SeckillException(ErrorCode.SERVER_EXCEPTION);
        }
    }

    public void shutdown(){
        if (executor != null){
            executor.shutdown();
        }
    }

}
