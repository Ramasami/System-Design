package org.example.streams;

import java.util.Collection;
import java.util.function.Function;

public record TransformPipeline<IN, OUT>(Function<Collection<IN>, Collection<OUT>> function) {

    @SuppressWarnings("unchecked")
    public Collection<OUT> apply(Collection<?> collection) {
        return function.apply((Collection<IN>) collection);
    }
}
