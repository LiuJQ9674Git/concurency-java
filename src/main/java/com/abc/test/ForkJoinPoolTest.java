package com.abc.test;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinPoolTest {

    public static void main(String[] args){
        ForkJoinPool pool=new ForkJoinPool();

        Calculate calculate=new Calculate(10,10000000);
        long sum=pool.invoke(calculate);
        System.out.println("snum:\t" + sum);
    }

    static class Calculate extends RecursiveTask<Long>{
        private final long start;
        private final long end;

        static final long THRESHOLD=10000;

        Calculate(long start,long end){
            this.start=start;
            this.end=end;
        }
        @Override
        protected Long compute() {
            if(this.end-this.start<=THRESHOLD){
                long sum=0;
                for(long i=start;i<end;i++){
                    sum +=i;
                }
                return sum;
            }else {
                long middle=(start+end)/2;
                Calculate left=new Calculate(start,middle);
                left.fork();
                Calculate right=new Calculate(middle+1,end);
                right.fork();
                return left.join()+right.join();
            }

        }
    }

}
