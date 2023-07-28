package com.abc.art.counting.network.periodic;

public class Periodic {
    Block[] block;
    Periodic(int width){
        int logSize = 0;
        int myWidth = width;
        while (myWidth > 1) {
            logSize++;
            myWidth = myWidth / 2;
        }
        block = new Block[logSize];
        for (int i = 0; i < logSize; i++) {
            block[i] = new Block(width);
        }
    }
    public int traverse(int input) {
        int wire = input;
        for (Block b : block) {
            wire = b.traverse(wire);
        }
        return wire;
    }
}
