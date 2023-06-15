package com.abc.test;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchTest {
    static int N=2;
    public  static void main(String[] args) throws InterruptedException {
        CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(N);
        for (int i = 0; i < N; ++i) // create and start threads
            new Thread(new Worker(startSignal, doneSignal,i)).start();
        //没有开启
        doSomethingFirst();            // don't let run yet
        Thread.interrupted();
        startSignal.countDown();      // let all threads proceed，所有线程处理
        Thread.interrupted();
        doSomethingSencond();
        Thread.interrupted();
        doneSignal.await();           // wait for all to finish，等待处理完毕
        Thread.interrupted();
        doSomethingThird();
    }


    static void doSomethingFirst(){
        System.out.println("------Main-Thread First---------");
    }

    static void doSomethingSencond(){
        System.out.println("------Main-Thread Sencond---------");
    }
    static void doSomethingThird(){
        System.out.println("------Main-Thread Third---------");
    }
}

class Worker implements Runnable {
    private final CountDownLatch startSignal;
    private final CountDownLatch doneSignal;

    private final int pos;
    Worker(CountDownLatch startSignal, CountDownLatch doneSignal,int pos) {
        this.startSignal = startSignal;
        this.doneSignal = doneSignal;
        this.pos=pos;
    }
    public void run() {
        try {
            //等待开始信号
            startSignal.await();
            Thread.interrupted();
            doWork();//执行任务
            Thread.interrupted();
            doneSignal.countDown();
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } // return;
    }
    void doWork() {
        System.out.println("------Child Thread:\t"+pos);
    }
}