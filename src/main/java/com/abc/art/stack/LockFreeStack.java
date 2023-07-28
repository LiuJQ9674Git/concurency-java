package com.abc.art.stack;

import com.abc.art.mutalexcute.Backoff;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class LockFreeStack <T>{
    AtomicReference<Node> top = new AtomicReference<Node>(null);
    static final int MIN_DELAY = Config.MIN_DELAY;
    static final int MAX_DELAY = Config.MAX_DELAY;
    Backoff backoff = new Backoff(MIN_DELAY, MAX_DELAY);
    //AtomicInteger atomicInteger=new AtomicInteger(0);
    protected boolean tryPush(Node node){
        Node oldTop = top.get();
        node.next = oldTop;
        return(top.compareAndSet(oldTop, node));
    }
    public void push(T value) {
        Node node = new Node(value);
        while (true) {
            if (tryPush(node)) {
                //atomicInteger.getAndIncrement();
                return;
            } else {
                try {
                    backoff.backoff();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    protected Node tryPop() throws EmptyException {
        Node oldTop = top.get();
        if (oldTop == null) {
            throw new EmptyException();
        }
        Node newTop = oldTop.next;
        if (top.compareAndSet(oldTop, newTop)) {
            //atomicInteger.getAndDecrement();
            return oldTop;
        } else {
            return null;
        }
    }
    public T pop() throws EmptyException {
        while (true) {
            Node returnNode = tryPop();
            if (returnNode != null) {
                return (T)returnNode.value;
            } else {
                try {
                    backoff.backoff();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }
//
//    public boolean empty(){
//        return atomicInteger.intValue()==0;
//    }
}
