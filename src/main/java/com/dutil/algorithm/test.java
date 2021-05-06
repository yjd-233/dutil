package com.dutil.algorithm;

import java.util.ArrayList;
import java.util.List;

/**
 * @author yangjiandong
 * @date 2020/6/30
 */
public class test {



    public static void main(String[] args) {
        test test = new test();
        System.out.println(test.longestCommonPrefix(new String []{"aa", "a"}));
    }

    public int compareVersion(String version1, String version2) {
        String[] a1 = version1.split("\\.");
        String[] a2 = version2.split("\\.");

        for(int n = 0; n < Math.max(a1.length, a2.length); n++){
            int i = (n < a1.length ? Integer.valueOf(a1[n]) : 0);
            int j = (n < a2.length ? Integer.valueOf(a2[n]) : 0);
            if(i < j) return -1;
            else if(i > j) return 1;
        }
        return 0;
    }

    public String longestCommonPrefix(String[] strs) {
        if (strs.length == 0) {
            return "";
        }
        if (strs.length == 1) {
            return strs[0];
        }

        boolean flag = true;
        int count = 0;
        while (flag) {
            if (strs[0].length() > count) {
                char c = strs[0].charAt(count);
                for (int i = 1; i < strs.length; i++) {
                    if (strs[i].length() <= count || strs[i].charAt(count) != c) {
                        flag = false;
                    }
                }
            } else {
                flag = false;
            }
            count++;
        }
        return strs[0].substring(0, count - 1);
    }

    public int maxArea(int[] height) {
        int max = 0;
        for (int i = 0; i < height.length - 1; i++) {
            for (int j = i + 1; j < height.length; j++) {
                max = Math.max((j - i) * Math.min(height[i], height[j]), max);
            }
        }
        return max;
    }

    /**
     * 回文数
     * @param x
     * @return
     */
    public boolean isPalindrome(int x) {
        if (x < 0) {
            return false;
        }
        if (reverse(x) == x) {
            return true;
        }
        return false;
    }

    /**
     * 字符串转整数
     * @param str
     * @return
     */
    public int myAtoi(String str) {
        int res = 0;
        String trim = str.trim();
        if (trim.equals("")) {
            return 0;
        }
        char[] chars = trim.toCharArray();
        if (chars[0] == '-') {
            for (int i = 1; i < chars.length; i++) {
                if (isNum(chars[i])) {
                    if (res < Integer.MIN_VALUE / 10) {
                        return Integer.MIN_VALUE;
                    }
                    res = res * 10;
                    if (res < Integer.MIN_VALUE + Character.getNumericValue(chars[i])) {
                        return Integer.MIN_VALUE;
                    }
                    res = res - Character.getNumericValue(chars[i]);
                } else {
                    break;
                }
            }
        } else if (chars[0] == '+') {
            for (int i = 1; i < chars.length; i++) {
                if (isNum(chars[i])) {
                    if (res > Integer.MAX_VALUE / 10) {
                        return Integer.MAX_VALUE;
                    }
                    res = res * 10;
                    if (res > Integer.MAX_VALUE - Character.getNumericValue(chars[i])) {
                        return Integer.MAX_VALUE;
                    }
                    res = res + Character.getNumericValue(chars[i]);
                } else {
                    break;
                }
            }
        } else if (isNum(chars[0])) {
            for (int i = 0; i < chars.length; i++) {
                if (isNum(chars[i])) {
                    if (res > Integer.MAX_VALUE / 10) {
                        return Integer.MAX_VALUE;
                    }
                    res = res * 10;
                    if (res > Integer.MAX_VALUE - Character.getNumericValue(chars[i])) {
                        return Integer.MAX_VALUE;
                    }
                    res = res + Character.getNumericValue(chars[i]);
                } else {
                    break;
                }
            }
        }
        return res;
    }
    public static boolean isNum(char c) {
        if (c == '0' || c == '1' || c == '2' || c == '3' || c == '4'
                || c == '5' || c == '6' || c == '7' || c == '8' || c == '9' ) {
            return true;
        }
        return false;
    }
    /**
     * 整数反转 判断溢出
     * @param x
     * @return
     */
    public static int reverse(int x) {
        boolean flag = true;
        if (x < 0) {
            flag = false;
            x = -x;
        }

        int r = x % 10;
        while (x / 10 != 0) {
            x = x / 10;
            if (Integer.MAX_VALUE / 10 < r || Integer.MAX_VALUE - r * 10 < x % 10) {
                r = 0;
                break;
            }
            r = r * 10 + x % 10;
        }
        if (!flag) {
            r = -r;
        }
        return r;
    }

    /**
     * Z 字形变换
     * @param s
     * @param numRows
     * @return
     */
    public String convert(String s, int numRows) {
        if (numRows <= 1) {
            return s;
        }

        List<List<Character>> strList = new ArrayList<>();

        for (int i = 0; i < numRows; i++) {
            strList.add(new ArrayList<>());
        }

        char[] chars = s.toCharArray();
        boolean down = true;
        int rowCount = 0;
        for (int i = 0; i < chars.length; i++) {
            strList.get(rowCount).add(chars[i]);
            if (down) {
                if (rowCount != numRows-1) {
                    rowCount++;
                } else {
                    down = false;
                    rowCount--;
                }
            } else {
                if (rowCount != 0) {
                    rowCount--;
                } else {
                    down = true;
                    rowCount++;
                }
            }
        }
        StringBuilder builder = new StringBuilder(chars.length);
        for (List<Character> characters : strList) {
            for (Character character : characters) {
                builder.append(character);
            }
        }

        return builder.toString();
    }

    /**
     * 最长回文
     * @param s
     * @return
     */
    public String longestPalindrome(String s) {
        if (s.length() < 2) {
            return s;
        }

        char[] chars = s.toCharArray();
        boolean[][] dp = new boolean[chars.length][chars.length];

        int maxLen = 1;
        int begin = 0;

        for (int i = 0; i < chars.length; i++) {
            dp[i][i] = true;
        }

        for (int j = 1; j < chars.length; j++) {
            for (int i = 0; i < j; i++) {
                if (chars[i] != chars[j]) {
                    dp[i][j] = false;
                } else {
                    if (j - i < 3) {
                        dp[i][j] = true;
                    } else {
                        dp[i][j] = dp[i + 1][j - 1];
                    }
                }

                if (dp[i][j] && j - i + 1 > maxLen) {
                    maxLen = j - i + 1;
                    begin = i;
                }
            }
        }
        return s.substring(begin, begin + maxLen);
    }
}
