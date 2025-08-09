package org.example.streams;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class ReducePipeline<IN, OUT> {
    private final List<TransformPipeline<?, ?>> transformPipelines;
    private final Function<Collection<IN>, OUT> reduceFunction;

    public ReducePipeline(List<TransformPipeline<?, ?>> transformPipelines, Function<Collection<IN>, OUT> reduceFunction) {
        this.transformPipelines = transformPipelines;
        this.reduceFunction = reduceFunction;
    }

    @SuppressWarnings("unchecked")
    public OUT reduce(Collection<?> collection) {
        for (TransformPipeline<?, ?> pipeline : transformPipelines) {
            collection = pipeline.apply(collection);
        }
        return reduceFunction.apply((Collection<IN>) collection);
    }
}
