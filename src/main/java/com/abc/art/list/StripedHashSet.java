package com.abc.art.list;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class StripedHashSet<T> extends BaseHashSet<T> {
    final Lock[] locks;

    StripedHashSet(int capacity) {
        super(capacity);
        locks = new ReentrantLock[capacity];
        for (int j = 0; j < locks.length; j++) {
            locks[j] = new ReentrantLock();
        }
    }

    @Override
    public void acquire(T x) {
        locks[x.hashCode() % locks.length].lock();
    }

    @Override
    public void release(T x) {
        locks[x.hashCode() % locks.length].unlock();
    }

    @Override
    public boolean policy() {
        return setSize.get() / table.length > 4;
    }

    public void resize() {
        for (Lock lock : locks) {
            lock.lock();
        }
        try {
            if (!policy()) {
                return; // someone beat us to it
            }
            int newCapacity = 2 * table.length;
            List<T>[] oldTable = table;
            table = (List<T>[]) new List[newCapacity];
            for (int i = 0; i < newCapacity; i++) {
                table[i] = new ArrayList<T>();
            }
            for (List<T> bucket : oldTable) {
                for (T x : bucket) {
                    table[x.hashCode() % table.length].add(x);
                }
            }

        } finally {
            for (Lock lock : locks) {
                lock.unlock();
            }
        }
    }

}
