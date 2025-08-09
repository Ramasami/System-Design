package org.example.streams;

public class StreamApplication {

    public static void main(String[] args) {

        StreamsUtils.stream(new int[]{1, 2, 3, 4, 5})
                .println();
    }
}
