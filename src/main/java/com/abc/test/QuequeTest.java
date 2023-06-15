package com.abc.test;
import java.util.concurrent.*;

public class QuequeTest {
    private static ExecutorService cachedThreadPool = new
            ThreadPoolExecutor(4,
            Runtime.getRuntime().availableProcessors() * 2,
            0, TimeUnit.MILLISECONDS,
            new SynchronousQueue<>(), r -> new Thread(r, "ThreadTest"));


    public static void doSynchronousQueue(){
        final SynchronousQueue<Integer> queue = new SynchronousQueue<Integer>();

        Thread putThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("put thread start");
                try {
                    System.out.println("put data : 1");
                    queue.put(1);
                    System.out.println("put data : 2");
                    queue.put(2);
                } catch (InterruptedException e) {
                }
                System.out.println("put thread end");
            }
        });

        Thread takeThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("take thread start");
                try {
                    System.out.println("take from putThread: " + queue.take());
                } catch (InterruptedException e) {
                }
                System.out.println("take thread end");
            }
        });

        Thread takeThread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("take1 thread start");
                try {
                    System.out.println("take1 from putThread: " + queue.take());
                } catch (InterruptedException e) {
                }
                System.out.println("take1 thread end");
            }
        });

        Thread takeThread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("take2 thread start");
                try {
                    System.out.println("take2 from putThread: " + queue.take());
                } catch (InterruptedException e) {
                }
                System.out.println("take1 thread end");
            }
        });

        putThread.start();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        takeThread1.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        takeThread2.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        takeThread.start();
        try {
            queue.put(3);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
//        try {
//            System.out.println("take from main: " + queue.take());
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }

    public static void doLinkedBlockingQueue(){
        BlockingQueue blockingQueue=new LinkedBlockingQueue();
        blockingQueue.offer("AAAAAA");
        blockingQueue.offer("BBBBBB");
        try {
            String str=(String) blockingQueue.take();
            System.out.println("str:\t"+str);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        blockingQueue.offer("CCCCCC");
    }

    public static void doSynchronousQueuePut(){
        BlockingQueue blockingQueue=new SynchronousQueue();
        try {
            blockingQueue.put("AAAAAA");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public static void doSynchronousQueuePutThread(){

        final SynchronousQueue<Integer> queue = new SynchronousQueue<Integer>();

        Thread putThread = new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("put thread start");
                try {
                    System.out.println("put data : 1");

                    //boolean b=queue.offer(1);
                   // System.out.println("put data : "+b);
                    queue.put(2);
                } catch (InterruptedException e) {
                }
                System.out.println("put thread end");
            }
        });
        putThread.start();
    }

    public static void doSynchronousQueueTake(){
        BlockingQueue blockingQueue=new SynchronousQueue();
        try {
            String str=(String) blockingQueue.take();
            System.out.println("str:\t"+str);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        blockingQueue.offer("CCCCCC");
    }

    public static void main(String[] args){

        //doSynchronousQueue();
        //doLinkedBlockingQueue();
        doSynchronousQueuePut();
        //doSynchronousQueuePutThread();
    }
}
