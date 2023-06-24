package com.abc.art.mutalexcute;

import com.abc.annotations.ErrorCriticalSection;

@ErrorCriticalSection
public class LockInt  implements Lock{
    private volatile int victim;
    @Override
    public void lock() {
        int i=ThreadId.get();
        victim=i;
        while (victim==i){}
    }

    @Override
    public void unlock() {

    }
}
