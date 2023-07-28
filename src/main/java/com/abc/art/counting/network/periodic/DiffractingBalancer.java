package com.abc.art.counting.network.periodic;

import com.abc.art.counting.network.bitonic.Balancer;

import java.util.concurrent.TimeoutException;

public class DiffractingBalancer {
    Prism prism;
    Balancer toggle;

    public DiffractingBalancer(int capacity) {
        prism = new Prism(capacity);
        toggle = new Balancer();
    }

    public int traverse() {
        boolean direction = false;
        try {
            if (prism.visit()) {
                return 0;
            } else {
                return 1;
            }
        } catch (TimeoutException ex) {
            return toggle.traverse();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
