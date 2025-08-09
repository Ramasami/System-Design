package org.example.rate.limiter;

import lombok.SneakyThrows;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class RateLimiterApp {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(200);

    @SneakyThrows
    public static void main(String[] args) {

        IRateLimiter rateLimiter;

//        rateLimiter = new TokenBucketRateLimiter(3, TimeUnit.SECONDS, 5);
//        rateLimiter = new LeakyBucketRateLimiter(3, TimeUnit.SECONDS, 5);
        rateLimiter = new FixedWidthRateLimiter(TimeUnit.SECONDS, 5);

        for (int i = 0; i < 100; i++) {
            Thread.sleep(10);
            long tic = System.currentTimeMillis();
            executorService.submit(() -> rateLimiter.process(() -> {
                long toc = System.currentTimeMillis();
                return ((tic / 100) + ", total time: " + (toc - tic));
            }));
        }
        executorService.shutdown();
    }
}
