package com.abc.test;

import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Exchanger;
import java.util.concurrent.atomic.AtomicInteger;

public class ExchangerTest {

    static void start() {


        new Thread(new FillAndEmpty.EmptyingLoop()).start();
        new Thread(new FillAndEmpty.FillingLoop()).start();
    }

    public static  void main(String[] args){
        start();
    }

}

 class DataBuffer{
     volatile int pos;
     DataBuffer(int i){
         this.pos=i;
     }
    boolean isFull(){
        return pos!=0;
    }

     boolean isEmpty(){
         return pos==0;
     }
}
 class FillAndEmpty {
     static final CountDownLatch latch=new CountDownLatch(1);
     static Exchanger<DataBuffer> exchanger = new Exchanger<>();
     static DataBuffer initialEmptyBuffer = new DataBuffer(0);
    // a made-up type
    static DataBuffer initialFullBuffer = new DataBuffer(0);
     static AtomicInteger atomicInteger=new AtomicInteger();
     static AtomicInteger data=new AtomicInteger();
    static class FillingLoop implements Runnable {
        //FillingLoop()
        public void run() {
            latch.countDown();
            System.out.println("开始生产");
            DataBuffer currentBuffer = initialEmptyBuffer;
            try {
                while (currentBuffer != null && atomicInteger.getAndIncrement()<10) {
                    //Thread.interrupted();
                    addToBuffer(currentBuffer);
                    Thread.sleep(100);
                    if (currentBuffer.isFull())
                        currentBuffer = exchanger.exchange(currentBuffer);
                }        } catch (InterruptedException ex) {

            }
        }
    }

     static class EmptyingLoop implements Runnable {
         public void run() {
             DataBuffer currentBuffer = initialFullBuffer;
             try {
                 latch.await();
                 System.out.println("开始消费");
             } catch (InterruptedException e) {
                 throw new RuntimeException(e);
             }
             try {
                 while (currentBuffer != null && atomicInteger.get()<10) {
                     //Thread.interrupted();
                     takeFromBuffer(currentBuffer);
                     Thread.sleep(100);
                     if (currentBuffer.isEmpty())
                         currentBuffer = exchanger.exchange(currentBuffer);
                 }
             } catch (InterruptedException ex) { //... handle ...
             }
         }
     }

     static void takeFromBuffer(DataBuffer currentBuffer){

         System.out.println("-----------消费------------：\t"+currentBuffer.pos);
         currentBuffer.pos=0;
     }

     static Random r = new Random();
     static void addToBuffer(DataBuffer currentBuffer){
         int i=data.getAndIncrement();
         System.out.println("+++++++++生产+++++++++++++：\t"+i);
         currentBuffer.pos = i;

    }
}


