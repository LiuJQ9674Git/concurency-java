package com.abc.concurrency.gate;

public class ThreadGateTest {

    public static void main(String[] args){
        ThreadGate threadGate=new ThreadGate();
        try {
            threadGate.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
