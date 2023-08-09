package com.abc.art.list;

public class HashSetTest {

    public static void main(String[] args){
        //handleCoarseHashSet();
        //handleStripedHashSet();
        handleRefinableHashSet();
    }

    public static void handleRefinableHashSet(){
        RefinableHashSet<Integer> coarseHashSet=new RefinableHashSet(2);
        coarseHashSet.add(0);
        coarseHashSet.add(1);
        coarseHashSet.add(2);
        coarseHashSet.add(3);
        coarseHashSet.add(4);
        coarseHashSet.add(5);
        coarseHashSet.add(6);
        coarseHashSet.add(7);
        coarseHashSet.add(8);
        coarseHashSet.add(9);
        coarseHashSet.add(10);
    }

    public static void handleStripedHashSet(){
        StripedHashSet<Integer> coarseHashSet=new StripedHashSet(2);
        coarseHashSet.add(0);
        coarseHashSet.add(1);
        coarseHashSet.add(2);
        coarseHashSet.add(3);
        coarseHashSet.add(4);
        coarseHashSet.add(5);
        coarseHashSet.add(6);
        coarseHashSet.add(7);
        coarseHashSet.add(8);
        coarseHashSet.add(9);
        coarseHashSet.add(10);
    }

    public static void handleCoarseHashSet(){
        CoarseHashSet<Integer> coarseHashSet=new CoarseHashSet(2);
        coarseHashSet.add(0);
        coarseHashSet.add(1);
        coarseHashSet.add(2);
        coarseHashSet.add(3);
        coarseHashSet.add(4);
        coarseHashSet.add(5);
        coarseHashSet.add(6);
        coarseHashSet.add(7);
        coarseHashSet.add(8);
        coarseHashSet.add(9);
        coarseHashSet.add(10);
    }
}
