package org.example.streams;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Stream<T> {

    private final Collection<?> collection;
    private final List<TransformPipeline<?, ?>> transformPipelines;

    public Stream(Collection<T> collection) {
        this.collection = collection;
        transformPipelines = new ArrayList<>();
    }

    private <T> Stream(Collection<?> collection, List<TransformPipeline<?, ?>> transformPipelines, TransformPipeline<?, T> transformPipeline) {
        this.collection = collection;
        this.transformPipelines = transformPipelines;
    }

    private List<TransformPipeline<?, ?>> getTransformPipelines() {
        return transformPipelines;
    }

    public Stream<T> filter(Predicate<T> predicate) {
        TransformPipeline<T, T> transformPipeline = new TransformPipeline<>(items -> {
            List<T> filtered = new ArrayList<>();
            for (T item : items) {
                if (predicate.test(item)) {
                    filtered.add(item);
                }
            }
            return filtered;
        });
        return appendPipeline(transformPipeline);
    }

    public List<T> toList() {
        ReducePipeline<List<T>> reducePipeline = new ReducePipeline<>(transformPipelines, items -> new ArrayList<>((Collection<T>) items));
        return reducePipeline.reduce(collection);
    }

    private <U> Stream<U> appendPipeline(TransformPipeline<T, U> transformPipeline) {
        transformPipelines.add(transformPipeline);
        return new Stream<>(collection, transformPipelines, transformPipeline);
    }

    public <U> Stream<U> map(Function<T, U> mapper) {
        TransformPipeline<T, U> transformPipeline = new TransformPipeline<>(items -> {
            List<U> mapped = new ArrayList<>();
            for (T item : items) {
                mapped.add(mapper.apply(item));
            }
            return mapped;
        });
        return appendPipeline(transformPipeline);
    }

    public <U> Stream<U> flatMap(Function<T, Stream<U>> flatMapper) {
        TransformPipeline<T, U> transformPipeline = new TransformPipeline<>(items -> {
            List<U> flatMapped = new ArrayList<>();
            for (T item : items) {
                Stream<U> stream = flatMapper.apply(item);
                flatMapped.addAll(stream.toList());
            }
            return flatMapped;
        });
        return appendPipeline(transformPipeline);
    }

    public Stream<T> peek(Consumer<T> consumer) {
        TransformPipeline<T, T> transformPipeline = new TransformPipeline<>(items -> {
            for (T item : items) {
                consumer.accept(item);
            }
            return items;
        });
        return appendPipeline(transformPipeline);
    }

    public Stream<T> sorted() {
        TransformPipeline<T, T> transformPipeline = new TransformPipeline<>(items -> {
            List<T> sorted = new ArrayList<>(items);
            sorted.sort(null); // Default natural ordering
            return sorted;
        });
        return appendPipeline(transformPipeline);
    }

    public Stream<T> sorted(Comparator<T> comparator) {
        TransformPipeline<T, T> transformPipeline = new TransformPipeline<>(items -> {
            List<T> sorted = new ArrayList<>(items);
            sorted.sort(comparator); // Default natural ordering
            return sorted;
        });
        return appendPipeline(transformPipeline);
    }

    public Stream<T> distinct() {
        TransformPipeline<T, T> transformPipeline = new TransformPipeline<>(items -> {
            List<T> distinct = new ArrayList<>();
            for (T item : items) {
                if (!distinct.contains(item)) {
                    distinct.add(item);
                }
            }
            return distinct;
        });
        return appendPipeline(transformPipeline);
    }

    public Stream<T> skip(int i) {
        TransformPipeline<T, T> transformPipeline = new TransformPipeline<>(items -> {
            List<T> skipped = new ArrayList<>();
            int count = 0;
            for (T item : items) {
                if (count++ >= i) {
                    skipped.add(item);
                }
            }
            return skipped;
        });
        return appendPipeline(transformPipeline);
    }

    public Stream<T> limit(int i) {
        TransformPipeline<T, T> transformPipeline = new TransformPipeline<>(items -> {
            List<T> limited = new ArrayList<>();
            int count = 0;
            for (T item : items) {
                if (count++ < i) {
                    limited.add(item);
                } else {
                    break;
                }
            }
            return limited;
        });
        return appendPipeline(transformPipeline);
    }

    public long count() {
        return new ReducePipeline<>(transformPipelines, items -> (long) items.size()).reduce(collection);
    }

    public T max(Comparator<T> comparator) {
        return new ReducePipeline<>(transformPipelines, items -> {
            if (items.isEmpty()) {
                return null;
            }
            T maxItem = null;
            for (T item : (Collection<T>) items) {
                if (maxItem == null || comparator.compare(item, maxItem) > 0) {
                    maxItem = item;
                }
            }
            return maxItem;
        }).reduce(collection);
    }

    public T min(Comparator<T> comparator) {
        return new ReducePipeline<>(transformPipelines, items -> {
            if (items.isEmpty()) {
                return null;
            }
            T maxItem = null;
            for (T item : (Collection<T>) items) {
                if (maxItem == null || comparator.compare(item, maxItem) < 0) {
                    maxItem = item;
                }
            }
            return maxItem;
        }).reduce(collection);
    }

    public Optional<T> find(Predicate<T> predicate) {
        return new ReducePipeline<>(transformPipelines, items -> {
            for (T item : (Collection<T>) items) {
                if (predicate.test(item)) {
                    return Optional.of(item);
                }
            }
            return (Optional<T>) Optional.empty();
        }).reduce(collection);
    }

    public void forEach(Consumer<T> consumer) {
        new ReducePipeline<>(transformPipelines, items -> {
            for (T item : (Collection<T>) items) {
                consumer.accept(item);
            }
            return null; // Void return type
        }).reduce(collection);
    }

    public boolean anyMatch(Predicate<T> predicate) {
        return new ReducePipeline<>(transformPipelines, items -> {
            for (T item : (Collection<T>) items) {
                if (predicate.test(item)) {
                    return true;
                }
            }
            return false;
        }).reduce(collection);
    }

    public boolean allMatch(Predicate<T> predicate) {
        return new ReducePipeline<>(transformPipelines, items -> {
            for (T item : (Collection<T>) items) {
                if (!predicate.test(item)) {
                    return false;
                }
            }
            return true;
        }).reduce(collection);
    }

    public boolean noneMatch(Predicate<T> predicate) {
        return new ReducePipeline<>(transformPipelines, items -> {
            for (T item : (Collection<T>) items) {
                if (predicate.test(item)) {
                    return false;
                }
            }
            return true;
        }).reduce(collection);
    }
}
