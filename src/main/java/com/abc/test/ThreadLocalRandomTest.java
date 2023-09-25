package java.utill.concurrent;

import java.util.concurrent.ThreadLocalRandom;

public class ThreadLocalRandomTest {
    public static void main(String[] args){
        ThreadLocalRandom current=ThreadLocalRandom.current();
        //获取线程随机数(线程探针哈希)
        int seed=current.nextInt();
        System.out.println("seed:->"+seed);
        seed=current.nextInt();
        System.out.println("seed:->"+seed);
    }
}
