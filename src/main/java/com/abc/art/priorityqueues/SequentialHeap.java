package com.abc.art.priorityqueues;

public class SequentialHeap<T> implements PQueue<T> {
    private static final int ROOT = 1;
    int next;
    HeapNode<T>[] heap;
    public SequentialHeap(int capacity) {
        next = ROOT;
        heap = (HeapNode<T>[]) new HeapNode[capacity + 1];
        for (int i = 0; i < capacity + 1; i++) {
            heap[i] = new HeapNode<T>();
        }
    }
    @Override
    public void add(T item, int score) {
        int child = next++;
        heap[child].init(item, score);
        while (child > ROOT) {
            int parent = child / 2;
            //int oldChild = child;
            if (heap[child].score < heap[parent].score) {
                swap(child, parent);
                child = parent;
            } else {
                return;
            }
        }
    }

    @Override
    public T removeMin() {
        int bottom = --next;
        T item = heap[ROOT].item;
        heap[ROOT] = heap[bottom];
        if (bottom == ROOT) {
            return item;
        }
        int child = 0;
        int parent = ROOT;
        while (parent < heap.length / 2) {
            int left = parent * 2;
            int right = (parent * 2) + 1;
            if (left >= next) {
                return item;
            } else if (right >= next ||
                    heap[left].score < heap[right].score) {
                child = left;
            } else {
                child = right;
            }
            if (heap[child].score < heap[parent].score) {
                swap(parent, child);
                parent = child;
            } else {
                return item;
            }
        }
        return item;
    }

    private void swap(int parent,int child){
        HeapNode p=heap[parent];
        HeapNode c=heap[child];
        heap[parent]=c;
        heap[child]=p;
    }
    private static class HeapNode<S> {
        int score;
        S item;
        void init(S x, int i){
            item=x;
            score=i;
        }
    }
}
