package com.abc.test;

public class Test {
    public static void main(String[] args) {
        Object tail = new Object();
        Object t = new Object();
        boolean eqs = t != (t = tail);//t赋予新值
        System.out.println(eqs);
    }

}