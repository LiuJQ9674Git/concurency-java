package com.abc.art.queue;

import com.abc.art.ErrorDebug;

import java.util.concurrent.atomic.AtomicReference;

@ErrorDebug
public class SynchronousDualQueue<T> {
    AtomicReference<Node> head;
    AtomicReference<Node> tail;
    public SynchronousDualQueue() {
        Node sentinel = new Node(null, NodeType.ITEM);
        head = new AtomicReference<Node>(sentinel);
        tail = new AtomicReference<Node>(sentinel);
    }

    @ErrorDebug
    public T deq(){
        Node offer = new Node(null, NodeType.RESERVATION);
        while (true) {
            Node t = tail.get(), h = head.get();
            if (h == t || t.type == NodeType.RESERVATION) {
                Node n = h.next.get();
                if (t == tail.get()) {
                    if (n != null) {
                        tail.compareAndSet(t, n);
                        //return n.item.get();
                    } else if (t.next.compareAndSet(n, offer)) {
                        tail.compareAndSet(t, offer);
                        h = head.get();
                        if (offer == h.next.get()){
                            head.compareAndSet(h, offer);
                        }
                        return h.item.get();
                    }
                }
            } else {
                Node n = h.next.get();
//                if (t != tail.get() || h != head.get() || n == null) {
//                    continue;
//                }
                boolean success = n.item.compareAndSet(null, null);
                head.compareAndSet(h, n);
                if (success) {
                    return n.item.get();
                }
            }
        }
    }
    public void enq(T e) {
        Node offer = new Node(e, NodeType.ITEM);
        while (true) {
            Node t = tail.get(), h = head.get();
            if (h == t || t.type == NodeType.ITEM) {
                Node n = t.next.get();
                if (t == tail.get()) {
                    if (n != null) {
                        tail.compareAndSet(t, n);
                    } else if (t.next.compareAndSet(n, offer)) {
                        tail.compareAndSet(t, offer);
                        //Node noffer=tail.get();
                        while (((T)(offer=tail.get()).item.get()) == e);

                        h = head.get();
                        if (offer == h.next.get()){
                            head.compareAndSet(h, offer);
                        }
                        return;
                    }
                }
            } else {
                Node n = h.next.get();
                if (t != tail.get() || h != head.get() || n == null) {
                    continue;
                }
                boolean success = n.item.compareAndSet(null, e);
                head.compareAndSet(h, n);
                if (success) {
                    return;
                }
            }
        }
    }
    private enum NodeType {ITEM, RESERVATION};
    private class Node {
        volatile NodeType type;
        volatile AtomicReference<T> item;
        volatile AtomicReference<Node> next;
        Node(T myItem, NodeType myType) {
            item = new AtomicReference<T>(myItem);
            next = new AtomicReference<Node>(null);
            type = myType;
        }
    }
    // ===================================================
}
