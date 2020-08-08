package com.dutil.collection;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yangjiandong
 * @date 2020/8/7
 */
public class Stream {

    public void stream() {

    }

    public void list() {
        List<String> list = Lists.newArrayList("a", "b", "c");
        //数组转字符串
        String collect0 = list.stream().collect(Collectors.joining(","));
        String collect1 = Joiner.on(",").join(list);
        String collect2 = StringUtils.join(list.toArray(), ",");
        //字符串转数组
        List<String> list1 = Arrays.asList(collect0.split(","));
    }



    public static void main(String[] args) {
        List<String> list = Lists.newArrayList("a", "b", "c");

        //逗号隔开
        String collect = Joiner.on(",").join(list);

        System.out.println(collect);

    }

}
