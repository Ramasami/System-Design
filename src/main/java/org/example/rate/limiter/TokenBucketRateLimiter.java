package org.example.rate.limiter;

import lombok.SneakyThrows;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

public class TokenBucketRateLimiter implements IRateLimiter {

    private final int rate;
    private final TimeUnit timeUnit;
    private final int bucketSize;

    private final AtomicInteger currentTokens;
    private final Thread deamonThread;

    public TokenBucketRateLimiter(int rate, TimeUnit timeUnit, int bucketSize) {
        this.rate = rate;
        this.timeUnit = timeUnit;
        this.bucketSize = bucketSize;
        this.currentTokens = new AtomicInteger(0);
        this.deamonThread = new Thread(this::addTokens);
        deamonThread.setDaemon(true);
        deamonThread.start();
    }

    public synchronized boolean tryAcquire() {
        System.out.print("current: " + currentTokens.get() + " message: ");
        if (currentTokens.get() > 0) {
            currentTokens.decrementAndGet();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public void process(Supplier<String> runnable) {
        if (tryAcquire())
            System.out.println(runnable.get());
        else {
            System.out.println("ignored");
        }
    }

    @SneakyThrows
    private void addTokens() {
        while (true) {
            currentTokens.updateAndGet(currentToken -> Math.min(bucketSize, currentToken + rate));
            timeUnit.sleep(1);
        }
    }
}
