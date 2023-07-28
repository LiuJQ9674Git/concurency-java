package com.abc.art.stack;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class EliminationArray <T>{
    private static final int duration = Config.duration;
    final LockFreeExchanger<T>[] exchanger;
    public EliminationArray(int capacity) {
        exchanger = (LockFreeExchanger<T>[]) new LockFreeExchanger[capacity];
        for (int i = 0; i < capacity; i++) {
            exchanger[i] = new LockFreeExchanger<T>();
        }
    }
    public T visit(T value, int range) throws TimeoutException {
        int slot = ThreadLocalRandom.current().nextInt(range);
        return (exchanger[slot].exchange(value, duration, TimeUnit.MILLISECONDS));
    }
}
