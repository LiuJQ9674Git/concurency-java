package com.abc.art.counting.network;

public class BalancerTest {
    final Balancer balancer=new Balancer();
    public static void main(String[] args){
        BalancerTest balancerTest=new BalancerTest();
        while (true) {
            balancerTest.doBalancer();
            //balancerTest.doBalancer();
        }
    }

    void doBalancer(){
        Thread thread=new Thread(()->{
            for(int ii=0;ii<8;ii++) {
                int pos = balancer.traverse();
                System.out.println(
                        "\t ith:\t" + ii + "\t"+
                                Thread.currentThread().getName()
                     +"\tpos:\t" + pos+"\t");
            }
        }
        );
        thread.start();
    }
    void handleBalancer(){
        Thread thread=new Thread(()->{
            for(int ii=0;ii<5;ii++) {
                int pos = balancer.traverse();
                System.out.println(Thread.currentThread().getName() +
                        "\t ith:\t" + ii + "\tpos:\t" + pos);
            }
        }
        );
        thread.start();
    }
}
