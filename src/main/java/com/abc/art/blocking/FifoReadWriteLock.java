package com.abc.art.blocking;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FifoReadWriteLock implements ReadWriteLock {
    int readAcquires, readReleases;
    boolean writer;
    Lock lock;
    Condition condition;
    Lock readLock, writeLock; public FifoReadWriteLock() {
        readAcquires = readReleases = 0; writer = false;
        lock = new ReentrantLock(); condition = lock.newCondition();
        readLock = new ReadLock();
        writeLock = new WriteLock();
    }
    public Lock readLock() {
        return readLock; }
    public Lock writeLock() { return writeLock;
    }

    private class ReadLock implements Lock {
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
                readAcquires++;
            }finally{
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

        public void unlock() {
            lock.lock();
            try {
                readReleases++;
                if (readAcquires == readReleases) {
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
    }

    private class WriteLock implements Lock {
        public void lock() {
            lock.lock();
            try {

            while (writer) {
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            writer = true;
            while (readAcquires != readReleases) {
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
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

        public void unlock() {
            lock.lock();
            try {
                writer = false; condition.signalAll();
            }finally {
                lock.unlock();
            }
        }

        @Override
        public Condition newCondition() {
            return null;
        }


    }
}
