package org.example.streams;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

public interface AbstractStreamFuture<T> extends Future<T> {

    @Override
    T get();

    @Override
    T get(long timeout, TimeUnit unit);
}
