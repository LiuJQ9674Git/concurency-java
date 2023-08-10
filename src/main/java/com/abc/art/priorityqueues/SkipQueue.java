package com.abc.art.priorityqueues;

public class SkipQueue<T> {
    PrioritySkipList<T> skiplist;
    public SkipQueue(){
        skiplist=new PrioritySkipList<>();
    }
    public boolean add(T item, int score) {
        Node<T> node = (Node<T>)new Node(item, score);
        return skiplist.add(node);
    }
    public T removeMin() {
        Node<T> node = skiplist.findAndMarkMin();
        if (node != null) {
            skiplist.remove(node);
            return node.item;
        }else{
            return null;
        }
    }
}
