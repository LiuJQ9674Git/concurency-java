package com.abc.test;

import com.sun.tools.internal.ws.wscompile.Options;

import java.util.Random;

public class BitTest {

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
    private static final long SBITS = ~RBITS; // note overlap with ABITS
    // not writing and conservatively non-overflowing              -128 110000000
    private static final long RSAFE = ~(3L << (LG_READERS - 1)); //-193 1100111111

    private static final long SAFE_ = (3L << (LG_READERS - 1));
    private static final long ABIT_ = ~ABITS;
    private static final long ORIGIN = WBIT << 1;//256

    public static void main(String[] args){

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
        System.out.println("\n===================\n");
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
