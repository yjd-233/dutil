package com.dutil.spring;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;

/**
 * @author yangjiandong
 * @date 2021/5/12
 */
public class EventListener implements ApplicationListener<ApplicationContextEvent> {
    @Override
    public void onApplicationEvent(ApplicationContextEvent applicationContextEvent) {
        System.out.println(applicationContextEvent);
    }
}
