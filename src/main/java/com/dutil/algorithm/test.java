package com.dutil.algorithm;

import java.util.HashSet;
import java.util.Set;

/**
 * @author yangjiandong
 * @date 2020/6/30
 */
public class test {
    public double findMedianSortedArrays(int[] nums1, int[] nums2) {


        int len = nums1.length + nums2.length;
        boolean one = (nums1.length + nums2.length) % 2 != 0;
        int half = (nums1.length + nums2.length) / 2;

        return 0;
    }

    public int lengthOfLongestSubstring(String s) {
        int max = 0;
        Set<Character> set = new HashSet<>();
        char[] chars = s.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (set.contains(c)) {
                set = new HashSet<>();
                for (int j = i - 1; j >= 0; j--) {
                    if (c == chars[j]) {
                        i =  j;
                        break;
                    }
                }
                continue;
            }
            set.add(c);
            max = Math.max(max, set.size());
        }
        return max;
    }

    public static void main(String[] args) {
        System.out.println(5/2);
    }
}
