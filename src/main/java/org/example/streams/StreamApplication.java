package org.example.streams;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class StreamApplication {

    public static void main(String[] args) {

        List<String> names = List.of("Alice", "Bob", "Charlie", "David", "Eve");
        List<String> x = StreamsUtils.streams(names)
                .sorted(Comparator.comparingInt(String::length))
                .peek(System.out::println)
                .filter(name -> name.startsWith("A") || name.startsWith("B"))
                .map(String::toUpperCase)
                .flatMap(name -> StreamsUtils.streams(Arrays.asList(name.split(""))).map(String::toLowerCase))
                .skip(1)
                .limit(100)
                .distinct()
                .toList();
        System.out.println(x);

        StreamsUtils.streams(names)
                .sorted(Comparator.comparingInt(String::length))
                .flatMap(name -> StreamsUtils.streams(Arrays.asList(name.split(""))).map(String::toLowerCase))
                .forEach(System.out::println);


        System.out.println(StreamsUtils.streams(names)
                .sorted(Comparator.comparingInt(String::length))
                .peek(System.out::println)

                        .noneMatch(name -> name.startsWith("A") || name.startsWith("B")));



    }
}
