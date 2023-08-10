package com.abc.art.skiplist;

public class SkipListTest {

    public static void main(String[] args){
        //handleSkipList();
        handleLockFreeSkipList();
    }

    public static void handleLockFreeSkipList(){
        LockFreeSkipList lockFreeSkipList=new LockFreeSkipList();
        lockFreeSkipList.add(1);
        lockFreeSkipList.add(2);
        lockFreeSkipList.add(3);
        lockFreeSkipList.add(4);
        lockFreeSkipList.add(5);
        lockFreeSkipList.add(6);
        //boolean b= lockFreeSkipList.contains(2);
        lockFreeSkipList.remove(1);
        System.out.println("");
    }
    public static void handleSkipList(){
        LazySkipList lazySkipList=new LazySkipList();
        lazySkipList.add(1);
        lazySkipList.add(2);
        lazySkipList.add(3);
        lazySkipList.add(4);
        lazySkipList.add(5);
        lazySkipList.add(6);
        boolean b= lazySkipList.contains(2);
        //lazySkipList.remove(1);
        System.out.println(b);
    }
}
