package com.abc.art.counting.network.bitonic;

import java.util.concurrent.atomic.AtomicInteger;

public class BitonicTest {
   final Bitonic bitonic=new Bitonic(64);
    AtomicInteger atomicInteger=new AtomicInteger(0);
    public static void main(String[] args){
        BitonicTest bitonicTest=new BitonicTest();
        bitonicTest.handleBitonic();
    }

    void handleBitonic(){
        for(int i=1;i<32;i++) {
            final int ii=i;
            Thread thread = new Thread(() -> {
                int pos=bitonic.traverse(ii);
                System.out.println("Post:\t"+pos);
            });
            thread.start();
        }
    }
}
