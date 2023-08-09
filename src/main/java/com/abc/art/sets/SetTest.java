package com.abc.art.sets;

import com.abc.art.ErrorDebug;

import java.util.concurrent.atomic.AtomicInteger;

public class SetTest {
    static int NUM_THREAD=8;
    AtomicInteger atomicInteger=new AtomicInteger(0);

    public static void main(String[] args){
        SetTest setTest=new SetTest();
        //setTest.handleSynchronousDualQueue();
        //setTest.handleFineList();
        //setTest.handleOptimisticList();
        //setTest.handleLazyList();
        //setTest.handleLockFreeList();
        //setTest.handleBucketList();
        setTest.handleLockFreeHashSet();
    }

    public void handleLockFreeHashSet(){
        LockFreeHashSet queue=new LockFreeHashSet(2);
//        productLockFreeList(queue);
//        consumeLockFreeList(queue);
        queue.add(1);
        queue.add(2);
        queue.add(3);
        queue.add(3);

    }

    public void handleBucketList(){
        final Set queue=new BucketList();
//        productLockFreeList(queue);
//        consumeLockFreeList(queue);
        queue.add(1);
        queue.add(2);
        queue.add(3);
        boolean sec=queue.contains(2);
        queue.remove(1);
        boolean f=queue.contains(1);
        queue.contains(1);

    }

    void handleLockFreeList() {
        final Set queue=new LockFreeList();
        productLockFreeList(queue);
        consumeLockFreeList(queue);
    }
    void productLockFreeList(Set set){

        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
                    int pos = atomicInteger.getAndIncrement();
                    if(set.add(pos)) {
                        System.out.println(Thread.currentThread().getName()
                                + "\tproduct:\t" + pos);
                    }
                    currentSleep();
                }
            });
            thread.start();
        }
    }
    void consumeLockFreeList(Set set){
        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
                    Integer pos = atomicInteger.get();
                    if(set.remove(pos-1)) {
                        System.out.println(Thread.currentThread().getName()
                                + "\tconsume:\t" + pos);
                    }
                    currentSleep();
                }
            });
            thread.start();
        }
    }
    // ==================================================================

    void handleLazyList() {
        final Set queue=new LazyList();
        productLazyList(queue);
        consumeLazyList(queue);
    }
    void productLazyList(Set set){

        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
                    int pos = atomicInteger.getAndIncrement();
                    if(set.add(pos)) {
                        System.out.println(Thread.currentThread().getName()
                                + "\tproduct:\t" + pos);
                    }
                    currentSleep();
                }
            });
            thread.start();
        }
    }
    void consumeLazyList(Set set){
        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
                    Integer pos = atomicInteger.get();
                    if(set.remove(pos-1)) {
                        System.out.println(Thread.currentThread().getName()
                                + "\tconsume:\t" + pos);
                    }
                    currentSleep();
                }
            });
            thread.start();
        }
    }
    // ==================================================================

    @ErrorDebug
    void handleOptimisticList() {
        final Set queue=new OptimisticList();
        productOptimisticList(queue);
        consumeOptimisticList(queue);
    }
    void productOptimisticList(Set set){

        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
                    int pos = atomicInteger.getAndIncrement();
                    if(set.add(pos)) {
                        System.out.println(Thread.currentThread().getName()
                                + "\tproduct:\t" + pos);
                    }
                    currentSleep();
                }
            });
            thread.start();
        }
    }
    void consumeOptimisticList(Set set){
        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
                    Integer pos = atomicInteger.get();
                    if(set.remove(pos-1)) {
                        System.out.println(Thread.currentThread().getName()
                                + "\tconsume:\t" + pos);
                    }
                    currentSleep();
                }
            });
            thread.start();
        }
    }
    // ==================================================================

    void handleFineList() {
        final Set queue=new FineList();
        productFineList(queue);
        consumeFineList(queue);
    }
    void productFineList(Set set){

        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
                    int pos = atomicInteger.getAndIncrement();
                    if(set.add(pos)) {
                        System.out.println(Thread.currentThread().getName()
                                + "\tproduct:\t" + pos);
                    }
                    currentSleep();
                }
            });
            thread.start();
        }
    }
    void consumeFineList(Set set){
        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
                    Integer pos = atomicInteger.get();
                    if(set.remove(pos-1)) {
                        System.out.println(Thread.currentThread().getName()
                                + "\tconsume:\t" + pos);
                    }
                    currentSleep();
                }
            });
            thread.start();
        }
    }
    // ==================================================================

    void handleSynchronousDualQueue() {
        final Set queue=new CoarseList();
        productCoarseList(queue);
        consumeCoarseList(queue);
    }
    void productCoarseList(Set set){

        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
                    int pos = atomicInteger.getAndIncrement();
                    if(set.add(pos)) {
                        System.out.println(Thread.currentThread().getName()
                                + "\tproduct:\t" + pos);
                    }
                    currentSleep();
                }
            });
            thread.start();
        }
    }
    void consumeCoarseList(Set set){
        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
                    Integer pos = atomicInteger.get();
                    if(set.remove(pos-1)) {
                        System.out.println(Thread.currentThread().getName()
                                + "\tconsume:\t" + pos);
                    }
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
