package com.springboot.demo.config.threadpool;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * {@code @description}
 * 自定义线程工厂
 *
 * @author fangzhijian
 * @since 2025-11-26 15:02
 */
public class CustomThreadFactory implements ThreadFactory {

    // 线程池名称前缀（区分不同线程池）
    private final String prefix;
    // 线程计数器（原子类，避免并发问题）
    private final AtomicInteger threadNum = new AtomicInteger(1);

    public CustomThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public Thread newThread(Runnable r) {
        Thread thread = new Thread(r);
        // 线程名称：prefix-线程编号（如 "order-pool-1"）
        thread.setName(prefix + "-thread-" + threadNum.getAndIncrement());
        // 非守护线程（守护线程会随主线程退出，避免任务未执行完）
        thread.setDaemon(false);
        // 线程优先级（默认 5，范围 1-10）
        thread.setPriority(Thread.NORM_PRIORITY);
        return thread;
    }
}
