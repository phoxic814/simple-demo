package com.simple.async;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

public class AsyncConfig {

    public static ThreadPoolTaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor poolTaskExecutor = new ThreadPoolTaskExecutor();
        // 线程名称前缀
        poolTaskExecutor.setThreadNamePrefix("executor-");
        // 核心线程数
        poolTaskExecutor.setCorePoolSize(4);
        // 最大线程数
        poolTaskExecutor.setMaxPoolSize(8);
        // 设置线程保活时间（秒）
        poolTaskExecutor.setKeepAliveSeconds(120);
        // 设置任务队列容量
        poolTaskExecutor.setQueueCapacity(100);

        // 设置线程任务装饰器，完成异步线程或跨线程/父子线程间的租户数据源上下文值的传递
        poolTaskExecutor.setTaskDecorator(new MyContextDecorator());

        // 拒绝策略
        poolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        // 等待所有任务结束后再关闭线程池
        poolTaskExecutor.setWaitForTasksToCompleteOnShutdown(true);

        poolTaskExecutor.initialize();
        return poolTaskExecutor;
    }
}
