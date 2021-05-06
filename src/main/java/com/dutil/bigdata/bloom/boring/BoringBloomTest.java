package com.dutil.bigdata.bloom.boring;

import com.dutil.bigdata.bloom.boring.memory.*;
/**
 * @author yangjiandong
 * @date 2021/5/6
 */
public class BoringBloomTest {

    public static void main(String[] args) {
        BloomFilterFile bloomFilterFile = new BloomFilterFile(4, 86400000,
                1, 1200000,
                10000000, 0.01, "./",
                false);


        System.out.println("check1 + " + bloomFilterFile.addAndCheck("abc"));
        System.out.println("check2 + " + bloomFilterFile.addAndCheck("abc"));
        System.out.println("check3 + " + bloomFilterFile.addAndCheck("abc"));
    }
}
