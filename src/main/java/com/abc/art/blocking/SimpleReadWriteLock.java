package com.abc.art.blocking;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleReadWriteLock implements ReadWriteLock {

    int readers;
    boolean writer;
    Lock lock;
    Condition condition;
    Lock readLock, writeLock;

    public SimpleReadWriteLock() {
        writer = false;
        readers = 0;
        lock = new ReentrantLock();
        readLock = new ReadLock();
        writeLock = new WriteLock();
        condition = lock.newCondition();
    }

    public Lock readLock() {
        return readLock;
    }

    public Lock writeLock() {
        return writeLock;
    }

    class WriteLock implements Lock {
        public void lock() {
            lock.lock();
            try {
                while (readers > 0 || writer){
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                writer = true;
            }finally {
                lock.unlock();
            }
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {

        }

        @Override
        public boolean tryLock() {
            return false;
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            return false;
        }

        @Override
        public void unlock() {

        }

        @Override
        public Condition newCondition() {
            return null;
        }
    }
    class ReadLock implements Lock {
        public void lock() {
            lock.lock();
            try {
                while (writer){
                    try {
                        condition.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                readers++;
            }finally{
                    lock.unlock();
            }
        }

        public void unlock() {
            lock.lock();
            try {
                readers--;
                if (readers == 0) {
                    condition.signalAll();
                }
            }finally{
                lock.unlock();
            }
        }

        @Override
        public Condition newCondition() {
            return null;
        }

        @Override
        public void lockInterruptibly() throws InterruptedException {

        }

        @Override
        public boolean tryLock() {
            return false;
        }

        @Override
        public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
            return false;
        }

    }
}
