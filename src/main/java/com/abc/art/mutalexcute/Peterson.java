package com.abc.art.mutalexcute;

public class Peterson  implements Lock{
    boolean[] flag = new boolean[2];
    private volatile int victim;
    @Override
    public void lock() {
        int i=ThreadId.get();
        int j = (1 - i);
        flag[i] = true;
        while (flag[j]&&victim==i) {

        }
    }

    @Override
    public void unlock() {
        int i = ThreadId.get();
        flag[i] = false;
    }
}
