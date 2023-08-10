package com.abc.art.skiplist;

import com.abc.art.ErrorDebug;

import java.util.concurrent.atomic.AtomicMarkableReference;

public final class LockFreeSkipList<T> {
    static final int MAX_LEVEL =16;
    static final float SKIPLIST_P=1/2;
    final Node<T> head = new Node<T>(Integer.MIN_VALUE);
    final Node<T> tail = new Node<T>(Integer.MAX_VALUE);
    public LockFreeSkipList() {
        for (int i = 0; i < head.next.length; i++) {
            head.next[i]
                    = new AtomicMarkableReference<LockFreeSkipList.Node<T>>(tail, false);
        }
    }

    boolean add(T x) {
        int topLevel = randomLevel();
        int bottomLevel = 0;
        Node<T>[] preds = (Node<T>[]) new Node[MAX_LEVEL + 1];
        Node<T>[] succs = (Node<T>[]) new Node[MAX_LEVEL + 1];
        while (true) {
            boolean found = find(x, preds, succs);
            if (found) {
                return false;
            } else {
                Node<T> newNode = new Node(x, topLevel);
                for (int level = bottomLevel; level <= topLevel; level++) {
                    Node<T> succ = succs[level];
                    newNode.next[level].set(succ, false);
                }
                Node<T> pred = preds[bottomLevel];
                Node<T> succ = succs[bottomLevel];
                if (!pred.next[bottomLevel].compareAndSet(succ, newNode,
                        false, false)) {
                    continue;
                }
                for (int level = bottomLevel+1; level <= topLevel; level++) {
                    while (true) {
                    pred = preds[level];
                    succ = succs[level];
                    if (pred.next[level].compareAndSet(succ, newNode, false, false))
                        break;
                    find(x, preds, succs);
                    }
                }
                return true;
            }
        }
    }

    public boolean remove(T x) {
        int bottomLevel =0;
        Node<T>[] preds= (Node<T>[]) new Node[MAX_LEVEL + 1];
        Node<T>[] succs= (Node<T>[]) new Node[MAX_LEVEL + 1];
        Node<T> succ;
        while (true) {
            boolean found = find(x, preds, succs);
            if (!found) {
                return false;
            }else {
                Node<T> nodeToRemove = succs[bottomLevel];
                for (int level = nodeToRemove.topLevel;
                     level >= bottomLevel+1; level--) {
                    boolean[] marked = {false};
                    succ = nodeToRemove.next[level].get(marked);
                    while (!marked[0]) {
                        nodeToRemove.next[level].compareAndSet(succ,
                                succ, false, true);
                        succ = nodeToRemove.next[level].get(marked);
                    }
                } //level over
                boolean[] marked = {false};
                succ = nodeToRemove.next[bottomLevel].get(marked);
                while (true) {
                    boolean iMarkedIt = nodeToRemove.next[bottomLevel].compareAndSet(succ, succ,
                            false, true);
                    succ = succs[bottomLevel].next[bottomLevel].get(marked);
                    if (iMarkedIt) {
                        find(x, preds, succs);
                        return true;
                    } else if (marked[0]) {
                        return false;
                    }
                } //iMarkedIt over
            }
        }
    }

    @ErrorDebug
    public boolean contains(T x) {
        int bottomLevel = 0;
        int v = x.hashCode();
        boolean[] marked = {false};
        Node<T> pred = head, curr = null, succ = null;
        for (int level = MAX_LEVEL; level >= bottomLevel; level--) {
            if(curr!=null) {
                curr = curr.next[level].getReference();
            }
            while (true) {
                if(curr!=null) {
                    succ = curr.next[level].get(marked);
                }
                while (marked[0]) { // marked
                    curr = pred.next[level].getReference();
                    succ = curr.next[level].get(marked);
                } // marked over
                if (curr!=null && curr.key < v){
                    pred = curr;
                    curr = succ;
                } else {
                    break;
                }
            }
        }
        return (curr!=null && curr.key == v);
    }

    private boolean find(T x, Node<T>[] preds, Node<T>[] succs) {
        int bottomLevel = 0;
        int key = x.hashCode();
        boolean[] marked = {false};
        boolean snip;
        Node<T> pred = null, curr = null, succ = null;
        retry:
        while (true) {
            pred = head;
            for (int level = MAX_LEVEL; level >= bottomLevel; level--) {
                curr = pred.next[level].getReference();
                while (true) {
                    succ = curr.next[level].get(marked);
                    while (marked[0]) {//marked
                        snip = pred.next[level].compareAndSet(curr,
                                succ, false, false);
                        if (!snip) {
                            continue retry;
                        }
                        curr = pred.next[level].getReference();
                        succ = curr.next[level].get(marked);
                    }
                    if (curr.key < key){
                        pred = curr; curr = succ;
                    } else {
                        break;
                    }
                }
                preds[level] = pred;
                succs[level] = curr;
            } //level over
            return (curr.key == key);
        }
    }

    private int randomLevel(){
        int lvl = 1;
        while (lvl < this.MAX_LEVEL && Math.random() <SKIPLIST_P) {
            lvl++;
        }
        return lvl;
    }

    public static final class Node<T> {
        final T value;
        final int key;
        final AtomicMarkableReference<Node<T>>[] next;
        private int topLevel;
        // constructor for sentinel nodes
        public Node(int ikey) {
            value = null;
            key = ikey;
            next = (AtomicMarkableReference<Node<T>>[])
                    new AtomicMarkableReference[MAX_LEVEL + 1];
            for (int i = 0; i < next.length; i++) {
                next[i] = new AtomicMarkableReference<Node<T>>(null,false);
            }
            topLevel = MAX_LEVEL;
        }
        // constructor for ordinary nodes
        public Node(T x, int height) {
            value = x;
            key = x.hashCode();
            next = (AtomicMarkableReference<Node<T>>[])
                    new AtomicMarkableReference[height + 1];
            for (int i = 0; i < next.length; i++) {
                next[i] = new AtomicMarkableReference<Node<T>>(null,false);
            }
            topLevel = height;
        }
    }
}
