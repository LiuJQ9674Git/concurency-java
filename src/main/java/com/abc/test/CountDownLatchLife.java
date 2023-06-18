package com.abc.test;

import java.util.concurrent.CountDownLatch;

public class CountDownLatchLife {

    static int N=2;
    public  static void main(String[] args) throws InterruptedException {
        //CountDownLatch startSignal = new CountDownLatch(1);
        CountDownLatch doneSignal = new CountDownLatch(1);
        doneSignal.await();
        //doneSignal.countDown();
        //Thread.interrupted();


    }
}
