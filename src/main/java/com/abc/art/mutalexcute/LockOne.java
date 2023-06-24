package com.abc.art.mutalexcute;

import com.abc.annotations.ErrorCriticalSection;

import java.util.concurrent.atomic.AtomicInteger;

@ErrorCriticalSection
public class LockOne implements Lock{
    //final AtomicInteger atomicInteger=new AtomicInteger();
    boolean[] flag = new boolean[2];
    public void lock() {
        //int p = (int)Thread.currentThread().getId();
        int i=ThreadId.get();
        int j = (1 - i);
        flag[i] = true;
        while (flag[j]) {
            //System.err.println("unlock\t"+j);
        }
    }

    public void unlock() {
        int i = ThreadId.get();
        flag[i] = false;
        System.err.println("unlock\t"+i);
    }

}
