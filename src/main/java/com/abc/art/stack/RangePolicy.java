package com.abc.art.stack;

import com.abc.art.mutalexcute.Backoff;

import java.util.concurrent.atomic.AtomicInteger;

public class RangePolicy {
    Backoff backoff = new Backoff(LockFreeStack.MIN_DELAY,
            LockFreeStack.MAX_DELAY);

    AtomicInteger atomicInteger=new AtomicInteger(0);
    AtomicInteger pushInteger=new AtomicInteger(0);
    AtomicInteger popInteger=new AtomicInteger(0);
    public int getRange() {
        return Config.range;
    }

    public void recordEliminationSuccessPush(EliminationBackoffStack stack) {
        //先加一
        atomicInteger.getAndIncrement();
        pushInteger.incrementAndGet();
        //调整策略
        //System.err.println(Thread.currentThread().getName()+"\tstep: push");
    }

    public void recordEliminationSuccessPop(EliminationBackoffStack stack) {
        //减一
        atomicInteger.getAndDecrement();
        popInteger.incrementAndGet();
        //调整策略
        //System.err.println(Thread.currentThread().getName()+"\tstep:pop");
    }

    public void recordEliminationFailPush(EliminationBackoffStack stack) {
        //调整策略
        //System.err.println(Thread.currentThread().getName()+"\tstep: fail push");
    }

    public void recordEliminationFailPop(EliminationBackoffStack stack) {
        //调整策略
        //System.err.println(Thread.currentThread().getName()+"\tstep:fail pop");
    }

    public boolean empty() {
        //return top.get() == 0;
        return pushInteger.get()==0 || popInteger.get()==pushInteger.get();
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
