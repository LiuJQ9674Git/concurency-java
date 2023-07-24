package com.abc.queue;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

public class SynchronousQueueStack<E> {
    abstract static class Transferer<E> {
        /**
         * Performs a put or take.
         *
         * @param e if non-null, the item to be handed to a consumer;
         *          if null, requests that transfer return an item
         *          offered by producer.
         * @param timed if this operation should timeout
         * @param nanos the timeout, in nanoseconds
         * @return if non-null, the item provided or received; if null,
         *         the operation failed due to timeout or interrupt --
         *         the caller can distinguish which of these occurred
         *         by checking Thread.interrupted.
         */
        abstract E transfer(E e, boolean timed, long nanos);
    }

    /**
     * The number of nanoseconds for which it is faster to spin
     * rather than to use timed park. A rough estimate suffices.
     */
    static final long SPIN_FOR_TIMEOUT_THRESHOLD = 1023L;

    /** Dual stack */
    static final class TransferStack<E> extends Transferer<E> {
        /*
         * This extends Scherer-Scott dual stack algorithm, differing,
         * among other ways, by using "covering" nodes rather than
         * bit-marked pointers: Fulfilling operations push on marker
         * nodes (with FULFILLING bit set in mode) to reserve a spot
         * to match a waiting node.
         */

        /* Modes for SNodes, ORed together in node fields */
        /** Node represents an unfulfilled consumer */
        static final int REQUEST    = 0;
        /** Node represents an unfulfilled producer */
        static final int DATA       = 1;
        /** Node is fulfilling another unfulfilled DATA or REQUEST */
        static final int FULFILLING = 2;

        /** Returns true if m has fulfilling bit set. */
        static boolean isFulfilling(int m) { return (m & FULFILLING) != 0; }

        /** Node class for TransferStacks. */
        static final class SNode implements ForkJoinPool.ManagedBlocker {
            volatile SNode next;        // next node in stack
            volatile SNode match;       // the node matched to this
            volatile Thread waiter;     // to control park/unpark
            Object item;                // data; or null for REQUESTs
            int mode;
            // Note: item and mode fields don't need to be volatile
            // since they are always written before, and read after,
            // other volatile/atomic operations.

            SNode(Object item) {
                this.item = item;
            }

            boolean casNext(SNode cmp, SNode val) {
                return cmp == next &&
                        SNEXT.compareAndSet(this, cmp, val);
            }

            /**
             * Tries to match node s to this node, if so, waking up thread.
             * Fulfillers call tryMatch to identify their waiters.
             * Waiters block until they have been matched.
             *
             * @param s the node to match
             * @return true if successfully matched to s
             */
            boolean tryMatch(SNode s) {
                SNode m; Thread w;
                if ((m = match) == null) {
                    if (SMATCH.compareAndSet(this, null, s)) {
                        if ((w = waiter) != null)
                            LockSupport.unpark(w);
                        return true;
                    }
                    else
                        m = match;
                }
                return m == s;
            }

            /**
             * Tries to cancel a wait by matching node to itself.
             */
            boolean tryCancel() {
                return SMATCH.compareAndSet(this, null, this);
            }

            boolean isCancelled() {
                return match == this;
            }

            public final boolean isReleasable() {
                return match != null || Thread.currentThread().isInterrupted();
            }

            public final boolean block() {
                while (!isReleasable()) LockSupport.park();
                return true;
            }

            void forgetWaiter() {
                SWAITER.setOpaque(this, null);
            }

            // VarHandle mechanics
            private static final VarHandle SMATCH;
            private static final VarHandle SNEXT;
            private static final VarHandle SWAITER;
            static {
                try {
                    MethodHandles.Lookup l = MethodHandles.lookup();
                    SMATCH = l.findVarHandle(SNode.class, "match", SNode.class);
                    SNEXT = l.findVarHandle(SNode.class, "next", SNode.class);
                    SWAITER = l.findVarHandle(SNode.class, "waiter", Thread.class);
                } catch (ReflectiveOperationException e) {
                    throw new ExceptionInInitializerError(e);
                }
            }
        }

        /** The head (top) of the stack */
        volatile SNode head;

        boolean casHead(SNode h, SNode nh) {
            return h == head &&
                    SHEAD.compareAndSet(this, h, nh);
        }

        /**
         * Creates or resets fields of a node. Called only from transfer
         * where the node to push on stack is lazily created and
         * reused when possible to help reduce intervals between reads
         * and CASes of head and to avoid surges of garbage when CASes
         * to push nodes fail due to contention.
         */
        static SNode snode(SNode s, Object e, SNode next, int mode) {
            if (s == null) s = new SNode(e);
            s.mode = mode;
            s.next = next;
            return s;
        }

