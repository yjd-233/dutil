package com.dutil.sort;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Pageable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author yangjiandong
 * @date 2020/6/9
 */
@Slf4j
public class PageSortUtil {

    @Data
    @NoArgsConstructor
    public static class PageResult<T> {

        /**
         * 总条数
         */
        @JSONField(name = "total")
        private Long total;
        /**
         * 数据列表
         */
        @JSONField(name = "content")
        private List<T> resultList;

    }

    public static <T> PageResult<T> list2Page(List<T> list, Pageable pageable) {
        PageResult pageResult = new PageResult<>();
        int start = (int)pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), list.size());
        pageResult.setResultList(list.subList(start, end));
        pageResult.setTotal((long) list.size());
        return pageResult;
    }

    /**
     *
     * @param list         需要排序的数组
     * @param field        驼峰类型的字段
     * @param direction    DESC 为降序 ASC 为升序
     * @param clazz        排序对象Class
     * @param <T>
     * @return
     */
    public static <T> List<T> compareList(List<T> list, String field, String direction, Class<T> clazz) {
        list.sort((o1, o2) -> compare(o1, o2, field, direction, clazz));
        return list;
    }

    public static int compare(Object o1, Object o2, String field, String direction, Class clazz) {
        try {
            int op = "ASC".equals(direction)? 1 : -1;
            Method method = clazz.getMethod(getMethodName(field));
            Object o1Value = method.invoke(o1);
            Object o2Value = method.invoke(o2);
            if (o1Value == null && o2Value == null) {
                return 0;
            } else if (o1Value == null){
                return op;
            } else if (o2Value == null){
                return -1 * op;
            }
            Comparable c1 = (Comparable)o1Value;
            Comparable c2 = (Comparable)o2Value;
            return c1.compareTo(c2) * op;
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            log.error("Field2Method error", e);
        }
        return 0;
    }

    /**
     * value to getValue
     * @param field
     * @return
     */
    public static String getMethodName(String field) throws NullPointerException {
        if (StringUtils.isBlank(field)) {
            throw new NullPointerException("sort list field is blank!");
        }
        field = field.substring(0, 1).toUpperCase() + field.substring(1);
        return "get" + field;
    }

    public static void main(String[] args) {
        System.out.println(getMethodName("createTime"));
    }

}

