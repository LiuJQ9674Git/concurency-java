package com.abc.test;

//import com.sun.tools.internal.ws.wscompile.Options;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
public class ForkJoinPoolBit {

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
        //forkinit();
        //alg();
        //writeLock();

        // ForkJoinPool
//        displayNumber();
//        posbit();
//        shiftbit();
        int seed=28;
        //runWorker(seed);
        //poolBitArg();
        //testForkJoinData();
        threadRadom();
    }

    static void threadRadom(){
        Long z=0xc4ceb9fe1a85ec53L;
        System.out.println("z:\t"+z+"\tHex->:\t"
                +Long.toHexString(z) +"\nBit-Size->:\t"+Long.toBinaryString(z).length()
                +"\t\t"+"\tHex-Size->:"+Long.toHexString(z).length()
                +"\n"+ toString(Long.toBinaryString(z))
        );

    }
    public static void displayNumber(){
        Long maxLong =Long.MAX_VALUE;
        Long minLong =Long.MIN_VALUE;
        System.out.println("maxLong:\t"+maxLong+"\tHex->:\t"
                +Long.toHexString(maxLong) +"\nBit-Size->:\t"+Long.toBinaryString(maxLong).length()
                +"\t\t"+"\tHex-Size->:"+Long.toHexString(maxLong).length()
                +"\n"+ toString(Long.toBinaryString(maxLong))
        );
        System.out.println("minLong:\t"+minLong+"\tHex->:\t"
                +Long.toHexString(minLong) +"\nBit-Size->:\t"+Long.toBinaryString(minLong).length()
                +"\t\t"+"\tHex-Size->:"+Long.toHexString(minLong).length()
                +"\n"+ toString(Long.toBinaryString(minLong))
        );

        System.out.println();
        Integer maxInteger=Integer.MAX_VALUE;
        Integer mimInteger=Integer.MIN_VALUE;
        System.out.println("maxInteger:\t"+maxInteger+"\tHex->:\t"
                +Integer.toHexString(maxInteger) +"\nBit-Size->:\t"+Integer.toBinaryString(maxInteger).length()
                +"\t\t"+"\tHex-Size->:"+Integer.toHexString(maxInteger).length()
                +"\n"+ toString(Long.toBinaryString(maxInteger))
        );
        System.out.println("mimInteger:\t"+minLong+"\tHex->:\t"
                +Integer.toHexString(mimInteger) +"\nBit-Size->:\t"+Integer.toBinaryString(mimInteger).length()
                +"\t\t"+"\tHex-Size->:"+Integer.toHexString(mimInteger).length()
                +"\n"+ toString(Integer.toBinaryString(mimInteger))
        );

        System.out.println();
        Short maxShort=Short.MAX_VALUE;
        Short mimShort=Short.MIN_VALUE;
        System.out.println("maxShort:\t"+maxShort+"\tHex->:\t"
                +Integer.toHexString(maxShort) +"\nBit-Size->:\t"+Integer.toBinaryString(maxShort).length()
                +"\t\t"+"\tHex-Size->:"+Integer.toHexString(maxShort).length()
                +"\n"+ toString(Long.toBinaryString(maxShort))
        );
        System.out.println("mimShort:\t"+mimShort+"\tHex->:\t"
                +Integer.toHexString(mimShort) +"\nBit-Size->:\t"+Integer.toBinaryString(mimShort).length()
                +"\t\t"+"\tHex-Size->:"+Integer.toHexString(mimShort).length()
                +"\n"+ toString(Integer.toBinaryString(mimShort))
        );
        System.out.println();

    }
    public static String toString(String numb){
        StringBuffer sb=new StringBuffer((new StringBuffer(numb)).reverse());
        StringBuffer newStr=new StringBuffer();
        for(int i=0,j=1;i<numb.length();i++,j++){
            newStr.append(sb.charAt(i));
            if(j % 4==0){
                newStr.append(" ");
            }
        }

        return newStr.reverse().toString();
    }
    static final int FIFO         = 1 << 16;       // fifo queue or access mode
    static final int SRC          = 1 << 17;       // set for valid queue ids
    static final int INNOCUOUS    = 1 << 18;       // set for Innocuous workers
    static final int QUIET        = 1 << 19;       // quiescing phase or source
    static final int SHUTDOWN     = 1 << 24;
    static final int TERMINATED   = 1 << 25;
    static final int STOP         = 1 << 31;       // must be negative
    static final int UNCOMPENSATE = 1 << 16;       // tryCompensate return
    static final int UNSIGNALLED  = 1 << 31;     // must be negative
    static final int SS_SEQ       = 1 << 16;     // version count
    static final int  TC_SHIFT   = 32;
    static final long ADD_WORKER = 0x0001L << (TC_SHIFT + 15); // sign

    static final int SMASK        = 0xffff;        // short bits == max index
    static final int MAX_CAP      = 0x7fff;        // max #workers - 1

    // static final int UNSIGNALLED  = 1 << 31;       // must be negative
    static final int  RC_SHIFT   = 48;
    private static final long SP_MASK    = 0xffffffffL;
    private static final long UC_MASK    = ~SP_MASK;


    private static final long RC_UNIT    = 0x0001L << RC_SHIFT;
    private static final long RC_MASK    = 0xffffL << RC_SHIFT;

    //private static final int  TC_SHIFT   = 32;
    private static final long TC_UNIT    = 0x0001L << TC_SHIFT;
    private static final long TC_MASK    = 0xffffL << TC_SHIFT;
    //private static final long ADD_WORKER = 0x0001L << (TC_SHIFT + 15); // sign
    private static final long NS_MASK=0xffffffffL;//nsteals
    private static final long SAN=0x61c88647;


    static final long RC_MASKOR    =~RC_MASK;
    public static void shiftbit(){

        System.out.println("TC_MASK:\t"+TC_MASK+
                "\tH>:\t"+Long.toHexString(TC_MASK)+
                        "\tS-B->:"
                        +Long.toBinaryString(TC_MASK).length()+"\n"
                +toString(Long.toBinaryString(TC_MASK)));

        System.out.println("~RC_MASK:\t"+RC_MASKOR+
                "\tH>:\t"+Long.toHexString(RC_MASKOR)+
                        "\tS-B->:"
                        +Long.toBinaryString(RC_MASKOR).length()+"\n"
                +toString(Long.toBinaryString(RC_MASKOR)));

        System.out.println("RC_MASK:\t"+RC_MASK+
                "\tH>:\t"+Long.toHexString(RC_MASK)+
                        "\tS-B->:"
                        +Long.toBinaryString(RC_MASK).length()+"\n"
                +toString(Long.toBinaryString(RC_MASK)));

        System.out.println("SMASK:\t"+SMASK+
                "\tH>:\t"+Long.toHexString(SMASK)+
                        "\tS-B->:"
                        +Long.toBinaryString(SMASK).length()+"\n"
                +toString(Long.toBinaryString(SMASK)));

        System.out.println("UC_MASK:\t"+UC_MASK+
                "\tH>:\t"+Long.toHexString(UC_MASK)+
                        "\tS-B->:"
                        +Long.toBinaryString(UC_MASK).length()+"\n"
                +toString(Long.toBinaryString(UC_MASK)));

        System.out.println("SP_MASK:\t"+SP_MASK+
                "\tH>:\t"+Long.toHexString(SP_MASK)+
                        "\tS-B->:"
                        +Long.toBinaryString(SP_MASK).length()+"\n"
                +toString(Long.toBinaryString(SP_MASK)));

        System.out.println("RC_UNIT:\t"+RC_UNIT+
                "\tH>:\t"+Long.toHexString(RC_UNIT)+
                        "\tS-B->:"
                        +Long.toBinaryString(RC_UNIT).length()+"\n"
                +toString(Long.toBinaryString(RC_UNIT)));

        System.out.println("TC_UNIT:\t"+TC_UNIT+
                "\tH>:\t"+Long.toHexString(TC_UNIT)+
                        "\tS-B->:"
                        +Long.toBinaryString(TC_UNIT).length()+"\n"
                +toString(Long.toBinaryString(TC_UNIT)));

        System.out.println("ADD_WORKER:\t"+ADD_WORKER+
                "\tH>:\t"+Long.toHexString(ADD_WORKER)+
                        "\tS-B->:"
                        +Long.toBinaryString(ADD_WORKER).length()+"\n"
                +toString(Long.toBinaryString(ADD_WORKER)));
        System.out.println();
    }

    static int UNSIGNALLEDOR  = ~UNSIGNALLED;
    // 110 0001 1100 1000 1000 0110 0100 0111
    // 1640531527

    //UNSIGNALLED
    //-2147483648
    //1 1111 1111 11111 11111 11111 1111 1111 1000 0000 0000 0000 0000 0000 0000 0000

    public static void posbit(){
        System.out.println("FIFO:\t"+FIFO+
                "\tH>:\t"+Long.toHexString(FIFO)+
                "\tS-B->:"
                +Long.toBinaryString(FIFO).length()+"\n"
                +toString(Long.toBinaryString(FIFO)));

        System.out.println("SRC:\t"+SRC+
                "\tH>:\t"+Long.toHexString(SRC)+
                        "\tS-B->:"
                        +Long.toBinaryString(SRC).length()+"\n"
                +toString(Long.toBinaryString(SRC)));

        System.out.println("INNOCUOUS:\t"+INNOCUOUS+
                "\tH>:\t"+Long.toHexString(INNOCUOUS)+
                "\tS-B->:"
                +Long.toBinaryString(INNOCUOUS).length()+"\n"
                +toString(Long.toBinaryString(INNOCUOUS)));

        System.out.println("QUIET:\t"+QUIET+
                "\tH>:\t"+Long.toHexString(QUIET)+
                        "\tS-B->:"
                        +Long.toBinaryString(QUIET).length()+"\n"
                +toString(Long.toBinaryString(QUIET)));

        System.out.println("SHUTDOWN:\t"+SHUTDOWN+
                "\tH>:\t"+Long.toHexString(SHUTDOWN)+
                        "\tS-B->:"
                        +Long.toBinaryString(SHUTDOWN).length()+"\n"
                +toString(Long.toBinaryString(SHUTDOWN)));

        System.out.println("TERMINATED:\t"+TERMINATED+
                "\tH>:\t"+Long.toHexString(TERMINATED)+
                        "\tS-B->:"
                        +Long.toBinaryString(TERMINATED).length()+"\n"
                +toString(Long.toBinaryString(TERMINATED)));

        System.out.println("STOP:\t"+STOP+
                "\tH>:\t"+Integer.toHexString(STOP)+
                        "\tS-B->:"
                        +Integer.toBinaryString(STOP).length()+"\n"
                +toString(Integer.toBinaryString(STOP)));

        System.out.println("UNCOMPENSATE:\t"+UNCOMPENSATE+
                "\tH>:\t"+Long.toHexString(UNCOMPENSATE)+
                        "\tS-B->:"
                        +Long.toBinaryString(UNCOMPENSATE).length()+"\n"
                +toString(Long.toBinaryString(UNCOMPENSATE)));

        //

        System.out.println();
        System.out.println("MAX_CAP:\t"+MAX_CAP+"\tH>:\t"+Long.toHexString(MAX_CAP)+
                "\tS-B->:"
                +Long.toBinaryString(MAX_CAP).length()+"\n"
                +toString(Long.toBinaryString(MAX_CAP)));

        System.out.println("SS_SEQ:\t"+SS_SEQ+"\tH>:\t"+Long.toHexString(SS_SEQ)+
                "\tS-B->:"
                +Long.toBinaryString(SS_SEQ).length()+"\n"
                +toString(Long.toBinaryString(SS_SEQ)));

        System.out.println();

        System.out.println("UNSIGNALLED:\t"+UNSIGNALLED+
                "\tH>:\t"+Long.toHexString(UNSIGNALLED)+
                "\tS-B->:"
                +Long.toBinaryString(UNSIGNALLED).length()+"\n"
                +toString(Long.toBinaryString(UNSIGNALLED)));


        System.out.println("~UNSIGNALLED:\t"+UNSIGNALLEDOR+"" +
                "\tH>:\t"+Long.toHexString(UNSIGNALLEDOR)+
                "\tS-B->:"
                +Long.toBinaryString(UNSIGNALLEDOR).length()+"\n"
                +toString(Long.toBinaryString(UNSIGNALLEDOR)));

        System.out.println();


    }

    static void testForkJoinData(){

        long ctl=-1970359196712960L;

        System.out.println("ctl:\t"+ctl+
                "\tH>:\t"+Long.toHexString(ctl)+
                "\tS-B->:"
                +Long.toBinaryString(ctl).length()+"\n"
                +toString(Long.toBinaryString(ctl)));

        int ictl=(int)ctl;
        System.out.println("ictl:\t"+ictl+
                "\tH>:\t"+Integer.toHexString(ictl)+
                "\tS-B->:"
                +Integer.toBinaryString(ictl).length()+"\n"
                +toString(Integer.toBinaryString(ictl)));

        long splong=2147483647;
        int sp=(int)2147483647;

        System.out.println("sp:\t"+splong+
                "\tH>:\t"+Long.toHexString(splong)+
                "\tS-B->:"
                +Long.toBinaryString(splong).length()+"\n"
                +toString(Long.toBinaryString(splong)));

        System.out.println("sp:\t"+splong+
                "\tH>:\t"+Integer.toHexString(sp)+
                "\tS-B->:"
                +Integer.toBinaryString(sp).length()+"\n"
                +toString(Integer.toBinaryString(sp)));

        long c=-1407400653357056L;

        int ic=(int)c;

        System.out.println("c:\t"+c+
                "\tH>:\t"+Long.toHexString(c)+
                "\tS-B->:"
                +Long.toBinaryString(c).length()+"\n"
                +toString(Long.toBinaryString(c)));

        System.out.println("ic:\t"+c+
                "\tH>:\t"+Integer.toHexString(ic)+
                "\tS-B->:"
                +Integer.toBinaryString(ic).length()+"\n"
                +toString(Integer.toBinaryString(ic)));

        long cc=-281474976710656L;
        int icc=(int)cc;

        System.out.println("cc:\t"+cc+
                "\tH>:\t"+Long.toHexString(cc)+
                "\tS-B->:"
                +Long.toBinaryString(cc).length()+"\n"
                +toString(Long.toBinaryString(cc)));

        System.out.println("icc:\t"+cc+
                "\tH>:\t"+Integer.toHexString(icc)+
                "\tS-B->:"
                +Integer.toBinaryString(icc).length()+"\n"
                +toString(Integer.toBinaryString(icc)));

    }
    static void poolBitArg(){
        int p=16;
        int corePoolSize=64;
        int corep = Math.min(Math.max(corePoolSize, p), MAX_CAP);
        long pr=-p;
        long core=-corep;

        System.out.println("p:\t"+p+
                "\tH>:\t"+Long.toHexString(p)+
                "\tS-B->:"
                +Long.toBinaryString(p).length()+"\n"
                +toString(Long.toBinaryString(p)));

        System.out.println("-p:\t"+pr+
                "\tH>:\t"+Long.toHexString(pr)+
                "\tS-B->:"
                +Long.toBinaryString(pr).length()+"\n"
                +toString(Long.toBinaryString(pr)));



        System.out.println("-core:\t"+core+
                "\tH>:\t"+Long.toHexString(core)+
                "\tS-B->:"
                +Long.toBinaryString(core).length()+"\n"
                +toString(Long.toBinaryString(core)));

        //
        System.out.println("-corep(tc):\t"+pr+
                "\tH>:\t"+Long.toHexString((((long)(-corep) << TC_SHIFT) & TC_MASK))+
                "\tS-B->:"
                +Long.toBinaryString((((long)(-corep) << TC_SHIFT) & TC_MASK)).length()+"\n"
                +toString(Long.toBinaryString((((long)(-corep) << TC_SHIFT) & TC_MASK))));
        //
        System.out.println("-p(rc):\t"+core+
                "\tH>:\t"+Long.toHexString((((long)(-p)     << RC_SHIFT) & RC_MASK))+
                "\tS-B->:"
                +Long.toBinaryString((((long)(-p)     << RC_SHIFT) & RC_MASK)).length()+"\n"
                +toString(Long.toBinaryString((((long)(-p)     << RC_SHIFT) & RC_MASK))));

        //并发数初始值
        long ctl = ((((long)(-corep) << TC_SHIFT) & TC_MASK) | //核数（线程数、工作者数）
                (((long)(-p)     << RC_SHIFT) & RC_MASK)); //并发数

        System.out.println();

        //数组大小
        int size = 1 << (33 - Integer.numberOfLeadingZeros(p - 1));

        System.out.println("size:\t"+size+
                "\tH>:\t"+Integer.toHexString(size)+
                "\tS-B->:"
                +Integer.toBinaryString(size).length()+"\n"
                +toString(Integer.toBinaryString(size)));

        //获取 seed
        int seed=probe(size);

        System.out.println("seed:\t"+seed+
                "\tH>:\t"+Integer.toHexString(seed)+
                "\tS-B->:"
                +Integer.toBinaryString(seed).length()+"\n"
                +toString(Integer.toBinaryString(seed)));

        //

        int id = (seed << 1) | 1;

        System.out.println("phase.id:\t"+id+
                "\tH>:\t"+Integer.toHexString(id)+
                "\tS-B->:"
                +Integer.toBinaryString(id).length()+"\n"
                +"\t(seed << 1) | 1\n"
                +toString(Integer.toBinaryString(id)));

        //
        int phase = (id + SS_SEQ) & ~UNSIGNALLED;

        System.out.println("phase+:\t"+phase+
                "\tH>:\t"+Integer.toHexString(phase)+
                "\tS-B->:"
                +Integer.toBinaryString(phase).length()+"\n"
                +"\t(seed + SS_SEQ) & ~UNSIGNALLED\n"
                +toString(Integer.toBinaryString(phase)));

        phase = phase | UNSIGNALLED;  // advance phase

        System.out.println("phase-:\t"+phase+
                "\tH>:\t"+Integer.toHexString(phase)+
                "\tS-B->:"
                +Integer.toBinaryString(phase).length()+"\n"
                +"\tphase | UNSIGNALLED\n"
                +toString(Integer.toBinaryString(phase)));

        System.out.println("ctl:\t"+ctl+
                "\tH>:\t"+Long.toHexString(ctl)+
                "\tS-B->:"
                +Long.toBinaryString(ctl).length()+"\n"
                +toString(Long.toBinaryString(ctl)));

        long cdd = (ctl - RC_UNIT) ;

        System.out.println("ctl - RC_UNIT:\t"+cdd+
                "\tH>:\t"+Long.toHexString(cdd)+
                "\tS-B->:"
                +Long.toBinaryString(cdd).length()+"\n"
                +"\tctl - RC_UNIT\n"
                +toString(Long.toBinaryString(cdd)));

        cdd = (cdd - RC_UNIT) ;

        System.out.println("ctl - RC_UNIT:\t"+cdd+
                "\tH>:\t"+Long.toHexString(cdd)+
                "\tS-B->:"
                +Long.toBinaryString(cdd).length()+"\n"
                +"\tctl - RC_UNIT\n"
                +toString(Long.toBinaryString(cdd)));


        long codd = ((ctl - RC_UNIT) & UC_MASK) ;

        System.out.println("ctt-R_U & UC_MASK:\t"+codd+
                "\tH>:\t"+Long.toHexString(codd)+
                "\tS-B->:"
                +Long.toBinaryString(codd).length()+"\n"
                +"\t((ctl - RC_UNIT) & UC_MASK)\n"
                +toString(Long.toBinaryString(codd)));

       long phaseSP= (phase & SP_MASK);

        System.out.println("phase & SP_MASK:\t"+phaseSP+
                "\tH>:\t"+Long.toHexString(phaseSP)+
                "\tS-B->:"
                +"\t"+Long.toBinaryString(phaseSP).length()+"\n"
                +"\t(phase & SP_MASK)\n"
                +toString(Long.toBinaryString(phaseSP)));

        //

        long c = ((ctl - RC_UNIT) & UC_MASK) | (phase & SP_MASK);

        System.out.println("ctl-:\t"+c+
                "\tH>:\t"+Long.toHexString(c)+
                "\tS-B->:"
                +Long.toBinaryString(c).length()+"\n"
                +"\t ((ctl - RC_UNIT) & UC_MASK) | (phase & SP_MASK)\n"
                +toString(Long.toBinaryString(c)));

        //
        ctl = c;
        int ac = (int)(ctl >> RC_SHIFT);
        System.out.println("ac:\t"+ac+
                "\tH>:\t"+Integer.toHexString(ac)+
                "\tS-B->:"
                +Integer.toBinaryString(ac).length()+"\n"
                +"\tctl >> RC_SHIFT\n"
                +toString(Integer.toBinaryString(ac)));


        long nc = ((RC_MASK & (ctl - RC_UNIT)) | (~RC_MASK & ctl));
        System.out.println("nc:\t"+nc+
                "\tH>:\t"+Long.toHexString(nc)+
                "\tS-B->:"
                +Long.toBinaryString(nc).length()+"\n"
                +"\t nc = ((RC_MASK & (ctl - RC_UNIT)) | (~RC_MASK & ctl)); \n"
                +toString(Long.toBinaryString(nc)));

        int acSMASK = p & SMASK;
        System.out.println("p & SMASK:\t"+acSMASK+
                "\tH>:\t"+Integer.toHexString(acSMASK)+
                "\tS-B->:"
                +Integer.toBinaryString(acSMASK).length()+"\n"
                +toString(Integer.toBinaryString(acSMASK)));
        //
        System.out.println();
        long one = -1;
        System.out.println("-1:\t"+one+
                "\tH>:\t"+Long.toHexString(one)+
                "\tS-B->:"
                +Long.toBinaryString(one).length()+"\n"
                +toString(Long.toBinaryString(one)));
        one = -2;
        System.out.println("-2:\t"+one+
                "\tH>:\t"+Long.toHexString(one)+
                "\tS-B->:"
                +Long.toBinaryString(one).length()+"\n"
                +toString(Long.toBinaryString(one)));
        System.out.println();

    }
    static void runWorker(int stackPred){
        int r = stackPred;
        int  src = 0;
        do {
            r ^= r << 13; r ^= r >>> 17; r ^= r << 5; // xorshift
        }while ((src = scan(r)) >= 0 || //
                (src = awaitWork()) == 0); //当scan值小于0
    }

    static int scan(int r){
        System.out.println("r:\t"+r+
                "\tH>:\t"+Integer.toHexString(r)+
                "\tS-B->:"
                +Integer.toBinaryString(r).length()+"\n"
                +toString(Integer.toBinaryString(r)));

        int step = (r >>> 16) | 1;

        System.out.println("step:\t"+step+
                "\tH>:\t"+Integer.toHexString(step)+
                "\tS-B->:"
                +Integer.toBinaryString(step).length()+"\n"
                +toString(Integer.toBinaryString(step)));

        System.out.println("scan ok ");
        return -1;
    }

    static int awaitWork() {
        System.out.println("awaitWork ok ");
        return -1;
    }

    static ThreadLocalRandom localRandom= ThreadLocalRandom.current();
    static int probe(int size){
        //localRandom.localInit();
        //获取线程随机数(线程探针哈希)
        int seed;
        return (seed = localRandom.nextInt(size));
    }

    static int probe(){
        //localRandom.nextSeed();
        return localRandom.nextInt();
    }
    public static void forkinit(){
        int seed = 16;
        int id = (seed << 1) | 1;
        //seed:	10	Bit->:1010
        //id:	21	Bit->:10101 //11

        //seed:	11	Bit->: 1011 //12
        //id:	23	Bit->:10111

        //seed:	12	Bit->:1100 13
        //id:	25	Bit->:11001

        //seed:	13	Bit->:1101  14
        //id:	27	Bit->:11011

        //seed:	16	Bit->:10000 15
        //id:	33	Bit->:100001

        //seed:	31	Bit->:11111  32
        //id:	63	Bit->:111111

        //seed:	32	Bit->:100000 33
        //id:	65	Bit->:1000001

        //seed:	33	Bit->:100001 34
        //id:	67	Bit->:1000011
        System.out.println("seed:\t"+seed+"\tBit->:"
                +Long.toBinaryString(seed));
        System.out.println("id:\t"+id+"\tBit->:"
                +Long.toBinaryString(id));
        int p = 16;

        //int size = 1 << (33 - Integer.numberOfLeadingZeros(p - 1));
        int corep = Math.min(Math.max(16, p), MAX_CAP);
        //int maxSpares = Math.min(maximumPoolSize, MAX_CAP) - p;
       // int minAvail = Math.min(Math.max(minimumRunnable, 0), MAX_CAP);
       // this.bounds = ((minAvail - p) & SMASK) | (maxSpares << SWIDTH);
        //this.mode = p | (asyncMode ? FIFO : 0);

        //TC_SHIFT   = 32;
        //TC_MASK:	281470681743360	Bit->:    1111 1111 1111 1111 0000 0000 0000 0000 0000 0000 0000 0000
        long tcp=(((long)(-corep) << TC_SHIFT) & TC_MASK); //-16
        //RC_SHIFT   = 48;
        //                 1111 1111 1111 1111 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000
        //  281406257233920
        //-4503599627370496
        //-4222193370136576
        //      16  15   14   13    12   11   10   9     8    7    6   5    4    3    2    1
        //tcp:                     1111 1111 1111 0000 0000 0000 0000 0000 0000 0000 0000 0000 32
        //rcp: 1111 1111 1111 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 48
        //ctl: 1111 1111 1111 0000 1111 1111 1111 0000 0000 0000 0000 0000 0000 0000 0000 0000 16/15
        long rcp=(((long)(-p)<< RC_SHIFT) & RC_MASK);

        long ctl = ((((long)(-corep) << TC_SHIFT) & TC_MASK) |
                (((long)(-p)     << RC_SHIFT) & RC_MASK));

        long pp=-16; //long 16*4
        //111 1111 1111 1111 11111 1111 1111 1111 1111 1111 1111 1111 1111 1111 1111 0000
        //                                                                         1 0000
        System.out.println("pp:\t"+pp+"\tBit->:"
                +Long.toBinaryString(pp));

        System.out.println("p:\t"+p+"\tBit->:"
                +Long.toBinaryString(p));

        System.out.println("corep:\t"+corep);
        System.out.println("p:\t"+p);

        System.out.println("tcp:\t"+tcp+"\tBit->:"
                +Long.toBinaryString(tcp));
        System.out.println("rcp:\t"+rcp+"\tBit->:"
                +Long.toBinaryString(rcp));
        System.out.println("ctl:\t"+ctl+"\tBit->:"
                +Long.toBinaryString(ctl));
    }

    //private static final long SP_MASK    = 0xffffffffL;

    static void alg(){

        //SP_MASK
        //          4294967295                                  1111 1111 1111 1111 1111 1111 1111 1111
        //-4294967296          11111111111111111111111111111111 0000 0000 0000 0000 0000 0000 0000 0000
        //UNSIGNALLED  = 1 << 31;
        //-2147483648          11111111111111111111111111111111 1000 0000 0000 0000 0000 0000 0000 0000

        //TC_UNIT:	4294967296	                              1 0000 0000 0000 0000 0000 0000 0000 0000
        //TC_MASK:	281470681743360	        1111 1111 1111 1111 0000 0000 0000 0000 0000 0000 0000 0000
        //ADD_WORKER:	140737488355328     1000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000
        System.out.println("SP_MASK:\t"+SP_MASK+"\tBit->:"
                +Long.toBinaryString(SP_MASK));
        System.out.println("UC_MASK:\t"+UC_MASK+"\tBit->:"
                +Long.toBinaryString(UC_MASK));

        int UNSIGNALLED  = 1 << 31;
        // 110 0001 1100 1000 1000 0110 0100 0111
        // 1640531527

        //UNSIGNALLED
        //-2147483648
        //1 1111 1111 11111 11111 11111 1111 1111 1000 0000 0000 0000 0000 0000 0000 0000
        System.out.println("UNSIGNALLED:\t"+UNSIGNALLED+"\tBit->:"
                +Long.toBinaryString(UNSIGNALLED));

        int UNSIGNALLEDOR  = ~UNSIGNALLED;
        // 110 0001 1100 1000 1000 0110 0100 0111
        // 1640531527

        //UNSIGNALLED
        //-2147483648
        //1 1111 1111 11111 11111 11111 1111 1111 1000 0000 0000 0000 0000 0000 0000 0000
        System.out.println("UNSIGNALLEDOR:\t"+UNSIGNALLEDOR+"\tBit->:"
                +Long.toBinaryString(UNSIGNALLEDOR));

        System.out.println("SAN:\t"+SAN+"\tBit->:"
                +Long.toBinaryString(SAN));

        System.out.println("NS_MASK:\t"+NS_MASK+"\tBit->:"
                +Long.toBinaryString(NS_MASK));

        //SP_MASK                                                       11111111111111111111111111111111
        //SP_MASK                       1111111111111111111111111111111100000000000000000000000000000000
        //TC_UNIT:	4294967296	Bit->:                          1 0000 0000 0000 0000 0000 0000 0000 0000
        //TC_MASK:	281470681743360	Bit->:    1111 1111 1111 1111 0000 0000 0000 0000 0000 0000 0000 0000
        //ADD_WORKER:	140737488355328	Bit->:1000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000
        System.out.println("TC_UNIT:\t"+TC_UNIT+"\tBit->:"
                +Long.toBinaryString(TC_UNIT));
        System.out.println("TC_MASK:\t"+TC_MASK+"\tBit->:"
                +Long.toBinaryString(TC_MASK));
        System.out.println("ADD_WORKER:\t"+ADD_WORKER+"\tBit->:"
                +Long.toBinaryString(ADD_WORKER));

        //ForkJoinPool
        //SP_MASK
        //1111111111111111111111111111111100000000000000000000000000000000

        //SP_MASK/UC_MASK 0XFFFFFFFFL / UC_MASK= ~SP_MASK;

        //                                 1111 1111 1111 1111 1111 1111 1111 1111
        //11111111111111111111111111111111 0000 0000 0000 0000 0000 0000 0000 0000

        //RC 48 RC_UNIT= 0x0001L << RC_SHIFT; / RC_MASK= 0xffffL << RC_SHIFT;
        //                  1 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000
        //1111 1111 1111 1111 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000

        //TC 32 TC_UNIT= 0x0001L << TC_SHIFT; / TC_MASK= 0xffffL << TC_SHIFT;
        //                                      1 0000 0000 0000 0000 0000 0000 0000 0000
        //                    1111 1111 1111 1111 0000 0000 0000 0000 0000 0000 0000 0000
        //ADD_WORKER 47 ADD_WORKER = 0x0001L << (TC_SHIFT + 15);
        //                    1000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000 0000

        System.out.println("RC_UNIT:\t"+RC_UNIT+"\tBit->:"
                +Long.toBinaryString(RC_UNIT));
        System.out.println("RC_MASK:\t"+RC_MASK+"\tBit->:"
                +Long.toBinaryString(RC_MASK));

        //1111 1111 1111 1111 1111 1111 1111 1111
        System.out.println("SP_MASK:\t"+SP_MASK+"\tBit->:"
                +Long.toBinaryString(SP_MASK));
        System.out.println("UC_MASK:\t"+UC_MASK+"\tBit->:"
                +Long.toBinaryString(UC_MASK));

        //////////////////////
        System.out.println("UNSIGNALLED:\t"+UNSIGNALLED+"\tBit->:"
                +Integer.toBinaryString(UNSIGNALLED));

        //  1111  1111  1111  1111
        //   111  1111  1111  1111
        System.out.println("SMASK:\t"+SMASK+"\tBit->:"
                +Integer.toBinaryString(SMASK));
        System.out.println("MAX_CAP:\t"+MAX_CAP+"\tBit->:"
                +Integer.toBinaryString(MAX_CAP));
        System.out.println("SS_SEQ:\t"+SS_SEQ+"\tBit->:"
                +Integer.toBinaryString(SS_SEQ));
        System.out.println("FIFO:\t"+FIFO+"\tBit->:"
                +Integer.toBinaryString(FIFO));
        System.out.println("SRC:\t"+SRC+"\tBit->:"
                +Integer.toBinaryString(SRC));


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