        /**
         * Puts or takes an item.
         */
        @SuppressWarnings("unchecked")
        E transfer(E e, boolean timed, long nanos) {

            SNode s = null; // constructed/reused as needed
            int mode = (e == null) ? REQUEST : DATA;

            for (;;) {
                SNode h = head;
                if (h == null || h.mode == mode) {  // empty or same-mode
                    if (timed && nanos <= 0L) {     // can't wait
                        if (h != null && h.isCancelled())
                            casHead(h, h.next);     // pop cancelled node
                        else
                            return null;
                    } else if (casHead(h, s = snode(s, e, h, mode))) {
                        long deadline = timed ? System.nanoTime() + nanos : 0L;
                        Thread w = Thread.currentThread();
                        int stat = -1; // -1: may yield, +1: park, else 0
                        SNode m;                    // await fulfill or cancel
                        while ((m = s.match) == null) {
                            if ((timed &&
                                    (nanos = deadline - System.nanoTime()) <= 0) ||
                                    w.isInterrupted()) {
                                if (s.tryCancel()) {
                                    clean(s);       // wait cancelled
                                    return null;
                                }
                            } else if ((m = s.match) != null) {
                                break;              // recheck
                            } else if (stat <= 0) {
                                if (stat < 0 && h == null && head == s) {
                                    stat = 0;       // yield once if was empty
                                    Thread.yield();
                                } else {
                                    stat = 1;
                                    s.waiter = w;   // enable signal
                                }
                            } else if (!timed) {
                                LockSupport.setCurrentBlocker(this);
                                try {
                                    ForkJoinPool.managedBlock(s);
                                } catch (InterruptedException cannotHappen) { }
                                LockSupport.setCurrentBlocker(null);
                            } else if (nanos > SPIN_FOR_TIMEOUT_THRESHOLD)
                                LockSupport.parkNanos(this, nanos);
                        }
                        if (stat == 1)
                            s.forgetWaiter();
                        Object result = (mode == REQUEST) ? m.item : s.item;
                        if (h != null && h.next == s)
                            casHead(h, s.next);     // help fulfiller
                        return (E) result;
                    }
                } else if (!isFulfilling(h.mode)) { // try to fulfill
                    if (h.isCancelled())            // already cancelled
                        casHead(h, h.next);         // pop and retry
                    else if (casHead(h, s=snode(s, e, h, FULFILLING|mode))) {
                        for (;;) { // loop until matched or waiters disappear
                            SNode m = s.next;       // m is s's match
                            if (m == null) {        // all waiters are gone
                                casHead(s, null);   // pop fulfill node
                                s = null;           // use new node next time
                                break;              // restart main loop
                            }
                            SNode mn = m.next;
                            if (m.tryMatch(s)) {
                                casHead(s, mn);     // pop both s and m
                                return (E) ((mode == REQUEST) ? m.item : s.item);
                            } else                  // lost match
                                s.casNext(m, mn);   // help unlink
                        }
                    }
                } else {                            // help a fulfiller
                    SNode m = h.next;               // m is h's match
                    if (m == null)                  // waiter is gone
                        casHead(h, null);           // pop fulfilling node
                    else {
                        SNode mn = m.next;
                        if (m.tryMatch(h))          // help match
                            casHead(h, mn);         // pop both h and m
                        else                        // lost match
                            h.casNext(m, mn);       // help unlink
                    }
                }
            }
        }

        /**
         * Unlinks s from the stack.
         */
        void clean(SNode s) {
            s.item = null;   // forget item
            s.forgetWaiter();

            /*
             * At worst we may need to traverse entire stack to unlink
             * s. If there are multiple concurrent calls to clean, we
             * might not see s if another thread has already removed
             * it. But we can stop when we see any node known to
             * follow s. We use s.next unless it too is cancelled, in
             * which case we try the node one past. We don't check any
             * further because we don't want to doubly traverse just to
             * find sentinel.
             */

           SNode past = s.next;
            if (past != null && past.isCancelled())
                past = past.next;

            // Absorb cancelled nodes at head
            SNode p;
            while ((p = head) != null && p != past && p.isCancelled())
                casHead(p, p.next);

            // Unsplice embedded nodes
            while (p != null && p != past) {
                SNode n = p.next;
                if (n != null && n.isCancelled())
                    p.casNext(n, n.next);
                else
                    p = n;
            }
        }

        // VarHandle mechanics
        private static final VarHandle SHEAD;
        static {
            try {
                MethodHandles.Lookup l = MethodHandles.lookup();
                SHEAD = l.findVarHandle(TransferStack.class, "head",SNode.class);
            } catch (ReflectiveOperationException e) {
                throw new ExceptionInInitializerError(e);
            }
        }
    }

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
             * Tries to cancel by CAS'ing ref to this as item.
             */
            boolean tryCancel(Object cmp) {
                return QITEM.compareAndSet(this, cmp, this);
            }

