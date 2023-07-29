package com.abc.art.queue;

import java.util.concurrent.atomic.AtomicInteger;

public class QueueTest {
    static int NUM_THREAD=8;
    AtomicInteger atomicInteger=new AtomicInteger(0);

    public static void main(String[] args){
        QueueTest queueTest=new QueueTest();
        //queueTest.handleBoundedQueue();
        //queueTest.handleLockFreeQueue();
        //queueTest.handleSynchronousQueue();
        queueTest.handleSynchronousDualQueue();
    }

    void handleSynchronousDualQueue() {
        final SynchronousDualQueue queue=new SynchronousDualQueue();
        productSynchronousDualQueue(queue);
        consumeSynchronousDualQueue(queue);
    }
    void productSynchronousDualQueue(SynchronousDualQueue queue){

        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
                    int pos = atomicInteger.getAndIncrement();
                    queue.enq(pos);
                    System.out.println(Thread.currentThread().getName()
                            + "\tproduct:\t" + pos);
                    currentSleep();
                }
            });
            thread.start();
        }
    }
    void consumeSynchronousDualQueue(SynchronousDualQueue queue){
        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
                    Integer pos = (Integer)queue.deq();
                    if(pos!=null)
                    System.out.println(Thread.currentThread().getName()
                            + "\tconsume:\t" + pos);
                    currentSleep();
                }
            });
            thread.start();
        }
    }

    // ==================================================================

    void handleSynchronousQueue() {
        final SynchronousQueue queue=new SynchronousQueue();
        productSynchronousQueue(queue);
        consumeSynchronousQueue(queue);
    }
    void productSynchronousQueue(SynchronousQueue queue){

        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
                    int pos = atomicInteger.getAndIncrement();
                    queue.enq(pos);
                    System.out.println(Thread.currentThread().getName()
                            + "\tproduct:\t" + pos);
                    currentSleep();
                }
            });
            thread.start();
        }
    }
    void consumeSynchronousQueue(SynchronousQueue queue){
        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
                    Integer pos = (Integer)queue.deq();
                    System.out.println(Thread.currentThread().getName()
                            + "\tconsume:\t" + pos);
                    currentSleep();
                }
            });
            thread.start();
        }
    }

    // ==================================================================
    void handleLockFreeQueue() {
        final LockFreeQueue queue=new LockFreeQueue();
        productLockFreeQueue(queue);
        consumeLockFreeQueue(queue);
    }
    void productLockFreeQueue(LockFreeQueue queue){

        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
                    int pos = atomicInteger.getAndIncrement();
                    queue.enq(pos);
                    System.out.println(Thread.currentThread().getName()
                            + "\tproduct:\t" + pos);
                    currentSleep();
                }
            });
            thread.start();
        }
    }
    void consumeLockFreeQueue(LockFreeQueue queue){
        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
                    Integer pos = (Integer)queue.deq();
                    System.out.println(Thread.currentThread().getName()
                            + "\tconsume:\t" + pos);
                    currentSleep();
                }
            });
            thread.start();
        }
    }

    // ==================================================================
    void handleBoundedQueue() {
        final BoundedQueue boundedQueue=new BoundedQueue();
        productBoundedQueue(boundedQueue);
        consumeBoundedQueue(boundedQueue);
    }
    void productBoundedQueue(BoundedQueue boundedQueue){

        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
                    int pos = atomicInteger.getAndIncrement();
                    boundedQueue.enq(pos);
                    System.out.println(Thread.currentThread().getName()
                            + "\tproduct:\t" + pos);
                    currentSleep();
                }
            });
            thread.start();
        }
    }
    void consumeBoundedQueue(BoundedQueue boundedQueue){
        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
                    Integer pos = (Integer)boundedQueue.deq();
                    System.out.println(Thread.currentThread().getName()
                            + "\tconsume:\t" + pos);
                    currentSleep();
                }
            });
            thread.start();
        }
    }

    // ==================================================================
    void currentSleep(){
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

    }
}
