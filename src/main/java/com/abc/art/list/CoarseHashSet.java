package com.abc.art.list;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CoarseHashSet<T> extends BaseHashSet<T> {


    final Lock lock = new ReentrantLock();

    public CoarseHashSet() {
        this(DEFAUILT_CAPACITY);
    }

    public CoarseHashSet(int capacity) {
        super(capacity);
    }

    @Override
    public void acquire(T x) {
        lock.lock();
    }

    @Override
    public void release(T x) {
        lock.unlock();
    }

    @Override
    public void resize() {
        lock.lock();
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
            lock.unlock();
        }
    }

    @Override
    public boolean policy() {
        return setSize.get() / table.length > 4;
    }
}
