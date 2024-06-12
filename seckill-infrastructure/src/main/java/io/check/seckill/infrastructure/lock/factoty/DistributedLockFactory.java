package io.check.seckill.infrastructure.lock.factoty;

import io.check.seckill.infrastructure.lock.DistributedLock;

public interface DistributedLockFactory {
    DistributedLock getDistributedLock(String key);
}
