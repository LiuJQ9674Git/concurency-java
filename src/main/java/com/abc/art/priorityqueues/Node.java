package com.abc.art.priorityqueues;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicMarkableReference;

public  final class Node<T> {
    final T item;
    final int score;
    AtomicBoolean marked;
    final AtomicMarkableReference<Node<T>>[] next;
    public Node(int myPriority){
        this(null,myPriority);
    }
    public Node(T x, int myPriority) {
        this.item=x;
        this.score=myPriority;
        next= (AtomicMarkableReference<Node<T>>[]) new AtomicMarkableReference[myPriority];
        for(int i=0;i<myPriority;i++) {
            next[i]=new AtomicMarkableReference(null, false);
        }
    }
}
