package com.abc.art.counting.network.bitonic;

public class Balancer {
    boolean toggle = true;

    public synchronized int traverse() {
        try {
            if (toggle) {
                return 0;
            } else {
                return 1;
            }
        } finally {
            toggle = !toggle;
        }
    }
}
