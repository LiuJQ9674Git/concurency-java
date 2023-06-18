package com.abc.test;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class LockTest {
    static final Lock lock=new ReentrantLock();
    static final int N=2;
    public static void handleLock(){

        lock.lock();

        lock.unlock();

    }

    public static void handleTask(){
        for(int i=0;i<N;i++) {
            Thread thread = new Thread(() -> {
                doTask();
            });
            thread.start();
        }

        for(int i=0;i<N;i++) {
            Thread thread = new Thread(() -> {
                doTaskNoUnLock();
            });
            thread.start();
        }
    }
    static void doTask(){
        lock.lock();
        System.out.println("------Task Running-------");
        lock.unlock();
    }
    static void doTaskNoUnLock(){
        lock.lock();
        System.out.println("------Task Running-------");
        lock.unlock();
    }
    public static void main(String[] args){
        //handleLock();
        handleTask();
    }

    class ForC{
        int doTask(){
            for(;;){
                System.out.println("ok");
                break;
            }
            return endTask();
        }
        int endTask(){
            return 10;
        }

    }
}
