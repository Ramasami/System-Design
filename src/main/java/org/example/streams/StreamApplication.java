package org.example.streams;

import java.util.*;

public class StreamApplication {

    public static void main(String[] args) {

        List<String> names = List.of("Alice", "Bob", "Charlie", "David", "Eve");
        Object x = StreamsUtils.stream(names)
                .sorted(Comparator.comparingInt(String::length))
//                .peek(System.out::println)
//                .filter(name -> name.startsWith("A") || name.startsWith("B"))
                .map(String::toUpperCase)
//                .flatMap(name -> StreamsUtils.streams(Arrays.asList(name.split(""))).map(String::toLowerCase))
                .skip(1)
                .limit(100)
                .distinct()
                .peek(System.out::println)
                .joining("###");
        System.out.println(x);
    }
}
