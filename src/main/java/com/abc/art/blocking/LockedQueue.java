package com.abc.art.blocking;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockedQueue<T> {
    final Lock lock = new ReentrantLock();
    final Condition notFull = lock.newCondition();
    final Condition notEmpty = lock.newCondition();
    T[] items;
    int tail, head, count;
    LockedQueue(int capacity) {
        items = (T[])new Object[capacity];
    }

    public void enq(T x) { lock.lock();
        try {
            while (count == items.length) {
                try {
                    notFull.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            items[tail] = x;
            if (++tail == items.length) {
                tail = 0;
            }
            ++count;
            notEmpty.signal();
        } finally { lock.unlock();
        }
    }
    public T deq() {
        lock.lock();
        try {
        while (count == 0) {
            try {
                notEmpty.await();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        T x = items[head];
        if (++head == items.length) {
            head = 0;
        }
        --count;
        notFull.signal();
        return x;
    } finally { lock.unlock();
    } }
}
