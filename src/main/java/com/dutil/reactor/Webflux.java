package com.dutil.reactor;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author yangjiandong
 * @date 2021/5/11
 */
public class Webflux {
    public static void main(String[] args) {


//        buffer();
    }

    public static void buffer() {
        Flux.range(1, 10).buffer(3).subscribe(System.out::println);
        System.out.println("-----------");
        Flux.range(1, 10).bufferUntil(i -> i % 2 == 0).subscribe(System.out::println);
        System.out.println("-----------");
        Flux.range(1, 10).bufferWhile(i -> i % 2 == 0).subscribe(System.out::println);
    }

    /**
     * 可以在一次调用中产生多个元素
     */
    public static void create() {
        Flux.create(sink -> {
            for (int i = 0; i < 10; i++) {
                sink.next(i);
            }
            sink.complete();
        }).subscribe(System.out::println);
    }

    /**
     * 只能有一次 next
     */
    public static void generate() {
        Flux.generate(ArrayList::new, (list, sink) -> {
            sink.next(list.size());
            list.add(list.size() + 1);
            if (list.size() == 10) {
                sink.complete();
            }
            return list;
        }).subscribe(System.out::println);

    }
}
