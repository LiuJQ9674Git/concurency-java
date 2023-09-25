package com.abc.queue;

import com.abc.test.SychroniousLockTest;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

public class SynchronousQueueTest {
    SynchronousQueue queue = new SynchronousQueue();
    AtomicInteger atomicInteger = new AtomicInteger(0);

    public static void main(String[] args) {
        //new SynchronousQueueTest().handleSynchronousQueue();
        new SynchronousQueueTest().createSynchronousQueue();
    }

    public void createSynchronousQueue() {
        createComsuer();
        createProduct();

    }

    public void handleSynchronousQueue() {
        productSynchronousQueue();
        comsuerSynchronousQueue();
    }

    void createProduct() {
        Thread thread = new Thread(() -> {
            for (; ; ) {
                try {

                    int o = atomicInteger.getAndAdd(1);
                    queue.put(o);
                    System.out.println(Thread.currentThread().getName()
                            + "\tDate:\t" + System.currentTimeMillis() + "\t" + "产：\t" + o);
                    int k = ThreadLocalRandom.current().nextInt(100);
                    try {
                        Thread.sleep(k);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                    throw new RuntimeException(e);
                }
            }
        }, "生产线程:\t");

        thread.start();

    }

    void createComsuer() {
        Thread thread = new Thread(() -> {
            try {
                for (; ; ) {
                    Object o = queue.take();
                    System.out.println(Thread.currentThread().getName()
                            + "\tDate:\t" + System.currentTimeMillis() + "\t消：\t" + o);
                }
            } catch (InterruptedException e) {
                System.err.println(e.getMessage());
                throw new RuntimeException(e);
            }
        }, "消费线程:\t");
        thread.start();

    }

    void productSynchronousQueue() {
        for (int i = 0; i < 2; i++) {
            Thread thread = new Thread(() -> {
                while (true) {
                    try {
                        int o = atomicInteger.getAndAdd(1);
                        if (o > 10) {
                            break;
                        }
                        queue.put(o);
                        System.out.println(Thread.currentThread().getName()
                                + "\tDate:\t" + System.currentTimeMillis() + "\t" + "产：\t" + o);
                        int k = ThreadLocalRandom.current().nextInt(100);
                        try {
                            Thread.sleep(k);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    } catch (InterruptedException e) {
                        System.err.println(e.getMessage());
                        throw new RuntimeException(e);
                    }
                }
            }, "生产线程:\t" + i);
            thread.start();
        }
    }

    void comsuerSynchronousQueue() {
        for (int i = 0; i < 10; i++) {
            Thread thread = new Thread(() -> {
                try {
                    for (; ; ) {

                        Object o = queue.take();
                        System.out.println(Thread.currentThread().getName()
                                + "\tDate:\t" + System.currentTimeMillis() + "\t消：\t" + o);
                    }
                } catch (InterruptedException e) {
                    System.err.println(e.getMessage());
                    throw new RuntimeException(e);
                }
            }, "消费线程:\t" + i);
            thread.start();
        }
    }

}
