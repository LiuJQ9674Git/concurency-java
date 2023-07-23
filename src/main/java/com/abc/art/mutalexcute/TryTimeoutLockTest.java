package com.abc.art.mutalexcute;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

public class TryTimeoutLockTest {
    private volatile long value;
    final Lock lock;

    TryTimeoutLockTest(Lock lock){
        this.lock=lock;
    }
    public static void dogeTOLockTest(){
        TryTimeoutLockTest counter=new TryTimeoutLockTest(new TOLock());
        //int k=0;
        for(int i=0;i<10;i++) {
            Thread thread=new Thread(()->{
                long c = counter.getAndIncrement();
                try {
                    int k= ThreadLocalRandom.current().nextInt(10000);
                    Thread.sleep(k);
                    System.out.println("Thread\t"+Thread.currentThread().getName()+
                            "\tsleep:\t"+k+"\tvalue:\t"+c);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
            );
            thread.start();

        }

    }

    public static void doCompositeLock(){
        TryTimeoutLockTest counter=new TryTimeoutLockTest(new CompositeLock());
        //int k=0;
        for(int i=0;i<30;i++) {
            Thread thread=new Thread(()->{
                long c = counter.getAndIncrement();
                try {
                    int k= ThreadLocalRandom.current().nextInt(100);
                    Thread.sleep(k);
                    System.out.println("Thread\t"+Thread.currentThread().getName()+
                            "\tsleep:\t"+k+"\tvalue:\t"+c);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
            );
            thread.start();

        }
    }

    public long getAndIncrement() {
        long temp=0;
        try {
            if(lock.tryLock(1000, TimeUnit.MILLISECONDS)) {
                temp = value;
                value = temp + 1;
            }else{
                //System.out.println("Thread\t"+Thread.currentThread().getName()+"\tgetAndIncrement else");
            }
        } catch (InterruptedException e) {
            System.out.println("Thread\t"+Thread.currentThread().getName()+e.getMessage());
            throw new RuntimeException(e);
        }

        lock.unlock();
        return temp;
    }

    public static void doCompositeFastPathLock(){
        TryTimeoutLockTest counter=new TryTimeoutLockTest(new CompositeFastPathLock());
        //int k=0;
        for(int i=0;i<30;i++) {
            Thread thread=new Thread(()->{
                long c = counter.getAndIncrement();
                try {
                    int k= ThreadLocalRandom.current().nextInt(100);
                    Thread.sleep(k);
                    System.out.println("Thread\t"+Thread.currentThread().getName()+
                            "\tsleep:\t"+k+"\tvalue:\t"+c);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
            );
            thread.start();

        }
    }


    public static void main(String[] args){
        //doLockInt();
        //doTTASLock();
        dogeTOLockTest();
        //doCompositeLock();
        //doCompositeFastPathLock();
    }
}
