package com.mywebsite.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Configuration
@EnableScheduling
public class BackgroundJobsConfig {

    static Logger log = LoggerFactory.getLogger(BackgroundJobsConfig.class.getName());

    @Bean
    ExecutorService executor() {
        ExecutorService executorService = Executors.newFixedThreadPool(30);
        return executorService;
    }

    @Scheduled(fixedDelay = 60*1000, initialDelay = 5*1000)
    public void doSomething() throws InterruptedException {
        log.info("background job");
    }
}
