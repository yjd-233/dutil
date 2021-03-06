package com.dutil.collection;

import lombok.Data;

import java.util.*;
import java.util.function.Consumer;

/**
 *
 * @author yangjiandong
 * @date 2020/8/18
 */
@Data
public class LoopList<T> {
    private T[] list;

    /**
     * 针对数组 每n个元素为一组传值到action中, 不足n个取到末尾
     * @param n
     * @param action
     */
    public void groupLoop(int n, Consumer<? super T[]> action) {
        boolean flag = true;
        for (int i = 1; flag; i++) {
            T[] exc = list;
            if (list.length <= n * i) {
                if (list.length > n) {
                    exc = Arrays.copyOfRange(list, (i - 1) * n, list.length);
                }
                flag = false;
            } else {
                exc = Arrays.copyOfRange(list, (i - 1) * n, i * n);
            }
            action.accept(exc);
        }
    }

    /**
     * 分页执行action, action的返回参数为总页数
     * @param action
     */
    public static void pageLoop(pageCunsumer action) {
        int page = 0;
        int totalPage = 1;
        while (page++ < totalPage) {
            totalPage = action.accept(page);
        }
    }


    /**
     * 从list中随机取出num个元素
     * @param list
     * @param num
     * @param <K>
     * @return
     */
    public static <K> List<K> randomList(List<K> list, int num) {
        Set<Integer> map = new HashSet();
        List<K> newList = new ArrayList();
        Set<K> set = new HashSet<>(list);
        if (set.size() <= num) {
            return new ArrayList<>(set);
        } else {
            while (newList.size() < num) {
                Random random = new Random();
                int r =random.nextInt(list.size());
                if (!map.contains(r)) {
                    map.add(r);
                    K k = list.get(r);
                    if(k!=null) {
                        newList.add(k);
                    }
                }
            }
        }
        return newList;
    }

    @FunctionalInterface
    public interface pageCunsumer {
        int accept(int page);
    }

    public LoopList(T[] list) {
        this.list = list;
    }


    public static void main(String[] args) {
        Long [] ids = {123L, 456L, 678L, 999L};
        LoopList<Long> loop = new LoopList<>(ids);
        loop.groupLoop(2, s -> System.out.println(Arrays.toString(s)));

        int totalPage = 10;
        LoopList.pageLoop(page -> {
            System.out.println(page);
            return totalPage;
        });
    }
}
