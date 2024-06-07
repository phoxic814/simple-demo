package com.simple.async;

import org.springframework.core.task.TaskDecorator;

/**
 * 裝飾氣, 用於threadPool間數據交換
 */
public class MyContextDecorator implements TaskDecorator {

    @Override
    public Runnable decorate(Runnable runnable) {
        String traceId = ThreadContext.get();
//        System.out.println("TaskDecorator traceId:" + traceId);
        System.out.println("TaskDecorator threadId:" + Thread.currentThread().getId());
        return () -> {
            try {
                // 2、将主线程的traceId，设置到子线程的本地线程变量中
                ThreadContext.set(traceId);
                System.out.println("TaskDecorator threadId:" + Thread.currentThread().getId());
                // 执行子线程
                runnable.run();
            } finally {
                // 3、子线程结束，清空子线程的本地线程变量中dataSourceName
                ThreadContext.clear();
            }
        };
    }
}
