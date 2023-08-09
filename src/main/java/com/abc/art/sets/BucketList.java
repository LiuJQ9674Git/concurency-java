package com.abc.art.sets;

import java.util.concurrent.atomic.AtomicMarkableReference;

public class BucketList<T> implements Set<T> {
    static final int HI_MASK = 0x80000000;
    static final int MASK = 0x00FFFFFF;
    Node head;

    public BucketList(Node head) {
        this.head = head;
//        head.next =
//                new AtomicMarkableReference<Node>(
//                        new Node(Integer.MAX_VALUE),
//                        false);

    }

    public BucketList() {
        head = new Node(Integer.MIN_VALUE);
        ;
        head.next =
                new AtomicMarkableReference<Node>(
                        new Node(Integer.MAX_VALUE),
                        false);
    }

    @Override
    public boolean add(T item) {
        //int rawkey = item.hashCode();
        int key = makeOrdinaryKey(item);
        while (true) {
            Window window = find(head, key);
            Node pred = window.pred, curr = window.curr;
            if (curr.key == key) {
                return false;
            } else {
                Node node = new Node(item);
                node.key = key;
                node.next = new AtomicMarkableReference(curr, false);
                if (pred.next.compareAndSet(curr, node, false, false)) {
                    return true;
                }
            }
        }
    }

    @Override
    public boolean remove(T x) {
        //int key = x.hashCode();
        int key = makeOrdinaryKey(x);
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


    public boolean contains(T x) {
        int key = makeOrdinaryKey(x);
        Window window = find(head, key);
        Node curr = window.curr;
        return (curr.key == key);
    }


    public int makeOrdinaryKey(T x) {
        int code = x.hashCode() & MASK; // take 3 lowest bytes
        return reverse(code | HI_MASK);
    }

    private static int makeSentinelKey(int key) {
        return reverse(key & MASK);
    }

    static int hashCode(Object x) {
        int code = x.hashCode() & MASK; // take 3 lowest bytes
        return reverse(code | HI_MASK);
    }

    public BucketList<T> getSentinel(int index) {
        int key = makeSentinelKey(index);
        boolean splice;
        while (true) {
            Window window = find(head, key);
            Node pred = window.pred;
            Node curr = window.curr;
            if (curr.key == key) {
                return new BucketList<T>(curr);
            } else {
                Node node = new Node(key);
                node.next.set(pred.next.getReference(), false);
                splice = pred.next.compareAndSet(curr, node, false, false);
                if (splice) {
                    return new BucketList<T>(node);
                } else {
                    continue;
                }
            }
        }
    }

    Window find(Node head, int key) {
        Node pred = null, curr = null, succ = null;
        boolean[] marked = {false};
        boolean snip;
        retry:
        while (true) {
            pred = head;
            curr = (Node) pred.next.getReference();
            if (curr == null) {
                continue retry;
            }
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

    private static int reverse(int code) {
        return Integer.reverse(code);
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
        AtomicMarkableReference<LockFreeList.Node> next = new
                AtomicMarkableReference<LockFreeList.Node>(null, false);

        Node(int key) {
            this.key = key;
        }

        Node(T item) {
            this.item = item;
            this.key = item.hashCode();
        }
    }
}
