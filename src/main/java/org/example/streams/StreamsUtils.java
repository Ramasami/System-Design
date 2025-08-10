package org.example.streams;

import java.util.*;
import java.util.function.Supplier;

public class StreamsUtils {

    public static <T> Stream<T> stream(Collection<T> collection) {
        if (collection == null) {
            throw new IllegalArgumentException("Collection cannot be null");
        }
        return new Stream<>(collection);
    }

    public static <T> Stream<T> stream(T[] objects) {
        if (objects == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        return new Stream<>(Arrays.asList(objects));
    }

    public static Stream<Integer> stream(int[] objects) {
        if (objects == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        List<Integer> objectList = new ArrayList<>(objects.length);
        for (int object : objects) {
            objectList.add(object);
        }
        return stream(objectList);
    }

    public static Stream<Integer> intStream(int endExclusive) {
        if (endExclusive < 0) {
            throw new IllegalArgumentException("End must be non-negative");
        }
        return intRangeStream(0, endExclusive);
    }

    public static Stream<Integer> intRangeStream(int startInclusive, int endExclusive) {
        if (startInclusive >= endExclusive) {
            throw new IllegalArgumentException("Start must be less than end");
        }
        List<Integer> rangeList = new ArrayList<>(endExclusive - startInclusive);
        for (int i = startInclusive; i < endExclusive; i++) {
            rangeList.add(i);
        }
        return stream(rangeList);
    }

    public static Stream<Long> longStream(int endExclusive) {
        if (endExclusive < 0) {
            throw new IllegalArgumentException("End must be non-negative");
        }
        return longRangeStream(0, endExclusive);
    }

    public static Stream<Long> longRangeStream(int startInclusive, int endExclusive) {
        if (startInclusive >= endExclusive) {
            throw new IllegalArgumentException("Start must be less than end");
        }
        List<Long> rangeList = new ArrayList<>(endExclusive - startInclusive);
        for (long i = startInclusive; i < endExclusive; i++) {
            rangeList.add(i);
        }
        return stream(rangeList);
    }

    public static <T> Stream<T> subscriberStream(Supplier<T> supplier, int size) {
        if (supplier == null) {
            throw new IllegalArgumentException("Consumer cannot be null");
        }
        return intStream(size)
                .map(i -> supplier.get());
    }

    public static <T> Stream<T> subscriberParallelStream(Supplier<T> supplier, int size) {
        if (supplier == null) {
            throw new IllegalArgumentException("Consumer cannot be null");
        }
        return intStream(size)
                .parallel()
                .map(i -> supplier.get())
                .sequential();
    }

    public static Stream<Long> stream(long[] objects) {
        if (objects == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        List<Long> objectList = new ArrayList<>(objects.length);
        for (long object : objects) {
            objectList.add(object);
        }
        return stream(objectList);
    }

    public static Stream<Double> stream(double[] objects) {
        if (objects == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        List<Double> objectList = new ArrayList<>(objects.length);
        for (double object : objects) {
            objectList.add(object);
        }
        return stream(objectList);
    }

    public static Stream<Boolean> stream(boolean[] objects) {
        if (objects == null) {
            throw new IllegalArgumentException("Array cannot be null");
        }
        List<Boolean> objectList = new ArrayList<>(objects.length);
        for (boolean object : objects) {
            objectList.add(object);
        }
        return stream(objectList);
    }

    public static <K, V> Stream<Stream.Pair<K, V>> stream(Map<K, V> objects) {
        if (objects == null) {
            throw new IllegalArgumentException("Map cannot be null");
        }
        List<Stream.Pair<K, V>> pairs = new ArrayList<>();
        for (Map.Entry<K, V> entry : objects.entrySet()) {
            pairs.add(new Stream.Pair<>(entry.getKey(), entry.getValue()));
        }
        return new Stream<>(pairs);
    }

    public static <T> Stream<Stream.Pair<Long, T>> iteratorStream(Collection<T> collection) {
        if (collection == null) {
            throw new IllegalArgumentException("Collection cannot be null");
        }
        List<Stream.Pair<Long, T>> pairs = new ArrayList<>();
        long index = 0L;
        for (T item : collection) {
            pairs.add(new Stream.Pair<>(index++, item));
        }
        return new Stream<>(pairs);
    }
}
