package com.abc.test;

public class PaddingTest {
    public static void main(String[] args){
        long[] arr = new long[64 * 1024 * 1024];
        long start = System.nanoTime();
        for (int i = 0; i < arr.length; i++) {
            arr[i] *= 3;
        }
        System.out.println(System.nanoTime() - start);
        long start2 = System.nanoTime();

        //long[] arr2 = new long[64 * 1024 * 1024];
        for (int i = 0; i < arr.length; i += 1) {
            arr[i] *= 3;
        }
        System.out.println(System.nanoTime() - start2);


    }
}
