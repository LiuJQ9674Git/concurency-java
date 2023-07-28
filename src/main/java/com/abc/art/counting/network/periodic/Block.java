package com.abc.art.counting.network.periodic;

public class Block {
    Block north;
    Block south;
    Layer layer;
    int width;

    public Block(int width) {
        this.width = width;
        if (width > 2) {
            north = new Block(width / 2);
            south = new Block(width / 2);
        }
        layer = new Layer(width);
    }

    public int traverse(int input) {
        int wire = layer.traverse(input);
        if (width > 2) {
            if (wire < width / 2) {
                return north.traverse(wire);
            } else {
                return (width / 2) + south.traverse(wire - (width / 2));
            }
        } else {
            return wire;
        }
    }
}
