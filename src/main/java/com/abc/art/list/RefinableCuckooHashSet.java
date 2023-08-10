package com.abc.art.list;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicMarkableReference;
import java.util.concurrent.locks.ReentrantLock;

public class RefinableCuckooHashSet<T> extends PhasedCuckooHashSet<T>{
    AtomicMarkableReference<Thread> owner;
    volatile ReentrantLock[][] locks;

    public RefinableCuckooHashSet(int capacity) {
        super(capacity);
        locks = new ReentrantLock[2][capacity];
        for (int i = 0; i < 2; i++) {
            for (int j = 0; j < capacity; j++) {
                locks[i][j] = new ReentrantLock();
            }
        }
        //locks init over
        owner = new AtomicMarkableReference<Thread>(null, false);
    }

    @Override
    public void acquire(T x) {
        boolean[] mark = {true};
        Thread me = Thread.currentThread(); Thread who;
        while (true) {
            do { // wait until not resizing
                who = owner.get(mark);
            } while (mark[0] && who != me);
            ReentrantLock[][] oldLocks = locks;
            ReentrantLock oldLock0 = oldLocks[0][hash0(x) % oldLocks[0].length];
            ReentrantLock oldLock1 = oldLocks[1][hash1(x) % oldLocks[1].length];
            oldLock0.lock();
            oldLock1.lock();
            who = owner.get(mark);
            if ((!mark[0] || who == me) && locks == oldLocks) {
                return; } else {
                oldLock0.unlock();
                oldLock1.unlock(); }
        }
    }

    @Override
    public void release(T x) {
        locks[0][hash0(x)].unlock();
        locks[1][hash1(x)].unlock();
    }

    @Override
    public void resize() {
        int oldCapacity = capacity;
        Thread me = Thread.currentThread();
        if (owner.compareAndSet(null, me, false, true)) {
            try {
                if (capacity != oldCapacity) { // someone else resized first
                    return; }
                quiesce();
                capacity = 2 * capacity;
                List<T>[][] oldTable = table;
                table = (List<T>[][]) new List[2][capacity];
                locks = new ReentrantLock[2][capacity];
                for (int i = 0; i < 2; i++) {
                    for (int j = 0; j < capacity; j++) {
                        locks[i][j] = new ReentrantLock();
                    }
                }
                for (List<T>[] row : table) {
                    for (int i = 0; i < row.length; i++) {
                        row[i] = new ArrayList<T>(PROBE_SIZE); }
                }
                for (List<T>[] row : oldTable) {
                    for (List<T> set : row) { for (T z : set) {
                        add(z); }
                    }
                }
            } finally {
                owner.set(null, false);
            }
        }
    }

    protected void quiesce() {
        for (ReentrantLock lock : locks[0]) {
            while (lock.isLocked()) {} }
    }
}
