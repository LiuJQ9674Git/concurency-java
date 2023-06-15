package com.abc.test;

//import com.sun.tools.internal.ws.wscompile.Options;

import java.util.Random;

public class StampedLockBit {

    static final int HASH_BITS = 0x7fffffff; // usable bits of normal node hash

    static final int spread(int h) {
        return (h ^ (h >>> 16)) & HASH_BITS;
    }

    static final int oxr(int h) {//不等为1
        return (h ^ (h >>> 16)) ;
    }
    static final int right16(int h) {
        return (h >>> 16) ;
    }

    private static final int LG_READERS = 7; // 127 readers

    // Values for lock state and stamp operations
    private static final long RUNIT = 1L;
    private static final long WBIT  = 1L << LG_READERS;
    private static final long RBITS = WBIT - 1L;
    private static final long RFULL = RBITS - 1L;
    private static final long ABITS = RBITS | WBIT;
    private static final long SBITS = ~RBITS;                   //SBITS:  -128 110000000
    private static final long RSAFE = ~(3L << (LG_READERS - 1)); //RSAFE: -193 1100111111

    private static final long SAFE_ = (3L << (LG_READERS - 1));
    private static final long ABIT_ = ~ABITS;
    private static final long ORIGIN = WBIT << 1;//256

    // Special value from cancelled acquire methods so caller can throw IE
    private static final long INTERRUPTED = 1L;

    // Bits for Node.status
    static final int WAITING   = 1;
    static final int CANCELLED = 0x80000000; // must be negative

    public static void main(String[] args){
        writeLock();
    }
    static void writeLock(){
        long state=640;
        long s;

        s=state & ABIT_;//11 0000 0000 减去111 1111
        long r=s | WBIT;
        //nextState = state | WBIT;
        System.out.println("state:\t"+state+"\tBit->:0"
                +Long.toBinaryString(WBIT));
        System.out.println("ABITS:\t"+ABIT_+"\t-:"+
                "1100000000");
        System.out.println("WBIT:\t"+WBIT+"\t\t-:"+
                "010000000");
        System.out.println("s:\t\t"+s+"\tBit->:"
                +Long.toBinaryString(s));
        System.out.println("r:\t\t"+r+"\tBit->:"
                +Long.toBinaryString(r));



        //100000000
    }

    public static void readLock(String[] args){

        //tryAcquireWrite();
        //tryOptimisticRead();
        long b=511;
        long s=b;
        long l=s&RSAFE;
        System.out.println("b:\t"+b+"\t\t\t"
                +Long.toBinaryString(b));

        System.out.println("s:\t"+s+"\t\t\t"
                +Long.toBinaryString(s));
        System.out.println("r:\t"+RSAFE+"\t\t100111111\t\t");
        System.out.println("l:\t"+l+"\t\t\t"
                +Long.toBinaryString(l));
        //1 1111 1111

        //SAFE_
        /**
         * s:	318			100111110
         * r:	-193		10 0111 111
         * l:	318			100111110
         *
         * s:	319			100111111
         * r:	-193		100111111
         * l:	319			100111111
         *
         *s:	320			101000000
         * r:	-193		100111111
         * l:	256			100000000
         *
         * s:	382			101111110
         * r:	-193		100111111
         * l:	318			100111110
         *
         * s:	375			101110111
         * r:	-193		100111111
         * l:	311			100110111
         *
         * s:	119			001110111
         * r:	-193		100111111
         * l:	55			000110111
         *
         * s:	256			100000000
         * r:	-193		100111111
         * l:	256			100000000
         *
         *s:	257			100000001
         * r:	-193		100111111
         * l:	257			100000001
         *
         * s:	258			100000010
         * r:	-193		100111111
         * l:	258			100000010
         *
         * s:	456			111001000
         * r:	-193		100111111
         * l:	264			100001000
         */
    }

    static void mis(){
        long t=640 & ~ABITS;          //10 1000  0000
        long b=~ABITS;                //11 0000 0000
        System.out.println("t:\t"+t+"\tBit->:\t"
                +Long.toBinaryString(t));

        System.out.println("640:\t"+t+"\tBit->:\t"
                +Long.toBinaryString(640));
        System.out.println("b:\t"+b+"\tBit->:\t"
                +Long.toBinaryString(b));
    }
    //public long tryOptimisticRead() {
    //        long s;
    //        return (((s = state) & WBIT) == 0L) ? (s & SBITS) : 0L;
    //    }
    static long tryOptimisticRead(){
        long state=ORIGIN;
        long s,nextState;
        s = state & WBIT;   //128
        nextState =s& SBITS;//-128
        System.out.println("ORIGIN:\t"+state+"\tBit->:\t"
                +Long.toBinaryString(state));
        System.out.println("WBIT:\t"+WBIT+"\tBit->:\t0"
                +Long.toBinaryString(WBIT)+
                "\n\t\ts & WBIT:\t"+s+
                "\n\t\ts & WBIT:\t"+Long.toBinaryString(s));

        System.out.println("SBITS:\t"+SBITS+"\tBit->:\t"+Long.toBinaryString(SBITS)+
                "\n\t\ts & SBITS:\t"+nextState+
                "\n\t\ts & SBITS:\t"+ Long.toBinaryString(nextState) );
        return 0L;
    }


