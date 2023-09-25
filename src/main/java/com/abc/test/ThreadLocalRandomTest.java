package com.abc.test;

import jdk.internal.misc.Unsafe;

import java.lang.reflect.Method;
import java.util.concurrent.ThreadLocalRandom;

public class ThreadLocalRandomTest {
    static ThreadLocalRandom current=ThreadLocalRandom.current();
    public static void main(String[] args){

        //获取线程随机数(线程探针哈希)
//        int seed=current.nextInt(100);
//        print(seed);
//        seed=current.nextInt(100);
//        print(seed);
        loopSeed();
//        testProbe();
    }

//    static void testProbe(){
//        int seed=getProbe();
//        print(seed);
//        seed=getProbe();
//        print(seed);
//
//    }
    static void loopSeed(){
        for(int i=0;i<10;i++){
            localeSeed();
        }
    }
    static void localeSeed()  {
        Thread thread=new Thread(
                ()->{
                    int seed=current.nextInt(100);
                    print(seed);

                    seed=current.nextInt(100);
                    print(seed);
                }
        );
        thread.start();

    }

    static void test4(){
        try {
            ThreadLocalRandom a = ThreadLocalRandom.current();

            Class<ThreadLocalRandom> clazz = ThreadLocalRandom.class;
            java.lang.reflect.Method getMethod = null;
            Method advanceMethod = null;
            try {
                getMethod = clazz.getMethod("getProbe");
                advanceMethod = clazz.getMethod("advanceProbe", int.class);
                getMethod.setAccessible(true);
                advanceMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            int h;
            System.out.println(h = (Integer) getMethod.invoke(a));
            System.out.println(getMethod.invoke(a));
            System.out.println(getMethod.invoke(a));

            System.out.println(h = (Integer) advanceMethod.invoke(a, h));
            System.out.println(getMethod.invoke(a));
            System.out.println(getMethod.invoke(a));

            System.out.println(h = (Integer) advanceMethod.invoke(a, h));
            System.out.println(getMethod.invoke(a));
            System.out.println(getMethod.invoke(a));
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    static void print(int seed){
        System.out.println(Thread.currentThread().getName()+"\t"+
                "seed:->"+seed);

    }

//    static final int getProbe() {
//        return U.getInt(Thread.currentThread(), PROBE);
//    }
//
//    ///////////////////////////////////////
//    private static final Unsafe U = Unsafe.getUnsafe();
//    private static final long SEED
//            = U.objectFieldOffset(Thread.class, "threadLocalRandomSeed");
//    private static final long PROBE
//            = U.objectFieldOffset(Thread.class, "threadLocalRandomProbe");
}
