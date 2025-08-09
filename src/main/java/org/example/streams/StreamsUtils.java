package org.example.streams;

import java.util.*;

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
