package org.example.streams;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Stream<T> {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(8);
    private final Collection<?> collection;
    private final List<TransformPipeline<?, ?>> transformPipelines;
    private final boolean isParallel;
    private final ExecutorService userProvidedExecutorService;

    public Stream(Collection<T> collection) {
        this.collection = new ArrayList<>(collection);
        this.transformPipelines = new ArrayList<>();
        this.isParallel = false;
        this.userProvidedExecutorService = null;
    }

    private <U> Stream<U> appendPipeline(TransformPipeline<T, U> transformPipeline) {
        transformPipelines.add(transformPipeline);
        return new Stream<>(collection, transformPipelines, isParallel, userProvidedExecutorService);
    }

    public Stream<T> filter(Predicate<T> predicate) {
        TransformPipeline<T, T> transformPipeline = new TransformPipeline<>(items -> {
            List<T> filtered = new ArrayList<>();
            List<Pair<T, AbstractStreamFuture<Boolean>>> futures = new ArrayList<>();
            for (T item : items) {
                futures.add(new Pair<>(item, execute(predicate, item)));
            }
            for (Pair<T, AbstractStreamFuture<Boolean>> pair : futures) {
                if (pair.value().get()) {
                    filtered.add(pair.key());
                }
            }
            return filtered;
        });
        return appendPipeline(transformPipeline);
    }

    public <U> Stream<U> map(Function<T, U> mapper) {
        TransformPipeline<T, U> transformPipeline = new TransformPipeline<>(items -> {
            List<U> mapped = new ArrayList<>();
            List<AbstractStreamFuture<U>> futures = new ArrayList<>();
            for (T item : items) {
                futures.add(execute(mapper, item));
            }
            for (AbstractStreamFuture<U> future : futures) {
                mapped.add(future.get());
            }
            return mapped;
        });
        return appendPipeline(transformPipeline);
    }

    public Stream<Pair<Long, T>> iterate() {
        TransformPipeline<T, Pair<Long, T>> transformPipeline = new TransformPipeline<>(items -> {
            List<Pair<Long, T>> indexed = new ArrayList<>();
            long index = 0L;
            for (T item : items) {
                indexed.add(new Pair<>(index++, item));
            }
            return indexed;
        });
        return appendPipeline(transformPipeline);
    }

    public <U> Stream<U> flatMap(Function<T, Stream<U>> flatMapper) {
        TransformPipeline<T, U> transformPipeline = new TransformPipeline<>(items -> {
            List<U> flatMapped = new ArrayList<>();
            List<AbstractStreamFuture<Stream<U>>> futures = new ArrayList<>();

            for (T item : items) {
                futures.add(execute(flatMapper, item));
            }
            for (AbstractStreamFuture<Stream<U>> future : futures) {
                Stream<U> stream = future.get();
                flatMapped.addAll(stream.toList());
            }
            return flatMapped;
        });
        return appendPipeline(transformPipeline);
    }

    public Stream<T> peek(Consumer<T> consumer) {
        return map(item -> {
            consumer.accept(item);
            return item;
        });
    }

    public Stream<T> sorted() {
        return sorted(null);
    }

    public Stream<T> sorted(Comparator<T> comparator) {
        TransformPipeline<T, T> transformPipeline = new TransformPipeline<>(items -> {
            List<T> sorted = new ArrayList<>(items);
            sorted.sort(comparator);
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

    public T reduce(BiFunction<T, T, T> accumulator, T identity) {
        return new ReducePipeline<>(transformPipelines, items -> {
            T result = identity;
            for (T item : items) {
                result = accumulator.apply(result, item);
            }
            return result;
        }, this).reduce(collection);
    }

    public Optional<T> reduce(BiFunction<T, T, T> accumulator) {
        return new ReducePipeline<T, Optional<T>>(transformPipelines, items -> {
            if (items.isEmpty()) {
                return Optional.empty();
            }
            T result = items.iterator().next();
            while (items.iterator().hasNext()) {
                T item = items.iterator().next();
                result = accumulator.apply(result, item);
            }
            return Optional.ofNullable(result);
        }, this).reduce(collection);
    }

    public long count() {
        return new ReducePipeline<>(transformPipelines, items -> (long) items.size(), this).reduce(collection);
    }

    public T max(Comparator<T> comparator) {
        return new ReducePipeline<>(transformPipelines, items -> {
            if (items.isEmpty()) {
                return null;
            }
            T maxItem = null;
            for (T item : items) {
                if (maxItem == null || comparator.compare(item, maxItem) > 0) {
                    maxItem = item;
                }
            }
            return maxItem;
        }, this).reduce(collection);
    }

    public T min(Comparator<T> comparator) {
        return new ReducePipeline<>(transformPipelines, items -> {
            if (items.isEmpty()) {
                return null;
            }
            T maxItem = null;
            for (T item : items) {
                if (maxItem == null || comparator.compare(item, maxItem) < 0) {
                    maxItem = item;
                }
            }
            return maxItem;
        }, this).reduce(collection);
    }

    public Double sum(Function<T, Double> mapper) {
        return new ReducePipeline<>(transformPipelines, items -> {
            Iterator<T> iterator = items.iterator();
            double sum = 0.0;
            while (iterator.hasNext()) {
                sum += mapper.apply(iterator.next());
            }
            return sum;
        }, this).reduce(collection);
    }

    public Double average(Function<T, Double> mapper) {
        return new ReducePipeline<>(transformPipelines, items -> {
            Iterator<T> iterator = items.iterator();
            double sum = 0.0;
            long count = 0;
            while (iterator.hasNext()) {
                sum += mapper.apply(iterator.next());
                count++;
            }
            return count == 0 ? 0.0 : sum / count;
        }, this).reduce(collection);
    }

    public Optional<T> find(Predicate<T> predicate) {
        return new ReducePipeline<T, Optional<T>>(transformPipelines, items -> {
            List<Pair<T, AbstractStreamFuture<Boolean>>> futures = new ArrayList<>();
            for (T item : items) {
                futures.add(new Pair<>(item, execute(predicate, item)));
            }
            for (Pair<T, AbstractStreamFuture<Boolean>> future : futures) {
                if (future.value().get()) {
                    return Optional.of(future.key());
                }
            }
            return Optional.empty();
        }, this).reduce(collection);
    }

    public void forEach(Consumer<T> consumer) {
        new ReducePipeline<T, Void>(transformPipelines, items -> {
            List<AbstractStreamFuture<Void>> futures = new ArrayList<>();
            for (T item : items) {
                futures.add(execute(consumer, item));
            }
            for (AbstractStreamFuture<Void> future : futures) {
                future.get();
            }
            return null;
        }, this).reduce(collection);
    }

    public boolean anyMatch(Predicate<T> predicate) {
        return new ReducePipeline<>(transformPipelines, items -> {
            List<AbstractStreamFuture<Boolean>> futures = new ArrayList<>();
            for (T item : items) {
                futures.add(execute(predicate, item));
            }
            for (AbstractStreamFuture<Boolean> future : futures) {
                if (future.get()) {
                    return true;
                }
            }
            return false;
        }, this).reduce(collection);
    }

    public boolean allMatch(Predicate<T> predicate) {
        return new ReducePipeline<>(transformPipelines, items -> {
            List<AbstractStreamFuture<Boolean>> futures = new ArrayList<>();
            for (T item : items) {
                futures.add(execute(predicate, item));
            }
            for (AbstractStreamFuture<Boolean> future : futures) {
                if (!future.get()) {
                    return false;
                }
            }
            return true;
        }, this).reduce(collection);
    }

    public boolean noneMatch(Predicate<T> predicate) {
        return new ReducePipeline<>(transformPipelines, items -> {
            List<AbstractStreamFuture<Boolean>> futures = new ArrayList<>();
            for (T item : items) {
                futures.add(execute(predicate, item));
            }
            for (AbstractStreamFuture<Boolean> future : futures) {
                if (future.get()) {
                    return false;
                }
            }
            return true;
        }, this).reduce(collection);
    }

    public List<T> toList() {
        return toList(ArrayList.class);
    }

    @SuppressWarnings("unchecked")
    public <L extends List<?>> List<T> toList(Class<L> clazz) {
        return new ReducePipeline<>(transformPipelines, items -> {
            try {
                List<T> list = (List<T>) clazz.getDeclaredConstructor().newInstance();
                list.addAll(items);
                return list;
            } catch (Exception e) {
                throw new RuntimeException("Failed to create list of type " + clazz.getName(), e);
            }
        }, this).reduce(collection);
    }

    public Set<T> toSet() {
        return toSet(HashSet.class);
    }

    @SuppressWarnings("unchecked")
    public <S extends Set<?>> Set<T> toSet(Class<S> clazz) {
        return new ReducePipeline<>(transformPipelines, items -> {
            try {
                Set<T> list = (Set<T>) clazz.getDeclaredConstructor().newInstance();
                list.addAll(items);
                return list;
            } catch (Exception e) {
                throw new RuntimeException("Failed to create list of type " + clazz.getName(), e);
            }
        }, this).reduce(collection);
    }

    public <K, V> Map<K, V> toMap(Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return toMap(keyMapper, valueMapper, HashMap.class);
    }

    public <M extends Map<?, ?>, K, V> Map<K, V> toMap(Function<T, K> keyMapper, Function<T, V> valueMapper, Class<M> mapClass) {
        return toMap(keyMapper, valueMapper, (a, b) -> {
            throw new IllegalStateException("Duplicate key found for " + keyMapper.apply(a) + " and " + keyMapper.apply(b));
        }, mapClass);
    }

    public <K, V> Map<K, V> toMap(Function<T, K> keyMapper, Function<T, V> valueMapper, BiFunction<T, T, T> accumulator) {
        return toMap(keyMapper, valueMapper, accumulator, HashMap.class);
    }

    @SuppressWarnings("unchecked")
    public <M extends Map<?, ?>, K, V> Map<K, V> toMap(Function<T, K> keyMapper, Function<T, V> valueMapper, BiFunction<T, T, T> accumulator, Class<M> mapClass) {
        return new ReducePipeline<>(transformPipelines, items -> {
            try {
                Map<K, V> map = (Map<K, V>) mapClass.getDeclaredConstructor().newInstance();
                for (T item : items) {
                    K key = keyMapper.apply(item);
                    V value = valueMapper.apply(item);
                    map.compute(key, (k, v) -> {
                        if (v == null) {
                            return value;
                        } else {
                            return (V) accumulator.apply((T) v, item);
                        }
                    });
                }
                return map;
            } catch (Exception e) {
                throw new RuntimeException("Failed to create map of type " + mapClass.getName(), e);
            }
        }, this).reduce(collection);
    }

    @SuppressWarnings("unchecked")
    public <K, V> Map<K, List<V>> toGroupedMap(Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return (Map) toGroupedMap(keyMapper, valueMapper, HashMap.class, ArrayList.class);
    }

    @SuppressWarnings("unchecked")
    public <M extends Map<?, ?>, C extends Collection<?>, K, V> Map<K, Collection<V>> toGroupedMap(Function<T, K> keyMapper, Function<T, V> valueMapper, Class<M> mapClass, Class<C> collectionClass) {
        return new ReducePipeline<>(transformPipelines, items -> {
            try {
                Map<K, Collection<V>> map = (Map<K, Collection<V>>) mapClass.getDeclaredConstructor().newInstance();
                for (T item : items) {
                    K key = keyMapper.apply(item);
                    V value = valueMapper.apply(item);
                    map.computeIfAbsent(key, k -> {
                        try {
                            return (Collection<V>) collectionClass.getDeclaredConstructor().newInstance();
                        } catch (Exception e) {
                            throw new RuntimeException("Failed to create collection of type " + collectionClass.getName(), e);
                        }
                    }).add(value);
                }

                return map;
            } catch (Exception e) {
                throw new RuntimeException("Failed to create map of type " + mapClass.getName(), e);
            }
        }, this).reduce(collection);
    }

    public T[] toArray() {
        return new ReducePipeline<>(transformPipelines, items -> {
            @SuppressWarnings("unchecked")
            T[] array = (T[]) new Object[items.size()];
            int index = 0;
            for (T item : items) {
                array[index++] = item;
            }
            return array;
        }, this).reduce(collection);
    }

    public String joining() {
        return new ReducePipeline<>(transformPipelines, items -> {
            StringBuilder sb = new StringBuilder();
            for (T item : items) {
                sb.append(item.toString());
            }
            return sb.toString();
        }, this).reduce(collection);
    }

    public String joining(String separator) {
        return new ReducePipeline<>(transformPipelines, items -> {
            StringBuilder sb = new StringBuilder();
            Iterator<T> iterator = items.iterator();
            while (iterator.hasNext()) {
                sb.append(iterator.next().toString());
                if (iterator.hasNext()) {
                    sb.append(separator);
                }
            }
            return sb.toString();
        }, this).reduce(collection);
    }

    public Iterator<T> iterator() {
        return new ReducePipeline<>(transformPipelines, Collection::iterator, this).reduce(collection);
    }

    public void println() {
        forEach(System.out::println);
    }

    public void print() {
        System.out.print(this);
    }

    public Stream<T> parallel() {
        return new Stream<>(collection, transformPipelines, true, null);
    }

    public Stream<T> parallel(ExecutorService executorService) {
        return new Stream<>(collection, transformPipelines, true, executorService);
    }

    public Stream<T> sequential() {
        return new Stream<>(collection, transformPipelines, false, null);
    }

    private AbstractStreamFuture<Boolean> execute(Predicate<T> predicate, T item) {
        return execute((Function<T, Boolean>) t -> predicate.test(item), item);
    }

    private <U> AbstractStreamFuture<U> execute(Function<T, U> mapper, T item) {
        if (!isParallel) {
            return new SimpleFuture<>(mapper.apply(item));
        } else
            return new ParallelFuture<>(Objects.requireNonNullElse(userProvidedExecutorService, executorService)
                    .submit(() -> mapper.apply(item)));
    }

    private AbstractStreamFuture<Void> execute(Consumer<T> consumer, T item) {
        return execute((Function<T, Void>) t -> {
            consumer.accept(item);
            return null;
        }, item);
    }

    @Override
    public String toString() {
        return toList().toString();
    }

    public record Pair<K, V>(K key, V value) {
    }

    // Add cleanup method
    public void close() {
        if (userProvidedExecutorService != null) {
            userProvidedExecutorService.shutdown();
        }
    }
}
