package com.abc.art.blocking;

import java.util.concurrent.locks.Lock;

public interface ReadWriteLock {
    Lock readLock();
    Lock writeLock();
}
