package com.abc.art.counting.network.diffracting;

public class DiffractingTree {
    DiffractingBalancer root;
    DiffractingTree[] child;
    int size;

    public DiffractingTree(int mySize) {
        size = mySize;
        root = new DiffractingBalancer(size);
        if (size > 2) {
            child = new DiffractingTree[]{new DiffractingTree(size / 2),
                    new DiffractingTree(size / 2)};
        }
    }

    public int traverse() {
        int half = root.traverse();
        if (size > 2) {
            return (2 * (child[half].traverse()) + half);
        } else {
            return half;
        }
    }
}
