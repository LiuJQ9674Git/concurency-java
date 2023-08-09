package com.abc.art.sets;

import java.util.concurrent.atomic.AtomicInteger;

public class LockFreeHashSet<T> {
    static int THRESHOLD = 126;
    protected BucketList<T>[] bucket;
    protected AtomicInteger bucketSize;
    protected AtomicInteger setSize;

    public LockFreeHashSet(int capacity) {
        bucket = (BucketList<T>[]) new BucketList[capacity];
        bucket[0] = new BucketList<T>();
        bucketSize = new AtomicInteger(2);
        setSize = new AtomicInteger(0);
    }

    public boolean add(T x) {
        int myBucket = x.hashCode() % bucketSize.get();
        //int myBucket = BucketList.hashCode(x) % bucketSize.get();
        BucketList<T> b = getBucketList(myBucket);
        if (!b.add(x)) {
            return false;
        }
        int setSizeNow = setSize.getAndIncrement();
        int bucketSizeNow = bucketSize.get();
        if (setSizeNow / bucketSizeNow > THRESHOLD) {
            bucketSize.compareAndSet(bucketSizeNow,
                    2 * bucketSizeNow);
        }
        return true;
    }

    BucketList<T> getBucketList(int myBucket) {
        if (bucket[myBucket] == null) {
            initializeBucket(myBucket);
        }
        return bucket[myBucket];
    }

    private void initializeBucket(int myBucket) {
        int parent = getParent(myBucket);
        if (bucket[parent] == null) {
            initializeBucket(parent);
        }
        BucketList<T> b = bucket[parent].getSentinel(myBucket);
        if (b != null) {
            bucket[myBucket] = b;
        }
    }

    private int getParent(int myBucket) {
        int parent = bucketSize.get();
        do {
            parent = parent >> 1;
        } while (parent > myBucket);
        parent = myBucket - parent;
        return parent;
    }
}
