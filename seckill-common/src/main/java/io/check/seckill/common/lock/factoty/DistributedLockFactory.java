package io.check.seckill.common.lock.factoty;

import io.check.seckill.common.lock.DistributedLock;

public interface DistributedLockFactory {
    DistributedLock getDistributedLock(String key);
}
