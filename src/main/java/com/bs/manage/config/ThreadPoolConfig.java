package com.bs.manage.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * {@code @description}
 * 自定义线程池
 *
 * @author fangzhijian
 * @since 2025-11-26 14:59
 */
@Configuration
public class ThreadPoolConfig {

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        int cpuCoreNum = Runtime.getRuntime().availableProcessors();
        return new ThreadPoolExecutor(
                cpuCoreNum * 2,
                cpuCoreNum * 4,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(100),
                new CustomThreadFactory("business-pool"),
                //线程池满时，让提交任务的线程自己执行任务
                new ThreadPoolExecutor.CallerRunsPolicy()
        );
    }

}
