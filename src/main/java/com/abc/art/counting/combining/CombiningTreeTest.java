package com.abc.art.counting.combining;

public class CombiningTreeTest {

    public static void main(String[] args){
        CombiningTree combiningTree=new CombiningTree(4);
        int pos=combiningTree.getAndIncrement();
        System.out.println("combiningTree\tpos:\t"+pos);
        pos=combiningTree.getAndIncrement();
        System.out.println("combiningTree\tpos:\t"+pos);
        pos=combiningTree.getAndIncrement();
        System.out.println("combiningTree\tpos:\t"+pos);
        pos=combiningTree.getAndIncrement();
        System.out.println("combiningTree\tpos:\t"+pos);

    }
}
