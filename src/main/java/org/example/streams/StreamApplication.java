package org.example.streams;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class StreamApplication {

    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(0);
        StreamsUtils.subscriberStream(() -> {
                    try {
                        Thread.sleep(new Random().nextInt(100));
                        return atomicInteger.incrementAndGet();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }, 100)
                .forEach(System.out::println);


        for (Iterator<String> it = new Stream<>(Arrays.asList("apple", "banana", "cherry")).iterator(); it.hasNext(); ) {
            String fruit = it.next();
            System. out. println(fruit);
        }
    }
}
