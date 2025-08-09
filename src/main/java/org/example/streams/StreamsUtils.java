package org.example.streams;

import java.util.Collection;

public class StreamsUtils {

    public static <T> Stream<T> streams(Collection<T> collection) {
        return new Stream<>(collection);
    }
}
