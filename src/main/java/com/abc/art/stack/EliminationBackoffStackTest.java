package com.abc.art.stack;

import java.util.concurrent.atomic.AtomicInteger;

public class EliminationBackoffStackTest {
    EliminationBackoffStack<Integer> lockFreeStack=new EliminationBackoffStack<Integer>();
    AtomicInteger atomicInteger=new AtomicInteger(0);
    static int NUM_THREAD=8;
    public static void main(String[] args){
        EliminationBackoffStackTest eliminationBackoffStackTest=new EliminationBackoffStackTest();
        eliminationBackoffStackTest.doElimination();
        //eliminationBackoffStackTest.doEliminationPush();
        //eliminationBackoffStackTest.doEliminationPop();
    }

    // ===========================================================================
    public void doEliminationPop()  {
        productEliminationPop();
    }

    public void productEliminationPop(){
        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
                    Integer pos = lockFreeStack.eliminatedPop();
                    System.out.println(Thread.currentThread().getName()
                            + "\tconsumeInteger:\t" + pos);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            });
            thread.start();
        }
    }
    public void doEliminationPush()  {
        productEliminationPush();
    }

    public void productEliminationPush(){
        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
                    int pos = atomicInteger.getAndIncrement();
                    lockFreeStack.eliminatedPush(pos);
                    System.out.println(Thread.currentThread().getName()
                            + "\tproduct:\t" + pos);
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        System.out.println(e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            });
            thread.start();
        }
    }
    // =================================================================
    public void doElimination()  {
        productElimination();
        consumeElimination();
    }

    public void productElimination(){
        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
                    int pos = atomicInteger.getAndIncrement();
                    lockFreeStack.eliminatedPush(pos);
                    System.out.println(Thread.currentThread().getName()
                            + "\tproductInteger:\t" + pos);
                }
            });
            thread.start();
        }
    }


    public void consumeElimination(){
        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
                    Integer pos = lockFreeStack.eliminatedPop();
                    //if(pos!=null)
                    System.out.println(Thread.currentThread().getName()
                            + "\tconsumeInteger:\t" + pos);
                }
            });
            thread.start();
        }
    }

    //===========================================================================
    public void doEliminationSigle()  {
        Thread product = new Thread(() -> {
            int pos = atomicInteger.getAndIncrement();
            lockFreeStack.eliminatedPush(pos);
            System.out.println(Thread.currentThread().getName()
                    + "\tproductInteger:\t" + pos);
        });
        product.start();

        Thread consume = new Thread(() -> {
            Integer posEliminated = lockFreeStack.eliminatedPop();
            System.out.println(Thread.currentThread().getName() +
                    "\tconsumeInteger:\t" + posEliminated);
        });
        consume.start();
    }

}
