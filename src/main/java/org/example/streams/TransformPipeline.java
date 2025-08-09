package org.example.streams;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collection;
import java.util.function.Function;

@Getter
@AllArgsConstructor
public class TransformPipeline<IN, OUT> {

    private final Function<Collection<IN>, Collection<OUT>> function;

    @SuppressWarnings("unchecked")
    public Collection<OUT> apply(Collection<?> collection) {
        return function.apply((Collection<IN>) collection);
    }
}
