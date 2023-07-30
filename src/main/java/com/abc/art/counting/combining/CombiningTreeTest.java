package com.abc.art.counting.combining;

import com.abc.art.queue.SynchronousDualQueue;

public class CombiningTreeTest {
    static  int NUM_THREAD=8;
    public static void main(String[] args){
        CombiningTreeTest combiningTreeTest=new CombiningTreeTest();
        //combiningTreeTest.handleSignleCombiningTree();
        combiningTreeTest.handleCombiningTree();
    }

    void handleSignleCombiningTree() {
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

    // ====================================================================

    void handleCombiningTree() {
        final CombiningTree combiningTree=new CombiningTree(4);
        productCombiningTree(combiningTree);
    }
    void productCombiningTree(CombiningTree combiningTree){

        for(int i=0;i<NUM_THREAD;i++){
            Thread thread = new Thread(() -> {
                while (true) {
                    int pos=combiningTree.getAndIncrement();
                    System.out.println(Thread.currentThread().getName()
                            + "\tpos:\t" + pos);
                    currentSleep();
                }
            });
            thread.start();
        }
    }

    // ====================================================================
    void currentSleep(){
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
