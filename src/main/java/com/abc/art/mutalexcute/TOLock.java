package com.abc.art.mutalexcute;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;

public class TOLock implements java.util.concurrent.locks.Lock{
    static QNode AVAILABLE = new QNode();
    AtomicReference<QNode> tail;
    ThreadLocal<QNode> myNode;
    public TOLock() {
        tail = new AtomicReference<QNode>(null);
        myNode = new ThreadLocal<QNode>() {
            protected QNode initialValue() { return new QNode();
            }
        };
    }
    static class QNode {
        public volatile QNode pred = null;
    }

    @Override
    public void lock() {
        int k=ThreadLocalRandom.current().nextInt(10);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        long startTime = System.currentTimeMillis();
        long patience = TimeUnit.MILLISECONDS.convert(time, unit);
        QNode qnode = new QNode();
        myNode.set(qnode);
        qnode.pred = null;
        QNode myPred = tail.getAndSet(qnode);

        System.out.println("Thread\t"+Thread.currentThread().getName()+
                "\ttryLock tail.getAndSet 1 "+
                "\n\t qnode:\t"+qnode+"\tmyPred:\t"+myPred);

        if (myPred == null || myPred.pred == AVAILABLE) {
//            System.out.println("Thread\t"+Thread.currentThread().getName()+
//                    "\ttryLock myPred == null || myPred.pred == AVAILABLE 2 "+
//                    "\n\t qnode:\t"+qnode+"\tmyPred:\t"+myPred);
            return true;
        }
        while (System.currentTimeMillis() - startTime < patience) {
            QNode predPred = myPred.pred;
//            System.out.println("Thread\t"+Thread.currentThread().getName()+
//                    "\ttryLock while myPred.pred 3 "+
//                    "\n\t myPred:\t"+myPred+"\tpredPred:\t"+predPred);
            if (predPred == AVAILABLE) {
//                System.out.println("Thread\t"+Thread.currentThread().getName()+
//                        "\ttryLock while predPred == AVAILABLE 4 "+
//                        "\n\t myPred:\t"+myPred+"\tpredPred:\t"+predPred);
                return true;
            } else if (predPred != null) {
                System.out.println("Thread\t"+Thread.currentThread().getName()+
                        "\ttryLock while predPred != null 5 "+
                        "\n\t myPred:\t"+myPred+"\tpredPred:\t"+predPred);
                myPred = predPred;
            }
        }
//        System.out.println("Thread\t"+Thread.currentThread().getName()+
//                "\ttryLock while out 6 "+
//                "\n\t qnode:\t"+qnode+"\tmyPred:\t"+myPred);
        if (!tail.compareAndSet(qnode, myPred)) {
            System.out.println("Thread\t"+Thread.currentThread().getName()+
                    "\ttryLock !tail.compareAndSet(qnode, myPred) 7 "+
                    "\n\t qnode:\t"+qnode+"\tmyPred:\t"+myPred);
            qnode.pred = myPred;
        }
        return false;
    }

    @Override
    public void unlock() {
        QNode qnode = myNode.get();
//        System.out.println("Thread\t"+Thread.currentThread().getName()+
//                "\tunlock  1 "+
//                "\n\t qnode:\t"+qnode);
        if (!tail.compareAndSet(qnode, null)) {
            // 当前节点不是尾部tail节点时，qnode.pred设置为AVAILABLE，
            // 即前一个任务可以运行
            qnode.pred = AVAILABLE;
            System.out.println("Thread\t"+Thread.currentThread().getName()+
                    "\tunlock !tail.compareAndSet(qnode, null) 2 "+
                    "\n\t qnode:\t"+qnode+"\tqnode.pred\t"+qnode.pred );

        }
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
