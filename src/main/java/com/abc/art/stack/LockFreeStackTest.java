package com.abc.art.stack;

import java.util.concurrent.atomic.AtomicInteger;

public class LockFreeStackTest {
    //LockFreeStack<Integer> lockFreeStack=new LockFreeStack();
    static int NUM_THREAD=8;

    EliminationBackoffStack<Integer> lockFreeStack=new EliminationBackoffStack<Integer>();
    AtomicInteger atomicInteger=new AtomicInteger(0);
    public static void main(String[] args){
        LockFreeStackTest lockFreeStackTest=new LockFreeStackTest();
//        try {
//            lockFreeStackTest.doLockFreeStack();
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
        //lockFreeStackTest.doStack();

        lockFreeStackTest.doElimination();

    }
    public void doLockFreeStack() throws InterruptedException {
        productInteger();
        //Thread.sleep(10);
        consumeInteger();
    }

    public void doStack(){
        Thread product = new Thread(() -> {
            for(int i=0;i<NUM_THREAD;i++) {
                int pos = atomicInteger.getAndIncrement();
                lockFreeStack.push(pos);
                System.out.println(Thread.currentThread().getName() + "\tproductInteger:\t" + pos);
            }
        });
        product.start();
        try {
            product.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        Thread consumer = new Thread(() -> {
            for(int i=0;i<NUM_THREAD;i++) {
                Integer pos = lockFreeStack.pop();
                System.out.println(Thread.currentThread().getName() + "\tconsumeInteger:\t" + pos);
            }
        });
        consumer.start();
    }
    public void productInteger(){
        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
                    int pos = atomicInteger.getAndIncrement();
                    lockFreeStack.push(pos);
                    System.out.println(Thread.currentThread().getName() + "\tproductInteger:\t" + pos);
                }
            });
            thread.start();
        }
    }

    public void consumeInteger(){
        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
                    Integer pos = lockFreeStack.pop();
                    System.out.println(Thread.currentThread().getName() + "\tconsumeInteger:\t" + pos);
                }
            });
            thread.start();
        }
    }

    public void doElimination()  {
        productElimination();
        //Thread.sleep(10);
        consumeElimination();
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

    public void productElimination(){
        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
//                    try {
//                        Thread.sleep(10);
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
//                        Thread.sleep(1);
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
