package com.abc.art.queue;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SynchronousQueue<T> {
    T item = null;
    boolean enqueuing;
    Lock lock;
    Condition condition;

    SynchronousQueue() {
        lock = new ReentrantLock();
        condition = lock.newCondition();
    }

    public void enq(T value) {
        lock.lock();
        try {
            while (enqueuing) {
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            enqueuing = true;
            item = value;
            condition.signalAll();
            while (item != null) {
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            enqueuing = false;
            condition.signalAll();
        } finally {
            lock.unlock();
        }
    }

    public T deq() {
        lock.lock();
        try {
            while (item == null) {
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            T t = item;
            item = null;
            condition.signalAll();
            return t;
        } finally {
            lock.unlock();
        }
    }
}
