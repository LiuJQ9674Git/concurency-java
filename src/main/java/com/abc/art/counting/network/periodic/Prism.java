package com.abc.art.counting.network.periodic;

import com.abc.art.mutalexcute.ThreadId;
import com.abc.art.stack.EliminationArray;
import com.abc.art.stack.LockFreeExchanger;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Prism {
    private static final int duration = 100;
    //Exchanger<Integer>[] exchanger;
    final LockFreeExchanger<Integer>[]  exchanger;
    public Prism(int capacity) {
        exchanger = (LockFreeExchanger<Integer>[]) new LockFreeExchanger[capacity];
        for (int i = 0; i < capacity; i++) {
            exchanger[i] = new LockFreeExchanger<Integer>();
        }
    }

    public boolean visit() throws TimeoutException,InterruptedException {
        int me = ThreadId.get();
        int slot = ThreadLocalRandom.current().nextInt(exchanger.length);
        int other = exchanger[slot].exchange(me,duration, TimeUnit.MILLISECONDS);
        return (me < other);
    }
}
