package com.abc.art.stack;

import java.util.concurrent.atomic.AtomicInteger;

public class EliminationBackoffStackTest {
    EliminationBackoffStack<Integer> lockFreeStack=new EliminationBackoffStack<Integer>();
    AtomicInteger atomicInteger=new AtomicInteger(0);
    static int NUM_THREAD=8;
    public static void main(String[] args){
        EliminationBackoffStackTest eliminationBackoffStackTest=new EliminationBackoffStackTest();
        eliminationBackoffStackTest.doElimination();
    }

    public void doEliminationSigle()  {
        Thread product = new Thread(() -> {
            int pos = atomicInteger.getAndIncrement();
            lockFreeStack.eliminatedPush(pos);
            System.out.println(Thread.currentThread().getName() + "\tproductInteger:\t" + pos);
        });
        product.start();

        Thread consume = new Thread(() -> {
            Integer posEliminated = lockFreeStack.eliminatedPop();
            System.out.println(Thread.currentThread().getName() + "\tconsumeInteger:\t" + posEliminated);
        });
        consume.start();
    }


    public void doElimination()  {
        productElimination();
        consumeElimination();
    }

    public void productElimination(){
        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
//                    try {
//                        Thread.sleep(20);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
                    int pos = atomicInteger.getAndIncrement();
                    lockFreeStack.eliminatedPush(pos);
                    System.out.println(Thread.currentThread().getName() + "\tproductInteger:\t" + pos);
                }
            });
            thread.start();
        }
    }
    public void consumeElimination(){
        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
//                    try {
//                        Thread.sleep(20);
//                    } catch (InterruptedException e) {
//                        throw new RuntimeException(e);
//                    }
                    Integer pos = lockFreeStack.eliminatedPop();
                    System.out.println(Thread.currentThread().getName() + "\tconsumeInteger:\t" + pos);
                }
            });
            thread.start();
        }
    }
}
