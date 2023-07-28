package com.abc.art.stack;

import com.abc.art.mutalexcute.Backoff;

public class RangePolicy {
    Backoff backoff = new Backoff(LockFreeStack.MIN_DELAY,
            LockFreeStack.MAX_DELAY);

    public int getRange() {
        return Config.range;
    }

    public void recordEliminationSuccess(String step) {
        //调整策略
        System.err.println(Thread.currentThread().getName()+"\tstep:\t"+step);
    }

    public void recordEliminationTimeout() {
//        try {
//            backoff.backoff();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        throw new RuntimeException();

    }
}
