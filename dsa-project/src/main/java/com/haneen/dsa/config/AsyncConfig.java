package com.haneen.dsa.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
public class AsyncConfig {
    @Bean(name="judgeExecutor")
    public Executor judgeExecutor(){
        ThreadPoolTaskExecutor ex = new ThreadPoolTaskExecutor();
        ex.setCorePoolSize(2);      // threads kept alive
        ex.setMaxPoolSize(4);       // hard ceiling
        ex.setQueueCapacity(50);    // THIS is your queue
        ex.setThreadNamePrefix("judge-");
        ex.initialize();
        return ex;
    }
}
