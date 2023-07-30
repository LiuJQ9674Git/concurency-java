package com.abc.art.counting.network.periodic;

import java.util.concurrent.atomic.AtomicInteger;

public class PeriodicTest {
    static int mySize=16;
    static AtomicInteger atomicInteger = new AtomicInteger();;
    public static void main(String[] args){
        Periodic periodic=new Periodic(mySize);
        for(int i=0;i<16;i++) {
            final int ii=i;
            Thread thread = new Thread(() -> {
                while (true) {
                    int pos = periodic.traverse(0);
                    System.out.println(Thread.currentThread().getName()+
                            "\t ith:\t" + ii+"\tPost:\t" + pos
                            +"\t atomicInteger\t "+atomicInteger.incrementAndGet() );

                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            });
            thread.start();
        }
    }
}
