package com.abc.art.mutalexcute;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.Condition;

public class TOLock implements java.util.concurrent.locks.Lock{
    static QNode AVAILABLE = new QNode("AVAILABLE");
    AtomicReference<QNode> tail;
    ThreadLocal<QNode> myNode;
    public TOLock() {
        tail = new AtomicReference<QNode>(null);
        myNode = new ThreadLocal<QNode>() {
            protected QNode initialValue() { return new QNode(Thread.currentThread().getName());
            }
        };
    }
    static class QNode {
        final String name;
        public volatile QNode pred = null;

        QNode(String name){
            this.name=name;
        }
        public String toString(){
            return name;
        }
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
        QNode qnode = new QNode(Thread.currentThread().getName()+"|Try");
        myNode.set(qnode);
        qnode.pred = null;
        QNode myPred = tail.getAndSet(qnode);

        System.err.println(Thread.currentThread().getName()+
                "\ttryLock enter 1 "+
                "\n\t qnode:\t"+qnode+"\tmyPred:\t"+myPred);

        if (myPred == null || myPred.pred == AVAILABLE) {
            System.out.println("\t"+Thread.currentThread().getName()+
                    "\ttryLock myPred == null || myPred.pred == AVAILABLE 2 "+
                    "\n\t qnode:\t"+qnode+"\tmyPred:\t"+myPred);
            return true;
        }
        System.out.println("\t"+Thread.currentThread().getName()+
                "\ttryLock while start 3 "+
                "\n\t qnode:\t"+qnode+"\tmyPred:\t"+myPred);

        while (System.currentTimeMillis() - startTime < patience) {
            QNode predPred = myPred.pred;
//            System.out.println("Thread\t"+Thread.currentThread().getName()+
//                    "\ttryLock while myPred.pred 4 "+
//                    "\n\t qnode:\t"+qnode+"\tmyPred:\t"+myPred+"\tpredPred:\t"+predPred);
            if (predPred == AVAILABLE) {
                System.out.println("\t"+Thread.currentThread().getName()+
                        "\ttryLock while predPred == AVAILABLE 5 "+
                        "\n\t qnode:\t"+qnode+"\tmyPred:\t"+myPred+"\tpredPred:\t"+predPred);
                return true;
            } else if (predPred != null) {
                System.err.println("\t"+Thread.currentThread().getName()+
                        "\ttryLock while predPred != null 6 "+
                        "\n\t qnode:\t"+qnode+"\t myPred:\t"+myPred+"\tpredPred:\t"+predPred);
                myPred = predPred;
            }
        }

        System.out.println("\t"+Thread.currentThread().getName()+
                "\ttryLock while out 8 "+
                "\n\t qnode:\t"+qnode+"\tmyPred:\t"+myPred);

        if (!tail.compareAndSet(qnode, myPred)) {
            System.out.println("\t"+Thread.currentThread().getName()+
                    "\ttryLock !tail.compareAndSet(qnode, myPred) 9 "+
                    "\n\t qnode:\t"+qnode+"\tmyPred:\t"+myPred);
            qnode.pred = myPred;
        }

        System.out.println("\t"+Thread.currentThread().getName()+
                "\ttryLock while exter a "+
                "\n\t qnode:\t"+qnode+"\tmyPred:\t"+myPred);

        return false;
    }

    @Override
    public void unlock() {
        QNode qnode = myNode.get();

        int k= ThreadLocalRandom.current().nextInt(500);
        try {
            Thread.sleep(k);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        System.out.println("\n\t"+Thread.currentThread().getName()+
                "\tunlock  1 "+
                "\n\t qnode:\t"+qnode+"\tqnode.pred\t"+qnode.pred );
        if (!tail.compareAndSet(qnode, null)) {
            // 当前节点不是尾部tail节点时，qnode.pred设置为AVAILABLE，
            // 即前一个任务可以运行
            qnode.pred = AVAILABLE;
            System.err.println("\t"+Thread.currentThread().getName()+
                    "\tunlock 2"+
                    "\n\t qnode:\t"+qnode+"\tqnode.pred\t"+qnode.pred );
        }
        System.out.println("\t"+Thread.currentThread().getName()+
                "\tunlock  3 "+
                "\n\t qnode:\t"+qnode+"\tqnode.pred\t"+qnode.pred );
    }

    @Override
    public Condition newCondition() {
        return null;
    }
}
