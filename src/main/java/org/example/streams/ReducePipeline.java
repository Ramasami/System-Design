package org.example.streams;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class ReducePipeline<OUT> {
    private final List<TransformPipeline<?, ?>> transformPipelines;
    private final Function<Collection<?>, OUT> reduceFunction;

    public ReducePipeline(List<TransformPipeline<?, ?>> transformPipelines, Function<Collection<?>, OUT> reduceFunction) {
        this.transformPipelines = transformPipelines;
        this.reduceFunction = reduceFunction;
    }

    public OUT reduce(Collection<?> collection) {
        for (TransformPipeline<?, ?> pipeline : transformPipelines) {
            collection = pipeline.apply(collection);
        }
        return reduceFunction.apply(collection);
    }
}
