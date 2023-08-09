package com.abc.art.list;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class BaseHashSet<T> {
    static int DEFAUILT_CAPACITY = 64;
    protected List<T>[] table;
    protected AtomicInteger setSize;

    public BaseHashSet(int capacity) {
        setSize = new AtomicInteger(0);
        ;
        table = (List<T>[]) new List[capacity];
        for (int i = 0; i < capacity; i++) {
            table[i] = new ArrayList<T>();
        }
    }

    public abstract void acquire(T x);

    public abstract void release(T x);

    public abstract void resize();

    public abstract boolean policy();

    public boolean contains(T x) {
        acquire(x);
        try {
            int myBucket = x.hashCode() % table.length;
            return table[myBucket].contains(x);
        } finally {
            release(x);
        }
    }

    public boolean add(T x) {
        boolean result = false;
        acquire(x);
        try {
            int myBucket = Math.abs(x.hashCode() % table.length);
            if (!table[myBucket].contains(x)) {
                table[myBucket].add(x);
                result = true;
                //size++;
                setSize.getAndIncrement();
            }
        } finally {
            release(x);
        }
        if (policy())
            resize();
        return result;
    }
}