            boolean isCancelled() {
                return item == this;
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
        /**
         * Reference to a cancelled node that might not yet have been
         * unlinked from queue because it was the last inserted node
         * when it was cancelled.
         */
        transient volatile QNode cleanMe;

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
         * Tries to CAS cleanMe slot.
         */
        boolean casCleanMe(QNode cmp, QNode val) {
            return cleanMe == cmp &&
                    QCLEANME.compareAndSet(this, cmp, val);
        }

        /**
         * Puts or takes an item.
         */
        @SuppressWarnings("unchecked")
        E transfer(E e, boolean timed, long nanos) {
            /* Basic algorithm is to loop trying to take either of
             * two actions:
             *
             * 1. If queue apparently empty or holding same-mode nodes,
             *    try to add node to queue of waiters, wait to be
             *    fulfilled (or cancelled) and return matching item.
             *
             * 2. If queue apparently contains waiting items, and this
             *    call is of complementary mode, try to fulfill by CAS'ing
             *    item field of waiting node and dequeuing it, and then
             *    returning matching item.
             *
             * In each case, along the way, check for and try to help
             * advance head and tail on behalf of other stalled/slow
             * threads.
             *
             * The loop starts off with a null check guarding against
             * seeing uninitialized head or tail values. This never
             * happens in current SynchronousQueue, but could if
             * callers held non-volatile/final ref to the
             * transferer. The check is here anyway because it places
             * null checks at top of loop, which is usually faster
             * than having them implicitly interspersed.
             */

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
                    else if (timed && nanos <= 0L)       // can't wait
                        return null;
                    else if (t.casNext(null, (s != null) ? s :
                            (s = new QNode(e, isData)))) {
                        advanceTail(t, s);
                        long deadline = timed ? System.nanoTime() + nanos : 0L;
                        Thread w = Thread.currentThread();
                        int stat = -1; // same idea as TransferStack
                        Object item;
                        while ((item = s.item) == e) {
                            if ((timed &&
                                    (nanos = deadline - System.nanoTime()) <= 0) ||
                                    w.isInterrupted()) {
                                if (s.tryCancel(e)) {
                                    clean(t, s);
                                    return null;
                                }
                            } else if ((item = s.item) != e) {
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
                            } else if (!timed) {
                                LockSupport.setCurrentBlocker(this);
                                try {
                                    ForkJoinPool.managedBlock(s);
                                } catch (InterruptedException cannotHappen) { }
                                LockSupport.setCurrentBlocker(null);
                            }
                            else if (nanos > SPIN_FOR_TIMEOUT_THRESHOLD)
                                LockSupport.parkNanos(this, nanos);
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

        /**
         * Gets rid of cancelled node s with original predecessor pred.
         */
        void clean(QNode pred, QNode s) {
            s.forgetWaiter();
            /*
             * At any given time, exactly one node on list cannot be
             * deleted -- the last inserted node. To accommodate this,
             * if we cannot delete s, we save its predecessor as
             * "cleanMe", deleting the previously saved version
             * first. At least one of node s or the node previously
             * saved can always be deleted, so this always terminates.
             */
            while (pred.next == s) { // Return early if already unlinked
                QNode h = head;
                QNode hn = h.next;   // Absorb cancelled first node as head
                if (hn != null && hn.isCancelled()) {
                    advanceHead(h, hn);
                    continue;
                }
                QNode t = tail;      // Ensure consistent read for tail
                if (t == h)
                    return;
               QNode tn = t.next;
                if (t != tail)
                    continue;
                if (tn != null) {
                    advanceTail(t, tn);
                    continue;
                }
                if (s != t) {        // If not tail, try to unsplice
                    QNode sn = s.next;
                    if (sn == s || pred.casNext(s, sn))
                        return;
                }
               QNode dp = cleanMe;
                if (dp != null) {    // Try unlinking previous cancelled node
                    QNode d = dp.next;
                    QNode dn;
                    if (d == null ||               // d is gone or
                            d == dp ||                 // d is off list or
                            !d.isCancelled() ||        // d not cancelled or
                            (d != t &&                 // d not tail and
                                    (dn = d.next) != null &&  //   has successor
                                    dn != d &&                //   that is on list
                                    dp.casNext(d, dn)))       // d unspliced
                        casCleanMe(dp, null);
                    if (dp == pred)
                        return;      // s is already saved node
                } else if (casCleanMe(null, pred))
                    return;          // Postpone cleaning s
            }
        }

        // VarHandle mechanics
        private static final VarHandle QHEAD;
        private static final VarHandle QTAIL;
        private static final VarHandle QCLEANME;
        static {
            try {
                MethodHandles.Lookup l = MethodHandles.lookup();
                QHEAD = l.findVarHandle(TransferQueue.class, "head",
                        QNode.class);
                QTAIL = l.findVarHandle(TransferQueue.class, "tail",
                        QNode.class);
                QCLEANME = l.findVarHandle(TransferQueue.class, "cleanMe",
                        QNode.class);
            } catch (ReflectiveOperationException e) {
                throw new ExceptionInInitializerError(e);
            }
        }
    }

    /**
     * The transferer. Set only in constructor, but cannot be declared
     * as final without further complicating serialization.  Since
     * this is accessed only at most once per public method, there
     * isn't a noticeable performance penalty for using volatile
     * instead of final here.
     */
    private transient volatile Transferer<E> transferer;



    public SynchronousQueueStack() {
        transferer =  new TransferStack<E>();
    }

    /**
     * Adds the specified element to this queue, waiting if necessary for
     * another thread to receive it.
     *
     * @throws InterruptedException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    public void put(E e) throws InterruptedException {
        if (e == null) throw new NullPointerException();
        if (transferer.transfer(e, false, 0) == null) {
            Thread.interrupted();
            throw new InterruptedException();
        }
    }

    /**
     * Inserts the specified element into this queue, waiting if necessary
     * up to the specified wait time for another thread to receive it.
     *
     * @return {@code true} if successful, or {@code false} if the
     *         specified waiting time elapses before a consumer appears
     * @throws InterruptedException {@inheritDoc}
     * @throws NullPointerException {@inheritDoc}
     */
    public boolean offer(E e, long timeout, TimeUnit unit)
            throws InterruptedException {
        if (e == null) throw new NullPointerException();
        if (transferer.transfer(e, true, unit.toNanos(timeout)) != null)
            return true;
        if (!Thread.interrupted())
            return false;
        throw new InterruptedException();
    }

    /**
     * Inserts the specified element into this queue, if another thread is
     * waiting to receive it.
     *
     * @param e the element to add
     * @return {@code true} if the element was added to this queue, else
     *         {@code false}
     * @throws NullPointerException if the specified element is null
     */
    public boolean offer(E e) {
        if (e == null) throw new NullPointerException();
        return transferer.transfer(e, true, 0) != null;
    }

    /**
     * Retrieves and removes the head of this queue, waiting if necessary
     * for another thread to insert it.
     *
     * @return the head of this queue
     * @throws InterruptedException {@inheritDoc}
     */
    public E take() throws InterruptedException {
        E e = transferer.transfer(null, false, 0);
        if (e != null)
            return e;
        Thread.interrupted();
        throw new InterruptedException();
    }

    /**
     * Retrieves and removes the head of this queue, waiting
     * if necessary up to the specified wait time, for another thread
     * to insert it.
     *
     * @return the head of this queue, or {@code null} if the
     *         specified waiting time elapses before an element is present
     * @throws InterruptedException {@inheritDoc}
     */
    public E poll(long timeout, TimeUnit unit) throws InterruptedException {
        E e = transferer.transfer(null, true, unit.toNanos(timeout));
        if (e != null || !Thread.interrupted())
            return e;
        throw new InterruptedException();
    }

    /**
     * Retrieves and removes the head of this queue, if another thread
     * is currently making an element available.
     *
     * @return the head of this queue, or {@code null} if no
     *         element is available
     */
    public E poll() {
        return transferer.transfer(null, true, 0);
    }


    /**
     * Always returns {@code "[]"}.
     * @return {@code "[]"}
     */
    public String toString() {
        return "[]";
    }

    /**
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @throws IllegalArgumentException      {@inheritDoc}
     */
    public int drainTo(Collection<? super E> c) {
        Objects.requireNonNull(c);
        if (c == this)
            throw new IllegalArgumentException();
        int n = 0;
        for (E e; (e = poll()) != null; n++)
            c.add(e);
        return n;
    }

    /**
     * @throws UnsupportedOperationException {@inheritDoc}
     * @throws ClassCastException            {@inheritDoc}
     * @throws NullPointerException          {@inheritDoc}
     * @throws IllegalArgumentException      {@inheritDoc}
     */
    public int drainTo(Collection<? super E> c, int maxElements) {
        Objects.requireNonNull(c);
        if (c == this)
            throw new IllegalArgumentException();
        int n = 0;
        for (E e; n < maxElements && (e = poll()) != null; n++)
            c.add(e);
        return n;
    }



    static {
        // Reduce the risk of rare disastrous classloading in first call to
        // LockSupport.park: https://bugs.openjdk.java.net/browse/JDK-8074773
        Class<?> ensureLoaded = LockSupport.class;
    }
}
