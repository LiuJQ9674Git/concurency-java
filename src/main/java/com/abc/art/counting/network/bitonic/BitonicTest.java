package com.abc.art.counting.network.bitonic;

import java.util.concurrent.atomic.AtomicInteger;

public class BitonicTest {
    static int WITH=4;
    static int FIRST=0;
    static int SECOND=WITH/2+1;

   final Bitonic bitonic=new Bitonic(WITH);
   AtomicInteger addTh=new AtomicInteger(1);
    public static void main(String[] args){
        BitonicTest bitonicTest=new BitonicTest();
        bitonicTest.handleBitonic();
    }


    void handleBitonic(){
        handleToken_1();
        handleToken_2();
        //handleToken_3();
        //handleToken_4();
    }

    void handleToken_1(){
        Thread thread = new Thread(() -> {
            while (true) {
                int pos = bitonic.traverse(FIRST);
                if(pos==0){
                    continue;
                }
                System.out.println(Thread.currentThread().getName()
                        +"\tPost:\t" + pos+"\taddTh:\t"+addTh.getAndAdd(pos)
                        );

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        },"handleToken_1");
        thread.start();
    }

    void handleToken_2(){
            Thread thread = new Thread(() -> {
                while (true) {
                    int pos = bitonic.traverse(SECOND);
                    if(pos==0){
                        continue;
                    }
                    System.out.println(Thread.currentThread().getName()
                            +"\tPost:\t" + pos+"\taddTh:\t"+addTh.getAndAdd(pos)
                    );
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            },"handleToken_2");
            thread.start();
    }
    void handleToken_3(){
        Thread thread = new Thread(() -> {
            while (true) {
                int pos = bitonic.traverse(1);

                System.out.println(Thread.currentThread().getName()+"\tPost:\t" + pos);
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        },"handleToken_3");
        thread.start();
    }

    void handleToken_4(){
        Thread thread = new Thread(() -> {
            while (true) {
                int pos = bitonic.traverse(2);
                System.out.println(Thread.currentThread().getName()+"\tPost:\t" + pos);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        },"handleToken_4");
        thread.start();
    }
}
