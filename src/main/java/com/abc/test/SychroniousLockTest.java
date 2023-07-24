package com.abc.test;

import javax.xml.crypto.Data;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class SychroniousLockTest {
     SynchronousQueue queue=new SynchronousQueue();


     AtomicInteger atomicInteger=new AtomicInteger(0);

    public static void main(String[] args){
        new SychroniousLockTest().handleSynchronousQueue();
        //new SychroniousLockTest().handleSynchronousQueueOfferPoll();
    }
    public  void  handleSynchronousQueue(){
        productSynchronousQueue();
        comsuerSynchronousQueue();
    }
      void productSynchronousQueue(){
         for(int i=0;i<2;i++) {
             Thread thread = new Thread(() -> {
                 while (true) {

                         int o = atomicInteger.getAndAdd(1);
                         if (o > 10) {
                             break;
                         }
                         try {
                             queue.put(o);
                         } catch (InterruptedException e) {
                             throw new RuntimeException(e);
                         }
                         System.out.println(Thread.currentThread().getName()
                                 + "\tDate:\t" + System.currentTimeMillis() + "\t" + "产：\t" + o);

                     int k= ThreadLocalRandom.current().nextInt(1000);
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
                            System.out.println(Thread.currentThread().getName()
                                    + "\tDate:\t" + System.currentTimeMillis() + "\t消：\t" + o);

                        int k= ThreadLocalRandom.current().nextInt(1500);
                        Thread.sleep(k);
                    }
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            thread.start();
        }
    }

    public  void  handleSynchronousQueueOfferPoll(){
        productSynchronousQueueOffer();
        comsuerSynchronousQueuePoll();
    }
    void productSynchronousQueueOffer(){
        for(int i=0;i<2;i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    int o=atomicInteger.getAndAdd(1);
                    if(o>10){
                        break;
                    }
                    queue.offer(o);
                    System.out.println(Thread.currentThread().getName() + "\t" + "产：\t"+o);
                    int k= ThreadLocalRandom.current().nextInt(600);
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

    void comsuerSynchronousQueuePoll(){
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
