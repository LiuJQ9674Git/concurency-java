package com.abc.art.sets;

import com.abc.art.ErrorDebug;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class OptimisticList<T> implements Set<T> {

    private final Node head;

    OptimisticList() {
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
            Node curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            //乐观处理结束

            pred.lock();
            try {
                curr.lock();
                try {
                    if (validate(pred, curr)) {
                        if (curr.key == key) {
                            pred.next = curr.next;
                            return true;
                        } else {
                            return false;
                        }
                    }// 验证之后的删除操作
                } finally {
                    curr.unlock();
                }
            } finally {
                pred.unlock();
            }
        }
    }

    private boolean validate(Node pred, Node curr) {
        Node node = head;
        while (node.key <= pred.key) {
            if (node == pred) {
                return pred.next == curr;
            }
            node = node.next;
        }
        return false;
    }

    @Override
    public boolean contains(T item) {
        int key = item.hashCode();
        while (true) {
            Node pred = head;
            Node curr = pred.next;
            while (curr.key < key) {
                pred = curr;
                curr = curr.next;
            }
            //乐观处理结束
            pred.lock();
            try {
                curr.lock();
                try {
                    if (validate(pred, curr)) {
                        return (curr.key == key);
                    }//
                } finally {
                    curr.unlock();
                }
            } finally {
                pred.unlock();
            }
        }
    }


    class Node<T> {
        private Lock lock = new ReentrantLock();
        T item;
        int key;
        volatile Node next;

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
