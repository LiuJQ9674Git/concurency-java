package com.abc.art.counting.network.bitonic;

import com.abc.art.counting.combining.CombiningTree;

import java.util.concurrent.atomic.AtomicInteger;

public class BitonicTest {
   final Bitonic bitonic=new Bitonic(4);
    final AtomicInteger atomicInteger=new AtomicInteger(0);
    final AtomicInteger atomicadd=new AtomicInteger(0);
    final CombiningTree combiningTree=new CombiningTree(4);
    public static void main(String[] args){
        BitonicTest bitonicTest=new BitonicTest();
        bitonicTest.handleBitonic();
    }

    void handleBitonic(){
        for(int i=0;i<8;i++) {
            final int ii=i;
            Thread thread = new Thread(() -> {
                while (true) {
                    int pos = bitonic.traverse(ii)+1;

                    System.out.println(Thread.currentThread().getName()+
                            "\t ith:\t" + ii+"\tPost:\t" + pos
                            +"\t incrementAndGet:\t "+atomicInteger.incrementAndGet()
                            +"\t getAndAdd:\t "+atomicadd.getAndAdd(pos)
                            //+"\t combiningTree:\t"+combiningTree.getAndIncrement()
                    );

//                    try {
//                        Thread.sleep(100);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
                }
            });
            thread.start();
        }
    }
}
