package com.abc.queue;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.locks.LockSupport;

public class SynchronousQueue<E> {
    
    abstract static class Transferer<E> {

        abstract E transfer(E e);
        
    }

    /**
     * The number of nanoseconds for which it is faster to spin
     * rather than to use timed park. A rough estimate suffices.
     */
    static final long SPIN_FOR_TIMEOUT_THRESHOLD = 1023L;

    /** Dual Queue */
    static final class TransferQueue<E> extends Transferer<E> {
        
        /*
         * This extends Scherer-Scott dual queue algorithm, differing,
         * among other ways, by using modes within nodes rather than
         * marked pointers. The algorithm is a little simpler than
         * that for stacks because fulfillers do not need explicit
         * nodes, and matching is done by CAS'ing QNode.item field
         * from non-null to null (for put) or vice versa (for take).
         */

        /** Node class for TransferQueue. */
        static final class QNode implements ForkJoinPool.ManagedBlocker {
            volatile QNode next;          // next node in queue
            volatile Object item;         // CAS'ed to or from null
            volatile Thread waiter;       // to control park/unpark
            final boolean isData;

            QNode(Object item, boolean isData) {
                this.item = item;
                this.isData = isData;
            }

            boolean casNext(QNode cmp, QNode val) {
                return next == cmp &&
                        QNEXT.compareAndSet(this, cmp, val);
            }

            boolean casItem(Object cmp, Object val) {
                return item == cmp &&
                        QITEM.compareAndSet(this, cmp, val);
            }

            /**
             * Returns true if this node is known to be off the queue
             * because its next pointer has been forgotten due to
             * an advanceHead operation.
             */
            boolean isOffList() {
                return next == this;
            }

            void forgetWaiter() {
                QWAITER.setOpaque(this, null);
            }

            boolean isFulfilled() {
                Object x;
                return isData == ((x = item) == null) || x == this;
            }

            public final boolean isReleasable() {
                Object x;
                return isData == ((x = item) == null) || x == this ||
                        Thread.currentThread().isInterrupted();
            }

            public final boolean block() {
                while (!isReleasable()) LockSupport.park();
                return true;
            }

            // VarHandle mechanics
            private static final VarHandle QITEM;
            private static final VarHandle QNEXT;
            private static final VarHandle QWAITER;
            static {
                try {
                    MethodHandles.Lookup l = MethodHandles.lookup();
                    QITEM = l.findVarHandle(QNode.class, "item", Object.class);
                    QNEXT = l.findVarHandle(QNode.class, "next", QNode.class);
                    QWAITER = l.findVarHandle(QNode.class, "waiter", Thread.class);
                } catch (ReflectiveOperationException e) {
                    throw new ExceptionInInitializerError(e);
                }
            }
        }

        /** Head of queue */
        transient volatile QNode head;
        
        /** Tail of queue */
        transient volatile QNode tail;

        TransferQueue() {
            QNode h = new QNode(null, false); // initialize to dummy node.
            head = h;
            tail = h;
        }

        /**
         * Tries to cas nh as new head; if successful, unlink
         * old head's next node to avoid garbage retention.
         */
        void advanceHead(QNode h, QNode nh) {
            if (h == head &&
                    QHEAD.compareAndSet(this, h, nh))
                h.next = h; // forget old next
        }

        /**
         * Tries to cas nt as new tail.
         */
        void advanceTail(QNode t, QNode nt) {
            if (tail == t)
                QTAIL.compareAndSet(this, t, nt);
        }

        /**
         * Puts or takes an item.
         */
        @SuppressWarnings("unchecked")
        E transfer(E e) {

            QNode s = null;                  // constructed/reused as needed
            boolean isData = (e != null);
            for (;;) {
                QNode t = tail, h = head, m, tn;         // m is node to fulfill
                if (t == null || h == null)
                    ;                                    // inconsistent
                else if (h == t || t.isData == isData) { // empty or same-mode
                    if (t != tail)                       // inconsistent
                        ;
                    else if ((tn = t.next) != null)      // lagging tail
                        advanceTail(t, tn);
                    else if (t.casNext(null, (s != null) ? s :
                            (s = new QNode(e, isData)))) {
                        advanceTail(t, s);
                        Thread w = Thread.currentThread();
                        int stat = -1; // same idea as TransferStack
                        Object item;
                        while ((item = s.item) == e) {
                            if ((item = s.item) != e) {
                                break;                   // recheck
                            } else if (stat <= 0) {
                                if (t.next == s) {
                                    if (stat < 0 && t.isFulfilled()) {
                                        stat = 0;        // yield once if first
                                        Thread.yield();
                                    }
                                    else {
                                        stat = 1;
                                        s.waiter = w;
                                    }
                                }
                            } else{
                                LockSupport.setCurrentBlocker(this);
                                try {
                                    ForkJoinPool.managedBlock(s);
                                } catch (InterruptedException cannotHappen) { }
                                LockSupport.setCurrentBlocker(null);
                            }
                        }
                        if (stat == 1)
                            s.forgetWaiter();
                        if (!s.isOffList()) {            // not already unlinked
                            advanceHead(t, s);           // unlink if head
                            if (item != null)            // and forget fields
                                s.item = s;
                        }
                        return (item != null) ? (E)item : e;
                    }

                } else if ((m = h.next) != null && t == tail && h == head) {
                    Thread waiter;
                    Object x = m.item;
                    boolean fulfilled = ((isData == (x == null)) &&
                            x != m && m.casItem(x, e));
                    advanceHead(h, m);                    // (help) dequeue
                    if (fulfilled) {
                        if ((waiter = m.waiter) != null)
                            LockSupport.unpark(waiter);
                        return (x != null) ? (E)x : e;
                    }
                }
            }
        }

        // VarHandle mechanics
        private static final VarHandle QHEAD;
        private static final VarHandle QTAIL;
        static {
            try {
                MethodHandles.Lookup l = MethodHandles.lookup();
                QHEAD = l.findVarHandle(TransferQueue.class, "head",
                        QNode.class);
                QTAIL = l.findVarHandle(TransferQueue.class, "tail",
                        QNode.class);
            } catch (ReflectiveOperationException e) {
                throw new ExceptionInInitializerError(e);
            }
        }
    }

    private transient volatile Transferer<E> transferer;

    public SynchronousQueue() {
        transferer =  new TransferQueue<E>();
    }

    public void put(E e) throws InterruptedException {
        if (e == null) throw new NullPointerException();
        if (transferer.transfer(e) == null) {
            Thread.interrupted();
            throw new InterruptedException();
        }
    }

    public E take() throws InterruptedException {
        E e = transferer.transfer(null);
        if (e != null)
            return e;
        Thread.interrupted();
        throw new InterruptedException();
    }


    static {
        // Reduce the risk of rare disastrous classloading in first call to
        // LockSupport.park: https://bugs.openjdk.java.net/browse/JDK-8074773
        Class<?> ensureLoaded = LockSupport.class;
    }
}

