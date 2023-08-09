package com.abc.art.list;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.locks.ReentrantLock;

public class RefinableHashSet<T> extends BaseHashSet<T> {
    AtomicMarkableReference<Thread> owner;
    volatile ReentrantLock[] locks;

    public RefinableHashSet(int capacity) {
        super(capacity);
        locks = new ReentrantLock[capacity];
        for (int j = 0; j < locks.length; j++) {
            locks[j] = new ReentrantLock();
        }
        owner = new AtomicMarkableReference<Thread>(null, false);
    }

    public void acquire(T x) {
        boolean[] mark = {true};
        Thread me = Thread.currentThread();
        Thread who;
        while (true) {
            do {
                who = owner.get(mark);
            } while (mark[0] && who != me);
            ReentrantLock[] oldLocks = locks;
            ReentrantLock oldLock = oldLocks[x.hashCode() % oldLocks.length];

            oldLock.lock();
            who = owner.get(mark);
            if ((!mark[0] || who == me) && locks == oldLocks) {
                return;
            } else {
                oldLock.unlock();
            }
        }
    }

    public void release(T x) {
        locks[x.hashCode() % locks.length].unlock();
    }

    public void resize() {
        boolean[] mark = {false};
        Thread me = Thread.currentThread();
        if (owner.compareAndSet(null, me, false, true)) {
            try {
                if (!policy()) { // someone else resized first
                    return;
                }
                quiesce();
                int newCapacity = 2 * table.length;
                List<T>[] oldTable = table;
                table = (List<T>[]) new List[newCapacity];
                for (int i = 0; i < newCapacity; i++) {
                    table[i] = new ArrayList<T>();
                }
                locks = new ReentrantLock[newCapacity];
                for (int j = 0; j < locks.length; j++) {
                    locks[j] = new ReentrantLock();
                }
                initializeFrom(oldTable);
            } finally {
                owner.set(null, false);
            }
        }
    }

    @Override
    public boolean policy() {
        return setSize.get() / table.length > 4;
    }

    protected void quiesce() {
        for (ReentrantLock lock : locks) {
            while (lock.isLocked()) {
            }
        }
    }

    void initializeFrom(List[] oldTable) {
        int newCapacity = 2 * table.length;
        table = (List<T>[]) new List[newCapacity];
        for (int i = 0; i < newCapacity; i++) {
            table[i] = new ArrayList<T>();
        }
        for (List<T> bucket : oldTable) {
            for (T x : bucket) {
                table[x.hashCode() % table.length].add(x);
            }
        }
    }
}
