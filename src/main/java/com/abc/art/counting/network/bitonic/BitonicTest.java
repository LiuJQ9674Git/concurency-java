package com.abc.art.counting.network.bitonic;

import java.util.concurrent.atomic.AtomicInteger;

public class BitonicTest {
   final Bitonic bitonic=new Bitonic(4);
    AtomicInteger atomicInteger=new AtomicInteger(0);
    public static void main(String[] args){
        BitonicTest bitonicTest=new BitonicTest();
        bitonicTest.handleBitonic();
    }

    void handleBitonic(){
        for(int i=0;i<16;i++) {
            final int ii=i;
            Thread thread = new Thread(() -> {
                while (true) {
                    int pos = bitonic.traverse(2);
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