     static long tryAcquireWrite() {
        long state=896;              //256 1 0000 0000
        long s, nextState;

         s=state & ABITS;               //255 0 1111 1111
         nextState = state | WBIT;      //128 0 1000 0000
         System.out.println("ORIGIN:\t"+state+"\tBit->:\t"
                 +Long.toBinaryString(state));
         System.out.println("ABITS:\t"+ABITS+"\tBit->:\t0"
                 +Long.toBinaryString(ABITS)+
                 "\n\t\ts & ABITS:\t"+s+
                 "\n\t\ts & ABITS:\t"+Long.toBinaryString(s));

         System.out.println("WBIT:\t"+WBIT+"\tBit->:\t"+Long.toBinaryString(WBIT)+
                 "\n\t\ts | WBIT:\t"+nextState+
                 "\n\t\ts | WBIT:\t"+ Long.toBinaryString(nextState) );

         /**
          * ORIGIN:	256	Bit->:	100000000
          * ABITS:	255	Bit->:	011111111
          * 		s & ABITS:	0
          * WBIT:	128	Bit->:	10000000
          * 		s | WBIT:	384
          * 		s | WBIT:	110000000
          * ////////////////////////////
          * ORIGIN:	0	Bit->:	0
          * ABITS:	255	Bit->:	011111111
          * 		s & ABITS:	0
          * 		s & ABITS:	0
          * WBIT:	128	Bit->:	10000000
          * 		s | WBIT:	128
          * 		s | WBIT:	10000000
          * //////////////////////////////
          *
          */
        return 0L;
    }

    static void handleStampedLockBit(){
        System.out.println("RUNIT->:\t"+RUNIT+"\tBit->:\t"+Long.toBinaryString(RUNIT));
        System.out.println("WBIT ->:\t"+WBIT+"\tBit->:\t"+Long.toBinaryString(WBIT));
        System.out.println("RFULL->:\t"+RFULL+"\tBit->:\t"+Long.toBinaryString(RFULL)+"\n");

        System.out.println("ABITS->:\t"+ABITS+"\tBit->:\t"+Long.toBinaryString(ABITS));
        System.out.println("ABITS->:\t"+ABIT_+"\tBit->:\t"+Long.toBinaryString(ABIT_)+"\n");

        System.out.println("RBITS->:\t"+RBITS+"\tBit->:\t"+Long.toBinaryString(RBITS));

        System.out.println("SBITS->:\t"+SBITS+"\tBit->:\t"+Long.toBinaryString(SBITS)+"\n");

        System.out.println("SAFE_->:\t"+SAFE_+"\tBit->:\t"+Long.toBinaryString(SAFE_));
        System.out.println("RSAFE->:\t"+RSAFE+"\tBit->:\t"+Long.toBinaryString(RSAFE));



        System.out.println("ORIGIN->:\t"+ORIGIN+"\tBit->:\t"+Long.toBinaryString(ORIGIN));

        /**
         * RUNIT->:	1	    Bit->:	1
         * WBIT ->:	128	    Bit->:	1000 0000
         * RFULL->:	126	    Bit->:	0111 1110
         * ABITS->:	255	    Bit->:	1111 1111
         *         -256         1 1 0000 0000
         * RBITS->:	127	    Bit->:	0111 1111
         * SBITS->:	-128	Bit->:1	1000 0000 取反
         *
         * SAFE_->:	192	    Bit->:	1100 0000
         * RSAFE->:	-193    Bit->:1	0011 1111 取反
         * ORIGIN->:256	    Bit->:1 0000 0000
         */
    }

    static void handleBit(){

        Random random = new Random();
        random.setSeed(10000L);
        //int p= random.nextInt() ;
        int p=Math.abs(random.nextInt());
        System.out.println(
                "\nsource-->b:\t"+Integer.toBinaryString(p)+ //原值
                        "\nright16->b:\t"+"0000000000000000"+Integer.toBinaryString(right16(p))+ //右移16
                        "\norx---->b:\t"+Integer.toBinaryString(oxr(p))+//原值与右移16后的值取或^
                        "\n\nspread->b: \t"+Integer.toBinaryString(spread(p)));
        //System.out.println("spread->b: \t"+sp+"\t"+Integer.toBinaryString(sp));

        /**               1   2     3    4    5    6    7    8
         * source-->b:	0111 1111 1111 1111 1111 1111 1111 1111
         * right16->b:	0000 0000 0000 0000 0111 1111 1111 1111
         * orx---->b:	0111 1111 1111 1111 1000 0000 0000 0000
         *
         * spread->b: 	0111 1111 1111 1111 1000 0000 0000 0000
         *
         * source-->b:	0111 1111 1111 1110 1010 0100 1100 0111
         * right16->b:	0000 0000 0000 0000 0111 1111 1111 1110
         * orx---->b:	0111 1111 1111 1110 1101 1011 0011 1001
         *
         * spread->b: 	0111 1111 1111 1110 1101 1011 0011 1001
         *
         * ///
         * source-->b:	1110 0010 0100 0110 0110 0101 1110 0000
         * right16->b:	0000 0000 0000 0000 1110 0010 0100 0110
         * orx---->b:	1110 0010 0100 0110 1000 0111 1010 0110
         *
         * spread->b: 	1100010010001101000011110100110
         */
    }


}
