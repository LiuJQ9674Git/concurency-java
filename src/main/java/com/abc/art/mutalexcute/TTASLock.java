package com.abc.art.mutalexcute;

import java.util.concurrent.atomic.AtomicBoolean;

public class TTASLock implements Lock{
    AtomicBoolean state = new AtomicBoolean(false);
    @Override
    public void lock() {
        while (true){
            System.out.println("1 state. start \t"+state+"\tThread\t"+Thread.currentThread().getName());
            while (state.get()) {
                System.out.println("2 state.get()\t"+state+"\tThread\t"+Thread.currentThread().getName());
            };
            System.out.println("3 state. middle \t"+state+"\tThread\t"+Thread.currentThread().getName());
            if (!state.getAndSet(true)) {
                System.out.println("4 state.getAndSet()\t"+state+"\tThread\t"+Thread.currentThread().getName());
                return;
            }else{
                System.out.println("5 state.getAndSet() else \t"+state+"\tThread\t"+Thread.currentThread().getName());
            }
            System.out.println("6 state. final \t"+state+"\tThread\t"+Thread.currentThread().getName());
        }//
    }

    @Override
    public void unlock() {
        state.set(false);
        System.out.println("state.set()+Thread:\t"+Thread.currentThread().getName());
    }
}
