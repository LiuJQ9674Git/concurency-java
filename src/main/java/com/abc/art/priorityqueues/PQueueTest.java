package com.abc.art.priorityqueues;

public class PQueueTest {

    public static void main(String[] args){
        //handleSimpleLinear();
        handleSimpleTree();
    }

    static void handleSimpleTree(){
        SimpleTree simpleTree=new SimpleTree(2);
        simpleTree.add(1,0);
        simpleTree.add(2,0);
        simpleTree.add(3,0);
        simpleTree.add(4,0);
        simpleTree.add(5,1);
        simpleTree.add(6,1);
        Object o=simpleTree.removeMin();
        System.out.println("object:\t"+o);
    }
    static void handleSimpleLinear(){
        SimpleLinear simpleLinear=new SimpleLinear(4);
        simpleLinear.add(1,1);
        simpleLinear.add(2,1);
        simpleLinear.add(3,1);
        simpleLinear.add(4,2);
        simpleLinear.add(5,2);
        Object o=simpleLinear.removeMin();
        System.out.println("object:\t"+o);
    }
}
