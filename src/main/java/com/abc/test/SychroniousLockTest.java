package com.abc.test;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class SychroniousLockTest {
     SynchronousQueue queue=new SynchronousQueue();
     AtomicInteger atomicInteger=new AtomicInteger(0);

    public static void main(String[] args){
        new SychroniousLockTest().handleSynchronousQueue();
    }
    public  void  handleSynchronousQueue(){
        productSynchronousQueue();
        comsuerSynchronousQueue();
    }
      void productSynchronousQueue(){
         for(int i=0;i<2;i++) {
             Thread thread = new Thread(() -> {
                 while (true) {
                     int o=atomicInteger.getAndAdd(1);
                     if(o>10){
                         break;
                     }
                     try {
                         queue.put(o);
                     } catch (InterruptedException e) {
                         throw new RuntimeException(e);
                     }
                     System.out.println(Thread.currentThread().getName() + "\t" + "产：\t"+o);
                     int k= ThreadLocalRandom.current().nextInt(100);
                     try {
                         Thread.sleep(k);
                     } catch (InterruptedException e) {
                         throw new RuntimeException(e);
                     }
                 }
             });
             thread.start();
         }
    }

     void comsuerSynchronousQueue(){
        for(int i=0;i<2;i++) {
            Thread thread = new Thread(() -> {
                try {
                    for (; ; ) {
                        Object o = queue.take();
                        System.out.println(Thread.currentThread().getName() + "\t消：\t" + o);
                        int k= ThreadLocalRandom.current().nextInt(500);
                        Thread.sleep(k);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();
        }
    }
}
