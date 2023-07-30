package com.abc.art.sets;

import com.abc.art.ErrorDebug;
import com.abc.art.stack.Node;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class LockFreeList<T> implements Set<T> {
    private final Node head;

    LockFreeList() {
        head = new Node(Integer.MIN_VALUE);
        Node next = new Node(Integer.MAX_VALUE);
        head.next = new AtomicMarkableReference(next,false);
    }

    public boolean add(T item) {
        int key = item.hashCode();
        while (true) {
            Window window = find(head, key);
            Node pred = window.pred, curr = window.curr;
            if (curr.key == key) {
                return false;
            } else {
                Node node = new Node(item);
                node.next = new AtomicMarkableReference(curr, false);
                if (pred.next.compareAndSet(curr, node, false, false)) {
                    return true;
                }
            }
        }
    }

    public boolean remove(T item) {
        int key = item.hashCode();
        boolean snip;
        while (true) {
            Window window = find(head, key);
            Node pred = window.pred, curr = window.curr;
            if (curr.key != key) {
                return false;
            } else {
                Node succ = (Node) curr.next.getReference();
                snip = curr.next.compareAndSet(succ, succ, false, true);
                if (!snip) {
                    continue;
                }
                pred.next.compareAndSet(curr, succ, false, false);
                return true;
            }
        }
    }

    public boolean contains(T item) {
        int key = item.hashCode();
        Node curr = head;
        while (curr.key < key) {
            curr = (Node) curr.next.getReference();
        }
        return (curr.key == key && !curr.next.isMarked());
    }

    Window find(Node head, int key) {
        Node pred = null, curr = null, succ = null;
        boolean[] marked = {false};
        boolean snip;
        retry:
        while (true) {
            pred = head;
            curr = (Node) pred.next.getReference();
//            if(curr==null){
//                continue retry;
//            }
            while (true) {
                succ = (Node) curr.next.get(marked);
                while (marked[0]) {
                    snip = pred.next.compareAndSet(curr, succ,
                            false, false);
                    if (!snip) {
                        continue retry;
                    }
                    curr = succ;
                    succ = (Node) curr.next.get(marked);
                }
                if (curr.key >= key)
                    return new Window(pred, curr);
                pred = curr;
                curr = succ;
            }
        }
    }

    class Window {
        public Node pred, curr;

        Window(Node myPred, Node myCurr) {
            pred = myPred;
            curr = myCurr;
        }
    }

    class Node<T> {
        T item;
        int key;
        //Node next;
        AtomicMarkableReference<Node> next = new
                AtomicMarkableReference<Node>(null, false);

        Node(int key) {
            this.key = key;
        }

        Node(T item) {
            this.item = item;
            this.key = item.hashCode();
        }
    }
}
