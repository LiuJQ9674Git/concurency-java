package com.abc.art.sets;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FineList<T> implements Set<T>{
    private final Node head;
    FineList(){
        head = new Node(Integer.MIN_VALUE);
        head.next = new Node(Integer.MAX_VALUE);
    }
    public boolean add(T item) {
        int key = item.hashCode();
        head.lock();
        Node pred = head;
        try {
            Node curr = pred.next;
            curr.lock();
            try {
                while (curr.key < key) {
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                if (curr.key == key) {
                    return false;
                }
                Node node = new Node(item);
                node.next = curr;
                pred.next = node;
                return true;
            } finally {
                curr.unlock();
            }
        } finally {
            pred.unlock();
        }
    }
    public boolean remove(T item) {
        int key = item.hashCode();
        head.lock();
        Node pred = head;
        try {
            Node curr = pred.next;
            curr.lock();
            try {
                while (curr.key < key) {
                    pred.unlock();
                    pred = curr;
                    curr = curr.next;
                    curr.lock();
                }
                if (curr.key == key) {
                    pred.next = curr.next;
                    return true;
                }
                return false;
            } finally {
                curr.unlock();
            }
        } finally {
            pred.unlock();
        }
    }

    @Override
    public boolean contains(T x) {
        return false;
    }


    class Node<T> {
        private Lock lock = new ReentrantLock();
        T item;
        int key;
        Node next;
        Node(int key){
            this.key=key;
        }
        Node(T item){
            this.item=item;
            this.key=item.hashCode();
        }
        void lock(){
            lock.lock();
        }
        void unlock(){
            lock.unlock();
        }
    }
}
