package com.abc.art.mutalexcute;

import java.util.concurrent.ThreadLocalRandom;

public class Backoff {
    final int minDelay, maxDelay;
    int limit;

    public Backoff(int min, int max) {
        minDelay = min;
        maxDelay = max;
        limit = minDelay;
    }

    public void backoff() throws InterruptedException {
        int delay = ThreadLocalRandom.current().nextInt(limit);
        limit = Math.min(maxDelay, 2 * limit);
        Thread.sleep(delay);
    }
}
