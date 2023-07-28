package com.abc.art.stack;

public class Node<T> {
    public T value;
    public Node next;
    public Node(T value) {
        this.value = value;
        this.next = null;
    }
}
