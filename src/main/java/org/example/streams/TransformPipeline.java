package org.example.streams;

import java.util.Collection;
import java.util.function.Function;

public class TransformPipeline<IN, OUT> {

    private final Function<Collection<IN>, Collection<OUT>> function;

    public TransformPipeline(Function<Collection<IN>, Collection<OUT>> function) {
        this.function = function;
    }

    @SuppressWarnings("unchecked")
    public Collection<OUT> apply(Collection<?> collection) {
        return function.apply((Collection<IN>) collection);
    }
}
