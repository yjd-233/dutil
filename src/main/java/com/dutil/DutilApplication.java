package com.dutil;

import com.dutil.spring.EventListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author yangjiandong
 * @date 2020/4/29
 */
@EnableScheduling
@EnableCaching
@SpringBootApplication
public class DutilApplication {
    public static void main(String[] args) {
        SpringApplication app = new SpringApplication(DutilApplication.class);
        app.addListeners(new EventListener());
        app.run(args);
    }
}
