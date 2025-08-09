package org.example.streams;

import lombok.AllArgsConstructor;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@AllArgsConstructor
public class SimpleFuture<T> implements AbstractStreamFuture<T> {

    private final T data;

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return true;
    }

    @Override
    public T get() {
        return data;
    }

    @Override
    public T get(long timeout, TimeUnit unit) {
        return data;
    }
}
