package com.abc.art.stack;

import java.util.concurrent.atomic.AtomicInteger;

public class LockFreeStackTest {
    static int NUM_THREAD=8;

    //LockFreeStack<Integer> lockFreeStack=new LockFreeStack<Integer>();
    LockFreeStack<Integer> lockFreeStack=new EliminationBackoffStack<Integer>();
    AtomicInteger atomicInteger=new AtomicInteger(0);
    public static void main(String[] args) throws InterruptedException {
        LockFreeStackTest lockFreeStackTest=new LockFreeStackTest();
        //lockFreeStackTest.doStack();

        lockFreeStackTest.doLockFreeStack();

    }
    public void doLockFreeStack() throws InterruptedException {
        productInteger();
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
     void productInteger(){
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

     void consumeInteger(){
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
}
