package com.abc.art.mutalexcute;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.AtomicStampedReference;
import java.util.concurrent.locks.Condition;

public class CompositeLock implements java.util.concurrent.locks.Lock{
    private static final int SIZE=16;//核数
    private static final int MIN_BACKOFF =10; //毫秒
    private static final int MAX_BACKOFF =10000; //毫秒

    enum State {FREE, WAITING, RELEASED, ABORTED};
    class QNode {
        AtomicReference<State> state;
        QNode pred;
        public QNode() {
            state = new AtomicReference<State>(State.FREE);
        }
    }

    AtomicStampedReference<QNode> tail;
    QNode[] waiting;
    ThreadLocal<QNode> myNode = new ThreadLocal<QNode>() {
        protected QNode initialValue() {
            return null;
        };
    };

    public CompositeLock() {
        tail = new AtomicStampedReference<QNode>(null,0);
        waiting = new QNode[SIZE];
        for (int i = 0; i < waiting.length; i++) {
            waiting[i] = new QNode();
        }
    }

    @Override
    public boolean tryLock(long time, TimeUnit unit) throws InterruptedException {
        long patience = TimeUnit.MILLISECONDS.convert(time, unit);
        long startTime = System.currentTimeMillis();
        Backoff backoff = new Backoff(MIN_BACKOFF, MAX_BACKOFF);
        try {
            QNode node = acquireQNode(backoff, startTime, patience);
            QNode pred = spliceQNode(node, startTime, patience);
            waitForPredecessor(pred, node, startTime, patience);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    @Override
    public void unlock() {
        QNode acqNode = myNode.get();
        acqNode.state.set(State.RELEASED);
        myNode.set(null);
    }

    // --------------- private function impliments --------------------------//
    private QNode acquireQNode(Backoff backoff, long startTime, long patience)
            throws TimeoutException, InterruptedException {
        QNode node = waiting[ThreadLocalRandom.current().nextInt(SIZE)];
        QNode currTail;
        int[] currStamp = {0};
        while (true) {
            if (node.state.compareAndSet(State.FREE, State.WAITING)) {
                return node;
            }
            //
            currTail = tail.get(currStamp);
            State state = node.state.get();
            if (state == State.ABORTED || state == State.RELEASED) {
                if (node == currTail) {
                    QNode myPred = null;
                    if (state == State.ABORTED) {
                        myPred = node.pred;
                    }
                    if (tail.compareAndSet(currTail, myPred,
                            currStamp[0], currStamp[0]+1)) {
                        node.state.set(State.WAITING);
                        return node;
                    }
                } //node == currTail function over
            } // state check over
            backoff.backoff();
            if (timeout(patience, startTime)) {
                throw new TimeoutException();
            }
        }
    }

    private QNode spliceQNode(QNode node, long startTime, long patience)
            throws TimeoutException {

        QNode currTail;
        int[] currStamp = {0};
        do {
            currTail = tail.get(currStamp);
            if (timeout(startTime, patience)) {
                node.state.set(State.FREE);
                throw new TimeoutException();
            }
        } while (!tail.compareAndSet(currTail, node,
                                     currStamp[0], currStamp[0]+1));
        return currTail;

    }

    private void waitForPredecessor(QNode pred, QNode node,
                                    long startTime, long patience)
            throws TimeoutException {
        int[] stamp = {0};
        if (pred == null) {
            myNode.set(node);
            return;
        }
        State predState = pred.state.get();
        while (predState != State.RELEASED) {
            if (predState == State.ABORTED) {
                QNode temp = pred;
                pred = pred.pred;
                temp.state.set(State.FREE);
            }
            if (timeout(patience, startTime)) {
                node.pred = pred;
                node.state.set(State.ABORTED);
                throw new TimeoutException();
            }
            predState = pred.state.get();
        }
        pred.state.set(State.FREE);
        myNode.set(node);
        return;
    }

    private boolean timeout(long patience,long startTime){
        while (System.currentTimeMillis() - startTime < patience) {
            return false;
        }
        return true;
    }

    // ************************************************* //

    @Override
    public void lock() {

    }

    @Override
    public Condition newCondition() {
        return null;
    }


    @Override
    public void lockInterruptibly() throws InterruptedException {

    }

    @Override
    public boolean tryLock() {
        return false;
    }
}
