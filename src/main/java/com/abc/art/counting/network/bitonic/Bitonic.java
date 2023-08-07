package com.abc.art.counting.network.bitonic;

public class Bitonic {
    Bitonic[] half; // two half-width bitonic networks
    Merger merger; // final merger layer
    final int width; // network width
    public Bitonic(int myWidth) {
        width = myWidth;
        merger = new Merger(width);
        if (width > 2) {
            half = new Bitonic[]{new Bitonic(width/2),
                    new Bitonic(width/2)};
        }
    }
    public int traverse(int input) {//
        int output = 0;
        //子网络
        int subnet = input / (width / 2);//4
        //4
        if (width > 2) {

            //subnet = input / (width / 2);
            //half[subnet].traverse(input - subnet * (width / 2));
            //with:16 width / 2:8
            //input:0   subnet=0/8=0
            //half[subnet].traverse(0-0*0);

            //subnet = input / (width / 2);
            //half[subnet].traverse(input - subnet * (width / 2));
            //with:16 width / 2:8
            //input:1   subnet=1/8=0
            //half[0].traverse(1-0*0);

            //input:2   subnet=2/8=0
            //half[0].traverse(2-0*0);
            //half[0].traverse(3-0*0);


            //half[0].traverse(0 - 0);

            //width:8 input=1 subnet=0
            //half[0].traverse(1);

            //width:8 input=2 subnet=0
            //half[0].traverse(2);

            //width:8 input=3 subnet=0
            //half[0].traverse(3);

            //width:8 input=4 subnet=1
            //half[1].traverse(0);

            //width:8 input=5 subnet=1
            //half[1].traverse(1);

            //width:8 input=9 subnet=2
            //half[1].traverse(1);

            output = half[subnet].traverse(input - subnet * (width / 2));
        }
        return merger.traverse(output + subnet * (width / 2));
    }
}
