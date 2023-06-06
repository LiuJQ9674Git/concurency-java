package com.abc.concurrency.guardedby;

import com.abc.annotations.GuardedBy;
import com.abc.annotations.ThreadSafe;
//import net.jcip.annotations.*;

/**
 * Sequence
 *
 * @author Brian Goetz and Tim Peierls
 */

@ThreadSafe
public class Sequence {
    @GuardedBy("this") private int nextValue;

    public synchronized int getNext() {
        return nextValue++;
    }
}
