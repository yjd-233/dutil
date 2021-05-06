package com.dutil.bigdata.bloom.guava;

import com.google.common.hash.BloomFilter;
import com.google.common.hash.Funnels;

import java.nio.charset.Charset;

/**
 * @author yangjiandong
 * @date 2021/5/6
 */
public class GuavaBloomTest {

    public static void main(String[] args) {
        String key = "abc";
        BloomFilter<CharSequence> bf = BloomFilter.create(Funnels.stringFunnel(), 100_0000, 0.001);
        System.out.println(bf.mightContain(key));
        bf.put(key);
        System.out.println(bf.mightContain(key));
    }
}
