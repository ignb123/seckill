package io.check.seckill.common.utils.time;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author check
 * @version 1.0.0
 * @description 时间戳性能优化
 */
public class SystemClock {

    /**
     * 线程名称，用于系统时钟更新线程。
     */
    private static final String THREAD_NAME = "system.clock";

    /**
     * 毫秒精度的系统时钟实例。
     */
    private static final SystemClock MILLIS_CLOCK = new SystemClock(1);

    /**
     * 时间精度，以毫秒为单位。
     */
    private final long precision;

    /**
     * 用于存储当前时间戳的原子长整型变量。
     */
    private final AtomicLong now;

    /**
     * 私有构造方法，初始化系统时钟。
     *
     * @param precision 时间精度，以毫秒为单位。
     */
    private SystemClock(long precision) {
        this.precision = precision;
        now = new AtomicLong(System.currentTimeMillis());
        scheduleClockUpdating();
    }

    /**
     * 提供一个用于获取毫秒精度系统时钟的静态方法。
     *
     * @return SystemClock 的实例，用于获取当前时间戳。
     */
    public static SystemClock millisClock() {
        return MILLIS_CLOCK;
    }

    /**
     * 定期更新当前时间戳。
     * 该方法利用ScheduledExecutorService在单独的线程中定期更新now变量的值。
     */
    private void scheduleClockUpdating() {
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor(runnable -> {
            Thread thread = new Thread(runnable, THREAD_NAME);
            thread.setDaemon(true);
            return thread;
        });
        scheduler.scheduleAtFixedRate(() ->
                now.set(System.currentTimeMillis()), precision, precision, TimeUnit.MILLISECONDS);
    }

    /**
     * 获取当前时间戳。
     *
     * @return 当前时间戳，以毫秒为单位。
     */
    public long now() {
        return now.get();
    }
}


