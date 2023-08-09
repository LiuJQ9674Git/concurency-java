package com.abc.art.stack;

import com.abc.art.mutalexcute.Backoff;

import java.util.concurrent.atomic.AtomicInteger;

public class RangePolicy {
    Backoff backoff = new Backoff(LockFreeStack.MIN_DELAY,
            LockFreeStack.MAX_DELAY);

    static AtomicInteger atomicInteger=new AtomicInteger(0);
    static AtomicInteger pushInteger=new AtomicInteger(0);
    static AtomicInteger popInteger=new AtomicInteger(0);

    public int getRange() {
        return Config.range;
    }

    public void recordEliminationSuccessPush(EliminationBackoffStack stack) {
        //pop取出调用
        //调整策略
    }

    public void recordEliminationWaiting(EliminationBackoffStack stack) {
        //调整策略
//        boolean isFull=isFull();
//        while (isFull){
//            try {
//                backoff.backoff();
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            //等待，通知机制
//        }
//        atomicInteger.getAndIncrement();
//        pushInteger.getAndIncrement();
    }


    public void recordEliminationSuccessPop(EliminationBackoffStack stack) {
    }

    public void recordEliminationPoping(EliminationBackoffStack stack) {
        //调整策略
        boolean isEmpty=atomicInteger.get()==0;
        atomicInteger.getAndDecrement();
        popInteger.getAndIncrement();

    }

    public void recordEliminationPopNull(EliminationBackoffStack stack) {
        //调整策略
    }

    public void recordEliminationTimeoutPush(EliminationBackoffStack stack) {
        throw new RuntimeException();

    }

    public void recordEliminationTimeoutPop(EliminationBackoffStack stack) {
        //System.err.println(Thread.currentThread().getName()+"\tstep: fail pop Timeout");
        throw new RuntimeException();
    }

    public boolean empty() {
        return atomicInteger.get()==0 || popInteger.get()==pushInteger.get();
    }

    boolean isFull(){
        return pushInteger.get()-2 - Config.capacity ==0
//                || popInteger.get()==pushInteger.get()
                ;
    }
}
