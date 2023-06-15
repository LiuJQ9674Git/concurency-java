package com.abc.concurrency;

/**
 * Holder
 * <p/>
 * Class at risk of failure if not properly published
 *
 * @author Brian Goetz and Tim Peierls
 */
public class Holder {
    private int n;
    //private final int n;
    //private volatile int n
    //public static Holder holder=new Holder(42);
    private Holder(int n) {
        this.n = n;
    }

    public void assertSanity() {
        if (n != n)
            throw new AssertionError("This statement is false.");
    }
}

