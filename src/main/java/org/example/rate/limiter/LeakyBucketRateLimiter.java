package org.example.rate.limiter;

import lombok.SneakyThrows;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

public class LeakyBucketRateLimiter implements IRateLimiter {

    private final int rate;
    private final TimeUnit timeUnit;
    private final int bucketSize;

    private final Queue<Supplier<String>> linkedList;
    private final Thread deamonThread;

    public LeakyBucketRateLimiter(int rate, TimeUnit timeUnit, int bucketSize) {
        this.rate = rate;
        this.timeUnit = timeUnit;
        this.bucketSize = bucketSize;
        this.linkedList = new LinkedList<>();
        this.deamonThread = new Thread(this::addTokens);
        deamonThread.setDaemon(true);
        deamonThread.start();
    }

    @Override
    public synchronized void process(Supplier<String> runnable) {
        if (linkedList.size() == bucketSize) {
            System.out.println("current: " + linkedList.size() + " message: ignored");
        } else {
            linkedList.add(runnable);
        }
    }

    @SneakyThrows
    private void addTokens() {
        while (true) {
            for (int i = 0; i < rate && !linkedList.isEmpty(); i++) {
                System.out.println("current: " + linkedList.size() + " message: " + linkedList.remove().get());
            }
            timeUnit.sleep(1);
        }
    }


}
