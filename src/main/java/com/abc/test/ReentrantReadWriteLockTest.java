package com.abc.test;

public class ReentrantReadWriteLockTest {

    static final int SHARED_SHIFT   = 16;
    static final int SHARED_UNIT    = (1 << SHARED_SHIFT);
    static final int MAX_COUNT      = (1 << SHARED_SHIFT) - 1;
    static final int EXCLUSIVE_MASK = (1 << SHARED_SHIFT) - 1;

    static int sharedCount(int c)    { return c >>> SHARED_SHIFT; }
    static int exclusiveCount(int c) { return c & EXCLUSIVE_MASK; }

    static void printValue(){
        System.out.println("  SHARED_UNIT:\t"+Integer.toBinaryString(SHARED_UNIT));
        System.out.println("    MAX_COUNT:\t"+Integer.toBinaryString(MAX_COUNT));
        System.out.println("EXCLUSIVE_MASK:\t"+Integer.toBinaryString(EXCLUSIVE_MASK));

    }

    public static void main(String[] args){
        printValue();
        int a=65538;
        int cont=exclusiveCount(a);
        System.out.println();

        System.out.println(Integer.toBinaryString(a));//Integer.MAX_VALUE);
        System.out.println(Integer.toBinaryString(cont));
        System.out.println(a);
        System.out.println(cont);
        //handleLock();
        //handleTask();
    }
}
