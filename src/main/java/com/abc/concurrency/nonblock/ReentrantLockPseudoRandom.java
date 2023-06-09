package com.abc.concurrency.nonblock;

import java.util.concurrent.locks.*;

import com.abc.annotations.ThreadSafe;
import com.abc.concurrency.PseudoRandom;
//import net.jcip.annotations.*;

/**
 * ReentrantLockPseudoRandom
 * <p/>
 * Random number generator using ReentrantLock
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class ReentrantLockPseudoRandom extends PseudoRandom {
    private final Lock lock = new ReentrantLock(false);
    private int seed;

    ReentrantLockPseudoRandom(int seed) {
        this.seed = seed;
    }

    public int nextInt(int n) {
        lock.lock();
        try {
            int s = seed;
            seed = calculateNext(s);
            int remainder = s % n;
            return remainder > 0 ? remainder : remainder + n;
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args){
        ReentrantLockPseudoRandom reentrantLockPseudoRandom=new ReentrantLockPseudoRandom(2);
        reentrantLockPseudoRandom.nextInt(1);
    }
}
