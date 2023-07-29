package com.abc.art.queue;

import java.util.concurrent.atomic.AtomicReference;

public class LockFreeQueue<T> {
    AtomicReference<Node> head, tail;

    public LockFreeQueue() {
        Node node = new Node(null);
        head = new AtomicReference(node);
        tail = new AtomicReference(node);
    }

    public void enq(T value) {
        Node node = new Node(value);
        while (true) {
            Node last = tail.get();
            Node next = (Node) last.next.get();
            if (last == tail.get()) {
                if (next == null) {//当前线程执行
                    //当前线程的next更新为node
                    if (last.next.compareAndSet(next, node)) {
                        //当前线程的next更新为node成功，则更新tail
                        tail.compareAndSet(last, node);
                        return;
                    }
                } else {//其它线程帮助完成了部分操作，即next已经挂到链上
                    tail.compareAndSet(last, next);
                }
            }
        }
    }

    public T deq() throws EmptyException {
        while (true) {
            Node first = head.get();
            Node last = tail.get();
            Node next = (Node) first.next.get();
            if (first == head.get()) {
                if (first == last) {//只有一个
                    if (next == null) {
                        throw new EmptyException();
                    }
                    tail.compareAndSet(last, next);
                } else {
                    //多个节点
                    T value = (T) next.value;
                    if (head.compareAndSet(first, next)) {
                        return value;
                    }
                }
            }
        }
    }

    static class Node<T> {
        public T value;
        public AtomicReference<Node> next;

        public Node(T value) {
            this.value = value;
            next = new AtomicReference<Node>(null);
        }
    }
}
