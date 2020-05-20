package com.victo.dtool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

/**
 * @author yangjiandong
 * @date 2020/4/29
 */
@EnableCaching
@SpringBootApplication
public class DToolApplication {
    public static void main(String[] args) {
        SpringApplication.run(DToolApplication.class, args);
    }
}
