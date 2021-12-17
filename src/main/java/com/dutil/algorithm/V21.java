package com.dutil.algorithm;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author yangjiandong
 * @date 2021/5/8
 */
public class V21 {

    public static void main(String[] args) {

    }



    /**
     * 三数3 (未做去重)
     * @param nums
     * @return
     */
    public List<List<Integer>> threeSum(int[] nums) {
        List<Integer> arrayList = new ArrayList<>();
        for (int num : nums) {
            arrayList.add(num);
        }
        arrayList.sort(Comparator.comparing(Integer::intValue));


        List<List<Integer>> result = new ArrayList<>();
        if (arrayList.size() > 2) {
            int t1 = arrayList.get(0) -1;
            int t2 = t1;
            int t3 = t2;
            for (int i = 0; i < arrayList.size() - 2; i++) {
                Integer ai = arrayList.get(i);
                if (ai > 0) {
                    break ;
                }

                for (int j = i + 1; j < arrayList.size() - 1; j++) {
                    Integer aj = arrayList.get(j);
                    if (ai + aj > 0) {
                        break ;
                    }

                    for (int k = j + 1; k < arrayList.size(); k++) {
                        Integer ak = arrayList.get(k);
                        int sum = ai + aj + ak;
                        if (sum > 0) {
                            break;
                        }

                        if (sum == 0) {
                            if (ai == t1 && aj == t2 && ak == t3) {
                                continue ;
                            } else {
                                ArrayList<Integer> one = new ArrayList<>();
                                one.add(ai);
                                one.add(aj);
                                one.add(ak);
                                t1 = ai;
                                t2 = aj;
                                t3 = ak;
                                result.add(one);
                            }
                        }
                    }
                }
            }
        }

        return result;
    }
}
