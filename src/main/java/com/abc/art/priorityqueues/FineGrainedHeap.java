package com.abc.art.priorityqueues;

import com.abc.art.mutalexcute.ThreadId;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class FineGrainedHeap<T> implements PQueue<T> {

    private static int ROOT = 1;
    private static int NO_ONE = -1;
    private Lock heapLock;
    int next;
    HeapNode<T>[] heap;
    public FineGrainedHeap(int capacity) {
        heapLock = new ReentrantLock();
        next = ROOT;
        heap = (HeapNode<T>[]) new HeapNode[capacity + 1];
        for (int i = 0; i < capacity + 1; i++) {
            heap[i] = new HeapNode<T>();
        }
    }

    @Override
    public void add(T item, int score) {
        heapLock.lock();
        int child = next++;
        heap[child].lock();
        heap[child].init(item, score);
        heapLock.unlock();
        heap[child].unlock();

        while (child > ROOT) {
            int parent = child / 2;
            heap[parent].lock();
            heap[child].lock();
            int oldChild = child;
            try {

                if (heap[parent].tag == Status.AVAILABLE && heap[child].amOwner()) {
                    if (heap[child].score < heap[parent].score) {
                        swap(child, parent);
                        child = parent;
                    } else {
                        heap[child].tag = Status.AVAILABLE;
                        heap[child].owner = NO_ONE;
                        return;
                    } //

                }else if (!heap[child].amOwner()) {
                    child = parent;
                }
            } finally {
                heap[oldChild].unlock();
                heap[parent].unlock();
            }
        } //
        if (child == ROOT) {
            heap[ROOT].lock();
            if (heap[ROOT].amOwner()) {
                heap[ROOT].tag = Status.AVAILABLE;
                heap[child].owner = NO_ONE;
            } //
            heap[ROOT].unlock();
        }
    }

    @Override
    public T removeMin() {
        heapLock.lock();
        int bottom = --next;

        heap[ROOT].lock();
        heap[bottom].lock();
        heapLock.unlock();

        T item = heap[ROOT].item;
        heap[ROOT].tag = Status.EMPTY;
        heap[ROOT].owner = NO_ONE;
        //
        swap(bottom, ROOT);
        heap[bottom].unlock();
        if (heap[ROOT].tag == Status.EMPTY) {
            heap[ROOT].unlock();
            return item;
        }
        heap[ROOT].tag = Status.AVAILABLE;
        int child = 0;
        int parent = ROOT;
        while (parent < heap.length / 2) {
            int left = parent * 2;
            int right = (parent * 2) + 1;
            heap[left].lock();
            heap[right].lock();
            if (heap[left].tag == Status.EMPTY) {
                heap[right].unlock();
                heap[left].unlock();
                break;
            } else if (heap[right].tag == Status.EMPTY ||
                    heap[left].score < heap[right].score) {
                heap[right].unlock();
                child = left;
            }else {
                heap[left].unlock();
                child = right;
            }
            //
            if (heap[child].score < heap[parent].score &&
                    heap[child].tag != Status.EMPTY) {
                swap(parent, child);
                heap[parent].unlock();
                parent = child;
            }else {
                heap[child].unlock();
                break;
            }
        }
        heap[parent].unlock();
        return item;
    }

    private void swap(int parent,int child){
        HeapNode p=heap[parent];
        HeapNode c=heap[child];
        heap[parent]=c;
        heap[child]=p;
    }
    private static enum Status {EMPTY, AVAILABLE, BUSY};
    private static class HeapNode<S> {
        Status tag;
        int score;
        S item;
        int owner;
        Lock lock;
        public void init(S myItem, int myScore) {
            item = myItem;
            score = myScore;
            tag = Status.BUSY;
            owner = ThreadId.get();
        }
        public HeapNode() {
            tag = Status.EMPTY;
            lock = new ReentrantLock();
        }
        public void lock() {
            lock.lock();
        }

        public void unlock() {
            lock.unlock();
        }

        boolean amOwner(){
            return NO_ONE==owner;
        }
    }
}
