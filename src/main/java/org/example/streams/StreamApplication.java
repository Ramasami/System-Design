package org.example.streams;

import java.util.Random;

public class StreamApplication {

    public static void main(String[] args) {

        StreamsUtils.stream(new int[]{1, 2, 3, 4, 5})
                .parallel()
                .peek(x-> {
                    try {
                        Thread.sleep(new Random().nextInt(1000));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                })
                .println();
    }
}
