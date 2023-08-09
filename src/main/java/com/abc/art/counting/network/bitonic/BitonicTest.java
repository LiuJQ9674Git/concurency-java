package com.abc.art.counting.network.bitonic;

import java.util.concurrent.atomic.AtomicInteger;

public class BitonicTest {
    //static int WITH=4;
    static int WITH=8;
    static int FIRST=0;
    static int SECOND=WITH/2+1;

    static int STEP_0=0;
    static int STEP_1=1;

    static int STEP_2=2;

    static int STEP_3=3;

    static int STEP_4=4;

    static int STEP_5=5;

    static int STEP_6=6;

    static int STEP_7=7;
   final Bitonic bitonic=new Bitonic(WITH);
   AtomicInteger addTh=new AtomicInteger(1);
    public static void main(String[] args){
        BitonicTest bitonicTest=new BitonicTest();
        //bitonicTest.handleBitonic();
        bitonicTest.handleBitonicStep();
    }

    void handleBitonicStep(){
        handleToken_Step_0();
        handleToken_Step_1();
        handleToken_Step_2();
        handleToken_Step_3();
        handleToken_Step_4();
        handleToken_Step_5();
        handleToken_Step_6();
        handleToken_Step_7();
    }

    void handleToken_Step_0(){
        Thread thread = new Thread(() -> {
            while (true) {
                int pos = bitonic.traverse(STEP_0);
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
        },"STEP_0");
        thread.start();
    }

    void handleToken_Step_1(){
        Thread thread = new Thread(() -> {
            while (true) {
                int pos = bitonic.traverse(STEP_1);
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
        },"STEP_1");
        thread.start();
    }

    void handleToken_Step_2(){
        Thread thread = new Thread(() -> {
            while (true) {
                int pos = bitonic.traverse(STEP_2);
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
        },"STEP_2");
        thread.start();
    }

    void handleToken_Step_3(){
        Thread thread = new Thread(() -> {
            while (true) {
                int pos = bitonic.traverse(STEP_3);
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
        },"STEP_3");
        thread.start();
    }

    void handleToken_Step_4(){
        Thread thread = new Thread(() -> {
            while (true) {
                int pos = bitonic.traverse(STEP_4);
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
        },"STEP_4");
        thread.start();
    }

    void handleToken_Step_5(){
        Thread thread = new Thread(() -> {
            while (true) {
                int pos = bitonic.traverse(STEP_5);
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
        },"STEP_5");
        thread.start();
    }

    void handleToken_Step_6(){
        Thread thread = new Thread(() -> {
            while (true) {
                int pos = bitonic.traverse(STEP_6);
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
        },"STEP_6");
        thread.start();
    }

    void handleToken_Step_7(){
        Thread thread = new Thread(() -> {
            while (true) {
                int pos = bitonic.traverse(STEP_7);
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
        },"STEP_7");
        thread.start();
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
