package com.abc.art.mutalexcute;

import java.util.concurrent.atomic.AtomicStampedReference;

public class FastPathTest {
    private static final int FASTPATH = 1 << 30;
    AtomicStampedReference<Integer> tail=new AtomicStampedReference<Integer>(null,0);;
    private boolean fastPathLock() {
        int oldStamp, newStamp;
        int stamp[] = {0};
        Integer qnode;
        qnode = tail.get(stamp);
        oldStamp = stamp[0];
        if (qnode != null) {//有值
            return false;
        }
        if ((oldStamp & FASTPATH) != 0) {//1073741824
            return false; //有标值时
        }
        newStamp = (oldStamp + 1) | FASTPATH;
        return tail.compareAndSet(qnode, null,
                oldStamp, newStamp);//0 1073741825
    }
    private boolean fastPathUnlock() {
        int oldStamp, newStamp;
        oldStamp = tail.getStamp();
        if ((oldStamp & FASTPATH) == 0) {
            return false; //无标记值时返回
        }
        int[] stamp = {0};
        Integer qnode;
        do {
            qnode = tail.get(stamp);
            oldStamp = stamp[0];//原始值
            newStamp = oldStamp & (~FASTPATH); //新值
        } while (!tail.compareAndSet(qnode, qnode,
                oldStamp, newStamp)); //1073741825 1
        return true;
    }
    public static void main(String[] args){
//        FastPathTest fastPath=new FastPathTest();
//        Integer i=  0;
        //fastPath.tail.set(i,0);
//        fastPath.fastPathLock();
//        fastPath.fastPathUnlock();
//        fastPath.fastPathUnlock();
        handleFASTPATH();
    }

    public static void handleFASTPATH(){
        int oldStamp=0, newStamp;

        System.out.println("FASTPATH |:\t"+FASTPATH+"\t0"+Integer.toBinaryString(FASTPATH));
        System.out.println("FASTPATH |:\t"+FASTPATH+"\t"+Integer.toBinaryString(~FASTPATH));
        //newStamp = (0) | FASTPATH; //1073741824
        newStamp = (oldStamp + 1) | FASTPATH; //
        System.out.println("newStamp |:\t"+newStamp+"\t0"+Integer.toBinaryString(newStamp));

        newStamp = (newStamp + 1) | FASTPATH; //
        System.out.println("newStamp |:\t"+newStamp+"\t0"+Integer.toBinaryString(newStamp));

        newStamp = (newStamp + 1) | FASTPATH; //
        System.out.println("newStamp |:\t"+newStamp+"\t0"+Integer.toBinaryString(newStamp));

        newStamp = (newStamp + 1) | FASTPATH; //
        System.out.println("newStamp |:\t"+newStamp+"\t0"+Integer.toBinaryString(newStamp));

        newStamp = (newStamp + 1) | FASTPATH; //
        System.out.println("newStamp |:\t"+newStamp+"\t0"+Integer.toBinaryString(newStamp));

        newStamp = (newStamp - 1) & (~FASTPATH);
        System.out.println("newStamp:&~\t"+newStamp+"\t"+Integer.toBinaryString(newStamp));

        newStamp = (newStamp - 1) & (~FASTPATH);
        System.out.println("newStamp:&~\t"+newStamp+"\t"+Integer.toBinaryString(newStamp));

        newStamp = (newStamp - 1) & (~FASTPATH);
        System.out.println("newStamp:&~\t"+newStamp+"\t"+Integer.toBinaryString(newStamp));

        newStamp = (newStamp - 1) & (~FASTPATH);
        System.out.println("newStamp:&~\t"+newStamp+"\t"+Integer.toBinaryString(newStamp));

    }
}
