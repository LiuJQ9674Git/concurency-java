package com.abc.art.priorityqueues;

public interface PQueue<T> {
    void add(T item, int score);
    T removeMin();
}
