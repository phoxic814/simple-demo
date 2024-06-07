package com.simple.async;

import org.junit.jupiter.api.Test;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class AsyncTest {

    private final ThreadPoolTaskExecutor executor = AsyncConfig.taskExecutor();

    @Test
    public void test() throws InterruptedException {
        ThreadContext.set(UUID.randomUUID().toString());
        ttt();

        Thread.sleep(2000);
        System.out.println("job execute id: " + ThreadContext.get());
    }

    private void ttt() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            return "Hello World";
        });

        CompletableFuture<Void> news = CompletableFuture.runAsync(() -> {
        });

        process(news, "news");

        System.out.println("job execute id: " + ThreadContext.get());
    }

    public void process(CompletableFuture<?> future, String futureName) {
        future.thenRunAsync(() -> {
                    System.out.println("kafka send: " + ThreadContext.get());
                    System.out.println("threadId:" + Thread.currentThread().getId() + ", name:" + futureName);
                }, executor)
                .exceptionally(e -> {
                    System.out.println("kafka send error: " + e.getMessage());
                    return null;
                })
                .thenRunAsync(() -> {
                    System.out.println("order process: " + ThreadContext.get());
                    System.out.println("threadId:" + Thread.currentThread().getId() + ", name:" + futureName);
                }, executor)
                .exceptionally(e -> {
                    System.out.println("error: " + e.getMessage());
                    return null;
                });
    }
}
