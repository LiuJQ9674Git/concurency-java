package com.abc.test;

import com.abc.concurrency.collection.LinkedQueue;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class LinkedQueueTest {

    public static void mainLinkedQueue(){
        LinkedQueue linkedQueue=new LinkedQueue();
        linkedQueue.put(1);
        linkedQueue.put(2);
        linkedQueue.put(3);
        linkedQueue.put(4);
    }

    public static void main(String[] args){
        ConcurrentLinked linkedQueue=new ConcurrentLinked();
        linkedQueue.offer(1);
        linkedQueue.offer(2);
        linkedQueue.offer(3);
        linkedQueue.offer(4);
        linkedQueue.offer(5);
    }

    public static void mainConcurrentLinkedQueue(){
        ConcurrentLinkedQueue linkedQueue=new ConcurrentLinkedQueue();
        linkedQueue.offer(1);
        linkedQueue.offer(2);
        linkedQueue.offer(3);
        linkedQueue.offer(4);
        linkedQueue.offer(5);
    }
}
