package com.abc.art.counting.network.periodic;

import com.abc.art.counting.network.bitonic.Balancer;

public class Layer {
    int width;
    Balancer[] layer;
    public Layer(int width) {
        this.width = width;
        layer = new Balancer[width];
        for (int i = 0; i < width / 2; i++) {
            layer[i] = layer[width - i - 1] = new Balancer();
        }
    }
    public int traverse(int input) {
        int toggle = layer[input].traverse();
        int hi, lo;
        if (input < width / 2) {
            lo = input;
            hi=width-input-1;
        }else {
            lo = width - input - 1;
            hi = input;
        }
        if (toggle == 0) {
            return lo;
        } else {
            return hi;
        }
    }
}
