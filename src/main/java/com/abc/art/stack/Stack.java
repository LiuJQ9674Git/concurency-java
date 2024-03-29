package com.abc.art.stack;

import java.util.concurrent.atomic.AtomicInteger;

public class Stack<T>  {
    static int DEFEAULT_CAPACITY=128;
    private AtomicInteger top;
    private T[] items;

    public Stack(){
        this(DEFEAULT_CAPACITY);
    }
    public Stack(int capacity) {
        top = new AtomicInteger();
        items = (T[]) new Object[capacity];
    }
    public void push(T x) throws FullException {
        int i = top.getAndIncrement();
        if (i >= items.length) { // stack is full
            top.getAndDecrement(); // restore state
            throw new FullException();
        }
        items[i] = x;
    }

    public T pop() throws EmptyException {
        int i = top.getAndDecrement() - 1;
        if (i < 0) { // stack is empty
            top.getAndIncrement(); // restore state
            throw new EmptyException();
        }
        return items[i];
    }

    public boolean empty() {
        return top.get() == 0;
    }

}
