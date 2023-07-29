package com.abc.art.queue;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedQueue<T> {
    ReentrantLock enqLock, deqLock;
    Condition notEmptyCondition, notFullCondition;
    AtomicInteger size;
    volatile Node head, tail;
    final int capacity;
    public BoundedQueue() {
        this(10);
    }
    public BoundedQueue(int _capacity) {
        capacity = _capacity;
        head = new Node(null);
        tail = head;
        size = new AtomicInteger(0);
        enqLock = new ReentrantLock();
        notFullCondition = enqLock.newCondition();
        deqLock = new ReentrantLock();
        notEmptyCondition = deqLock.newCondition();
    }

    public void enq(T x) {
        boolean mustWakeDequeuers = false;
        Node e = new Node(x);
        enqLock.lock();
        try {
            while (size.get() == capacity) {
                try {
                    notFullCondition.await();
                } catch (InterruptedException ex) {
                    throw new RuntimeException(ex);
                }
            }
            tail.next = e;
            tail = e;
            if (size.getAndIncrement() == 0)
                mustWakeDequeuers = true; } finally {
            enqLock.unlock(); }
        if (mustWakeDequeuers) { deqLock.lock();
            try {
                notEmptyCondition.signalAll();
            } finally {
                deqLock.unlock();
            }
        }
    }

    public T deq() {
        T result;
        boolean mustWakeEnqueuers = false; deqLock.lock();
        try {
            while (head.next == null) {
                try {
                    notEmptyCondition.await();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
            result = (T)head.next.value;
            head = head.next;
            if (size.getAndDecrement() == capacity) {
                mustWakeEnqueuers = true; }
        } finally {
            deqLock.unlock();
        }
        if (mustWakeEnqueuers) {
            enqLock.lock();
            try {
                notFullCondition.signalAll();
            } finally {
                enqLock.unlock();
            }
        }
        return result;
    }

    static class Node<T> {
        public T value;
        public volatile Node next;

        public Node(T x) {
            value = x;
            next = null;
        }
    }
}
