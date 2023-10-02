package com.abc.test;

public class ConcurrentLinkedTest {
    public static void main(String[] args){
        ConcurrentLinked concurrentLinked=new ConcurrentLinked();
        concurrentLinked.offer(1);
        concurrentLinked.offer(2);
        concurrentLinked.offer(3);
        concurrentLinked.offer(4);
    }
}
