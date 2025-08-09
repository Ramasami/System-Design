package org.example.rate.limiter;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class FixedWidthRateLimiter implements IRateLimiter {

    private final TimeUnit timeUnit;
    private final int bucketSize;

    private final Thread deamonThread;
    private final AtomicInteger utilised;

    public FixedWidthRateLimiter(TimeUnit timeUnit, int bucketSize) {
        this.timeUnit = timeUnit;
        this.bucketSize = bucketSize;
        this.utilised = new AtomicInteger(0);
        this.deamonThread = new Thread(this::addTokens);
        deamonThread.setDaemon(true);
        deamonThread.start();
    }

    @Override
    public void process(Supplier<String> runnable) {
        int curr = utilised.getAndUpdate(size -> Math.max(size - 1, 0));
        if (curr > 0)
            System.out.println("current: " + curr + " message: " + runnable.get());
        else
            System.out.println("current: " + curr + " message: ignored");
    }

    @SneakyThrows
    private void addTokens() {
        while (true) {
            utilised.set(bucketSize);
            timeUnit.sleep(1);
        }
    }


}
