package com.abc.concurrency.publishing;

import com.abc.annotations.ThreadSafe;

/**
 * EagerInitialization
 * <p/>
 * Eager initialization
 *
 * @author Brian Goetz and Tim Peierls
 */
@ThreadSafe
public class EagerInitialization {
    private static Resource resource = new Resource();

    public static Resource getResource() {
        return resource;
    }

    static class Resource {
    }
}
