package org.example.streams;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

public class ReducePipeline<IN, OUT> {
    private final List<TransformPipeline<?, ?>> transformPipelines;
    private final Function<Collection<IN>, OUT> reduceFunction;
    private final Stream<IN> stream;

    public ReducePipeline(List<TransformPipeline<?, ?>> transformPipelines, Function<Collection<IN>, OUT> reduceFunction, Stream<IN> stream) {
        this.transformPipelines = transformPipelines;
        this.reduceFunction = reduceFunction;
        this.stream = stream;
    }

    @SuppressWarnings("unchecked")
    public OUT reduce(Collection<?> collection) {
        for (TransformPipeline<?, ?> pipeline : transformPipelines) {
            collection = pipeline.apply(collection);
        }
        OUT result = reduceFunction.apply((Collection<IN>) collection);
        stream.close();
        return result;
    }
}
