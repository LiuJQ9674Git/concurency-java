package com.abc.art.counting.network.periodic;

import java.util.concurrent.atomic.AtomicInteger;

public class PeriodicTest {
    static int WITH=8;
    static int FIRST=0;
    static int SECOND=WITH/2+1;
    Periodic periodic=new Periodic(WITH);
    static AtomicInteger atomicInteger = new AtomicInteger();;
    public static void main(String[] args){
        PeriodicTest periodicTest=new PeriodicTest();
        periodicTest.doPeriodic();
    }

    void doPeriodic(){
        handleToken_1();
        handleToken_2();
    }

    void handleToken_1(){
        Thread thread = new Thread(() -> {
            while (true) {
                int pos = periodic.traverse(FIRST);
                System.out.println(Thread.currentThread().getName()+"\tPost:\t" + pos);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }

    void handleToken_2(){
        Thread thread = new Thread(() -> {
            while (true) {
                int pos = periodic.traverse(SECOND);
                System.out.println(Thread.currentThread().getName()+"\tPost:\t" + pos);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        thread.start();
    }
}
