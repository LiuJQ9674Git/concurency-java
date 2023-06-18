package com.abc.test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

public class AtomicReferenceTest {
    //static
    static final CountDownLatch latch=new CountDownLatch(1);
    public static void main(String[] args) {
        final AtomicInteger atomic = new AtomicInteger(0);
        consumer(atomic);
        producer(atomic);

    }

    static void producer(AtomicInteger atomic) {

        new Thread(() -> {

            latch.countDown();
            System.out.println("生产数据开始: " );
            for (int i = 0; i < 10; i++) {
                //atomic.set(i);
                //atomic.lazySet(i);
                synchronized (atomic) {
                    atomic.set(i);
                }
                //atomic.lazySet(i);
                //atomic.setRelease(i);

                System.out.println("生产数据："+i+"\tSet: " + i);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    static void consumer(AtomicInteger atomic) {
        new Thread(() -> {
            try {
                latch.await();
                System.out.println("消费数据等待生产: " );
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            int counter = 0;
            for (int i = 0; i < 10; i++) {
                //int counter = atomic.get();
                //int counter = atomic.getAcquire();

                // counter = atomic.getAcquire();
                synchronized (atomic) {
                    counter = atomic.get();
                }

                System.out.println("消费顺序："+i+"\tGet: " + counter);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }
}
