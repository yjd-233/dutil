package com.dutil.cache;

import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class Cache {

    public Cache() {
        init();
    }

    public static LoadingCache<String, String> cache;

    public void init() {
        CacheLoader<String, String> dataLoader = Cache::setValue;
        cache = Caffeine.newBuilder()
                .maximumSize(100)
                .expireAfterWrite(5, TimeUnit.SECONDS)
                .build(dataLoader);
    }

    public static String setValue(String key) {
        log.info("nocache: " + key);
        return key + System.currentTimeMillis();
    }

    public static void main(String[] args) throws InterruptedException {
        Cache cache = new Cache();
        String key = "abc";
        while (true) {
            Thread.sleep(1000);
            log.info("key: {}, value: {}", key, Cache.cache.get(key));
        }
    }

}
