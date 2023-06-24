package com.abc.art.mutalexcute;

public class ThreadId {
    private static volatile int nextId=0;
    private static class ThreadLocalId extends ThreadLocal{
        protected synchronized Integer initialValue(){
            return nextId++;
        }
    }

    private static ThreadLocalId threadLocalId=new ThreadLocalId();

    public static int get(){
        return (int) threadLocalId.get();
    }

    public static void set(int index){
        threadLocalId.set(index);
    }
}

