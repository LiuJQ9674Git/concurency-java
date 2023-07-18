package com.abc.art.mutalexcute;

import java.util.concurrent.atomic.AtomicInteger;

class Counter {
    private long value;
    private AtomicInteger atomicInteger=new AtomicInteger(0);
    Lock lock;
    public Counter(long c) { // constructor
        this(c,new LockOne());
    }

    public Counter(long c,Lock lock) { // constructor
        value = c;
        this.lock=lock;
    }
    // increment and return prior value
    public long getAndIncrement() {
        lock.lock();
        long temp = value;
        value = temp + 1;
        lock.unlock();
        return temp;
    }

    public static void doLockOne(){
        Counter counter=new Counter(0);
        for(int i=0;i<2;i++) {
            Thread thread=new Thread(()->{
            long c = counter.getAndIncrement();
            System.out.println(c);
            }
            );
            thread.start();
        }

    }

    public static void doLockInt(){
        Counter counter=new Counter(0,new Peterson());
        for(int i=0;i<2;i++) {
            Thread thread=new Thread(()->{
                long c = counter.getAndIncrement();
                System.out.println(c);
            }
            );
            thread.start();
        }
    }

    public static void doTTASLock(){
        Counter counter=new Counter(0,new TTASLock());
        for(int i=0;i<2;i++) {
            Thread thread=new Thread(()->{
                long c = counter.getAndIncrement();
                System.out.println(c);
            }
            );
            thread.start();
        }
    }
    public static void doAtomicInteger(){
        Counter counter=new Counter(0);
        int b=counter.atomicInteger.compareAndExchange(0,12);
        boolean bool=counter.atomicInteger.compareAndSet(10,12);
        int getIntSet=counter.atomicInteger.getAndSet(10);
        int getInt=counter.atomicInteger.get();
        int getAndIncrement=counter.atomicInteger.getAndIncrement();
    }
    public static void main(String[] args){
        //doLockInt();
        //doTTASLock();
        doAtomicInteger();
    }
}

