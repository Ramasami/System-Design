package org.example.streams;

import lombok.Getter;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

@Getter
public record ReducePipeline<IN, OUT>(List<TransformPipeline<?, ?>> transformPipelines,
                                      Function<Collection<IN>, OUT> reduceFunction, Stream<IN> stream) {

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