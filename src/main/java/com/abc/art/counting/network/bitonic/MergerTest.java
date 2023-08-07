package com.abc.art.counting.network.bitonic;

import com.abc.art.counting.network.Balancer;
import com.abc.art.counting.network.BalancerTest;

import java.awt.image.LookupOp;
import java.util.concurrent.atomic.AtomicInteger;

public class MergerTest {

    // Bitonic数量应当小于Token数
    int TK_NUM_1=4;
    int TK_NUM_2=4;
    int TK_NUM_3=4;
    int TK_NUM_4=6;
    static int THREAD_NUM=4; //4
    static int WITH_NUM=8;   //8
    static int LOOP=16; //4
    final Merger balancer=new Merger(WITH_NUM);
    static AtomicInteger stop=new AtomicInteger(1);
    static AtomicInteger posTh=new AtomicInteger(1);
    static AtomicInteger tokenInteger=new AtomicInteger(1);
    public static void main(String[] args){
        MergerTest balancerTest=new MergerTest();
        //balancerTest.doMerger();
        balancerTest.doMergerAlg();

    }

    public  void doMergerAlg(){
        //output = half[input % 2].traverse(input / 2);
        int input=5;
        int mod=input % 2; //1
        int div=input / 2; //2
        //5 1 2
        System.out.println("mod\t"+mod+"\tdiv\t"+div);


    }

    public  void doMerger(){
        MergerTest balancerTest=new MergerTest();
        //balancerTest.doToken1();
        //balancerTest.doToken2();

        while (stop.getAndIncrement()<THREAD_NUM) {//
            balancerTest.doToken(tokenInteger.getAndIncrement());
//            while (tokenInteger.getAndIncrement()<TOKEN_NUM) {
//                balancerTest.doToken(tokenInteger.get());
//            }
        }
    }
    void doToken(final int token){
        Thread thread=new Thread(()->{
            for(int ii = 0; ii< LOOP; ii++) {
                int pos = balancer.traverse(token);
                System.out.println(
                        "token:\t" + token + "\t"+
                                Thread.currentThread().getName()
                                +"\tpos:\t" + pos+"\t");
            }
        }
        );
        thread.start();
    }

    void doToken1(){
        Thread thread=new Thread(()->{
            for(int ii=0;ii<TK_NUM_1;ii++) {
                int pos = balancer.traverse(1);
                System.out.println(
                        "ith:\t" + posTh.getAndIncrement() + "\t"+
                                Thread.currentThread().getName()
                                +"\tpos:\t" + pos+"\t");
            }
        }
        );
        thread.start();
    }

    void doToken2(){
        Thread thread=new Thread(()->{
            for(int ii=0;ii<TK_NUM_2;ii++) {
                int pos = balancer.traverse(2);
                System.out.println(
                        "ith:\t" + posTh.getAndIncrement() + "\t"+
                                Thread.currentThread().getName()
                                +"\tpos:\t" + pos+"\t");
            }
        }
        );
        thread.start();
    }
    void doToken3(){
        Thread thread=new Thread(()->{
            for(int ii=0;ii<TK_NUM_3;ii++) {
                int pos = balancer.traverse(3);
                System.out.println(
                        "ith:\t" + posTh.getAndIncrement() + "\t"+
                                Thread.currentThread().getName()
                                +"\tpos:\t" + pos+"\t");
            }
        }
        );
        thread.start();
    }

    void doToken4(){
        Thread thread=new Thread(()->{
            for(int ii=0;ii<TK_NUM_4;ii++) {
                int pos = balancer.traverse(4);
                System.out.println(
                        "ith:\t" + posTh.getAndIncrement() + "\t"+
                                Thread.currentThread().getName()
                                +"\tpos:\t" + pos+"\t");
            }
        }
        );
        thread.start();
    }
}
