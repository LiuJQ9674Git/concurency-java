package com.abc.test;

import java.util.concurrent.locks.StampedLock;

public class StampedLockTest {
    private double x, y;
    private final StampedLock sl = new StampedLock();
    // an exclusively locked method


    void move(double deltaX, double deltaY) {
        long stamp = sl.writeLock();
        System.out.println("stamp->:\t"+stamp);
        /**
         * stamp->:	384
         * stamp->:	640
         * stamp->:	896
         */
        try {
//            try {
//                Thread.sleep(1000);
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
            x += deltaX;
            y += deltaY;
        } finally {
            sl.unlockWrite(stamp);
        }
    }

    void anysMove(){
        Thread t=new Thread(
                ()->{
                    move(10,10);
                });
        t.start();
    }

    double distanceFromOrigin() {
        long stamp = sl.tryOptimisticRead();
        System.out.println("stamp:\t"+stamp);
          try {
              retryHoldingLock:
              for (;; stamp = sl.readLock()) {
                  System.out.println("stamp:\t"+stamp);
                  if (stamp == 0L)
                      continue retryHoldingLock;
                  // possibly racy reads
                  double currentX = x;
                  double currentY = y;
                  if (!sl.validate(stamp))
                      continue retryHoldingLock;
                  return Math.hypot(currentX, currentY);
              }
          } finally {
              if (StampedLock.isReadLockStamp(stamp))
                  sl.unlockRead(stamp);
          }
    }

    void anysDistanceFromOrigin(){
        Thread t=new Thread(
                ()->{
                    moveIfAtOrigin2(10,30);
                    //distanceFromOrigin();
        });
        t.start();
    }

    static  void tryTryOptimisticRead(){
        StampedLockTest stampedLockTest=new StampedLockTest();
        for(int i=3 ; i>0; i--) {
            System.out.println("i:\t"+i);
            stampedLockTest.anysDistanceFromOrigin();
        }
    }

    void moveIfAtOrigin2(double newX, double newY) {
        long stamp = sl.readLock();
        /**
         * stamp:	259
         * stamp:	258
         * stamp:	257
         */
        System.out.println("stamp:\t"+stamp);
        try {
            while (x == 0.0 && y == 0.0) {
                long ws = sl.tryConvertToWriteLock(stamp);
                if (ws != 0L) {
                    stamp = ws;
                    x = newX;
                    y = newY;
                    break;
                }
                else {
                    sl.unlockRead(stamp);
                    stamp = sl.writeLock();
                }
            }
        } finally {
            sl.unlock(stamp);
        }
    }
    void anysMoveReadLock(){
        Thread t=new Thread(
                ()->{
                    moveIfAtOrigin2(10,10);
                });
        t.start();
    }

    static  void handleMoveeadLock(){
        StampedLockTest stampedLockTest=new StampedLockTest();
        for(int i=3 ; i>0; i--) {
            System.out.println("i:\t"+i);
            stampedLockTest.anysMoveReadLock();
        }
    }

    static  void tryAcquireWrite(){
        StampedLockTest stampedLockTest=new StampedLockTest();
        for(int i=3 ; i>0; i--) {
            System.out.println("i:\t"+i);
            stampedLockTest.anysMove();
        }
    }

    static  void writeRead(){
        StampedLockTest stampedLockTest=new StampedLockTest();
        stampedLockTest.anysDistanceFromOrigin();
       // stampedLockTest.anysMove();
    }

    public static void main(String[] args){
        //tryAcquireWrite();
        //tryTryOptimisticRead();

        //handleMoveeadLock();
        writeRead();

    }
}
