package com.abc.art.mutalexcute;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class TOLockTest {
    private volatile long value;
    TOLock toLock=new TOLock();
    public static void dogeTOLockTest(){
        TOLockTest counter=new TOLockTest();
        //int k=0;
        for(int i=0;i<14;i++) {
            Thread thread=new Thread(()->{
                long c = counter.getAndIncrement();
                try {
                    int k= ThreadLocalRandom.current().nextInt(10000);
                    Thread.sleep(k);
                    System.out.println("Thread\t"+Thread.currentThread().getName()+
                            "\tsleep:\t"+k+"\tvalue:\t"+c);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
            );
            thread.start();

        }

    }
    public long getAndIncrement() {
        long temp=0;
        try {
            if(toLock.tryLock(5000, TimeUnit.MILLISECONDS)) {
                temp = value;
                value = temp + 1;
            }else{
                System.out.println("Thread\t"+Thread.currentThread().getName()+"\tgetAndIncrement else");
            }
        } catch (InterruptedException e) {
            System.out.println("Thread\t"+Thread.currentThread().getName()+e.getMessage());
            throw new RuntimeException(e);
        }

        toLock.unlock();
        return temp;
    }

    public static void main(String[] args){
        //doLockInt();
        //doTTASLock();
        dogeTOLockTest();
    }
}
