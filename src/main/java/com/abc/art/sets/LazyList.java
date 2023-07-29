package com.abc.art.sets;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LazyList<T> implements Set<T> {

    private final Node head;

    public LazyList() {
        head = new Node(Integer.MIN_VALUE);
        head.next = new Node(Integer.MAX_VALUE);
    }

    @Override
    public boolean add(T item) {
        int key = item.hashCode();
        while (true) {
            Node pred = head;
            Node curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
                pred.lock();
            }
            //乐观处理结束
            pred.lock();
            try {
                curr.lock();
                try {
                    if (validate(pred, curr)) {
                        if (curr.key == key) {
                            return false;
                        } else {
                            Node node = new Node(item);
                            node.next = curr;
                            pred.next = node;
                            return true;
                        }
                    }//验证之后加入动作
                } finally {
                    curr.unlock();
                }
            } finally {
                pred.unlock();
            }
        }
    }

    @Override
    public boolean remove(T item) {
        int key = item.hashCode();
        while (true) {
            Node pred = head;
            Node curr = head.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            pred.lock();

            try {
                curr.lock();
                try {
                    if (validate(pred, curr)) {
                        if (curr.key == key) {
                            curr.marked = true;
                            pred.next = curr.next;
                            return true;
                        } else {
                            return false;
                        }
                    }
                } finally {
                    curr.unlock();
                }
                //
            } finally {
                pred.unlock();
            }
        }
    }

    @Override
    public boolean contains(T item) {
        int key = item.hashCode();
        Node curr = head;
        while (curr.key < key) {
            curr = curr.next;
        }
        return curr.key == key && !curr.marked;
    }

    private boolean validate(Node pred, Node curr) {
        return !pred.marked && !curr.marked && pred.next == curr;
    }

    class Node<T> {
        private Lock lock = new ReentrantLock();
        boolean marked;
        T item;
        int key;
        Node next;

        Node(int key) {
            this.key = key;
        }

        Node(T item) {
            this.item = item;
            this.key = item.hashCode();
        }

        void lock() {
            lock.lock();
        }

        void unlock() {
            lock.unlock();
        }
    }
}
