package com.abc.test;

public class StampedLockBit {

    private static final int LG_READERS = 7; // 127 readers
    private static final long RUNIT = 1L;
    private static final long WBIT  = 1L << LG_READERS;
    private static final long RBITS = WBIT - 1L;
    private static final long RFULL = RBITS - 1L;
    private static final long ABITS = RBITS | WBIT;
    private static final long SBITS = ~RBITS; // note overlap with ABITS
    // not writing and conservatively non-overflowing
    private static final long RSAFE = ~(3L << (LG_READERS - 1));
    private static final long ORIGIN = WBIT << 1;

    private static final long ABITS_OP=~ABITS;
    public static void main(String[] args){

        lockBit();
    }

    static void lockBit(){

        System.out.println("WBIT:\t"+WBIT+"\tHex->:\t"
                +Long.toHexString(WBIT) +"\nBit-Size->:\t"+Long.toBinaryString(WBIT).length()
                +"\t\t"+"\tHex-Size->:"+Long.toHexString(WBIT).length()
                +"\n"+ ForkJoinPoolBit.toString(Long.toBinaryString(WBIT))
        );

        System.out.println("RBITS:\t"+RBITS+"\tHex->:\t"
                +Long.toHexString(RBITS) +"\nBit-Size->:\t"+Long.toBinaryString(RBITS).length()
                +"\t\t"+"\tHex-Size->:"+Long.toHexString(RBITS).length()
                +"\n"+ ForkJoinPoolBit.toString(Long.toBinaryString(RBITS))
        );

        System.out.println("RFULL:\t"+RFULL+"\tHex->:\t"
                +Long.toHexString(RFULL) +"\nBit-Size->:\t"+Long.toBinaryString(RFULL).length()
                +"\t\t"+"\tHex-Size->:"+Long.toHexString(RFULL).length()
                +"\n"+ ForkJoinPoolBit.toString(Long.toBinaryString(RFULL))
        );
        //
        System.out.println("ABITS:\t"+ABITS+"\tHex->:\t"
                +Long.toHexString(ABITS) +"\nBit-Size->:\t"+Long.toBinaryString(ABITS).length()
                +"\t\t"+"\tHex-Size->:"+Long.toHexString(ABITS).length()
                +"\n"+ ForkJoinPoolBit.toString(Long.toBinaryString(ABITS))
        );

        System.out.println("ABITS~:\t"+ABITS_OP+"\tHex->:\t"
                +Long.toHexString(ABITS_OP) +"\nBit-Size->:\t"+Long.toBinaryString(ABITS_OP).length()
                +"\t\t"+"\tHex-Size->:"+Long.toHexString(ABITS_OP).length()
                +"\n"+ ForkJoinPoolBit.toString(Long.toBinaryString(ABITS_OP))
        );

        System.out.println("SBITS:\t"+SBITS+"\tHex->:\t"
                +Long.toHexString(SBITS) +"\nBit-Size->:\t"+Long.toBinaryString(SBITS).length()
                +"\t\t"+"\tHex-Size->:"+Long.toHexString(SBITS).length()
                +"\n"+ ForkJoinPoolBit.toString(Long.toBinaryString(SBITS))
        );


        System.out.println("RSAFE:\t"+RSAFE+"\tHex->:\t"
                +Long.toHexString(RSAFE) +"\nBit-Size->:\t"+Long.toBinaryString(RSAFE).length()
                +"\t\t"+"\tHex-Size->:"+Long.toHexString(RSAFE).length()
                +"\n"+ ForkJoinPoolBit.toString(Long.toBinaryString(RSAFE))
        );

        System.out.println("ORIGIN:\t"+ORIGIN+"\tHex->:\t"
                +Long.toHexString(ORIGIN) +"\nBit-Size->:\t"+Long.toBinaryString(ORIGIN).length()
                +"\t\t"+"\tHex-Size->:"+Long.toHexString(ORIGIN).length()
                +"\n"+ ForkJoinPoolBit.toString(Long.toBinaryString(ORIGIN))
        );

    }
}
