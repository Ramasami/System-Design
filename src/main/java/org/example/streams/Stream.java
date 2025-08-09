package org.example.streams;

import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class Stream<T> {

    private final Collection<?> collection;
    private final List<TransformPipeline<?, ?>> transformPipelines;

    public Stream(Collection<T> collection) {
        this.collection = new ArrayList<>(collection);
        transformPipelines = new ArrayList<>();
    }

    public Stream(Object[] array) {
        this.collection = Arrays.asList(array);
        transformPipelines = new ArrayList<>();
    }

    private Stream(Collection<?> collection, List<TransformPipeline<?, ?>> transformPipelines) {
        this.collection = collection;
        this.transformPipelines = transformPipelines;
    }

    private <U> Stream<U> appendPipeline(TransformPipeline<T, U> transformPipeline) {
        transformPipelines.add(transformPipeline);
        return new Stream<>(collection, transformPipelines);
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
            for (T item : items) {
                Stream<U> stream = flatMapper.apply(item);
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
        return new ReducePipeline<T, T>(transformPipelines, items -> {
            T result = identity;
            for (T item : items) {
                result = accumulator.apply(result, item);
            }
            return result;
        }).reduce(collection);
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
        }).reduce(collection);
    }

    public long count() {
        return new ReducePipeline<T, Long>(transformPipelines, items -> (long) items.size()).reduce(collection);
    }

    public T max(Comparator<T> comparator) {
        return new ReducePipeline<T, T>(transformPipelines, items -> {
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
        }).reduce(collection);
    }

    public T min(Comparator<T> comparator) {
        return new ReducePipeline<T, T>(transformPipelines, items -> {
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
        }).reduce(collection);
    }

    public Optional<T> find(Predicate<T> predicate) {
        return new ReducePipeline<T, Optional<T>>(transformPipelines, items -> {
            for (T item : items) {
                if (predicate.test(item)) {
                    return Optional.of(item);
                }
            }
            return Optional.empty();
        }).reduce(collection);
    }

    public void forEach(Consumer<T> consumer) {
        new ReducePipeline<T, Void>(transformPipelines, items -> {
            for (T item : items) {
                consumer.accept(item);
            }
            return null;
        }).reduce(collection);
    }

    public boolean anyMatch(Predicate<T> predicate) {
        return new ReducePipeline<T, Boolean>(transformPipelines, items -> {
            for (T item : items) {
                if (predicate.test(item)) {
                    return true;
                }
            }
            return false;
        }).reduce(collection);
    }

    public boolean allMatch(Predicate<T> predicate) {
        return new ReducePipeline<T, Boolean>(transformPipelines, items -> {
            for (T item : items) {
                if (!predicate.test(item)) {
                    return false;
                }
            }
            return true;
        }).reduce(collection);
    }

    public boolean noneMatch(Predicate<T> predicate) {
        return new ReducePipeline<T, Boolean>(transformPipelines, items -> {
            for (T item : items) {
                if (predicate.test(item)) {
                    return false;
                }
            }
            return true;
        }).reduce(collection);
    }

    public List<T> toList() {
        return toList(ArrayList.class);
    }

    @SuppressWarnings("unchecked")
    public <L extends List<?>> List<T> toList(Class<L> clazz) {
        return new ReducePipeline<T, List<T>>(transformPipelines, items -> {
            try {
                List<T> list = (List<T>) clazz.getDeclaredConstructor().newInstance();
                list.addAll(items);
                return list;
            } catch (Exception e) {
                throw new RuntimeException("Failed to create list of type " + clazz.getName(), e);
            }
        }).reduce(collection);
    }

    public Set<T> toSet() {
        return toSet(HashSet.class);
    }

    @SuppressWarnings("unchecked")
    public <S extends Set<?>> Set<T> toSet(Class<S> clazz) {
        return new ReducePipeline<T, Set<T>>(transformPipelines, items -> {
            try {
                Set<T> list = (Set<T>) clazz.getDeclaredConstructor().newInstance();
                list.addAll(items);
                return list;
            } catch (Exception e) {
                throw new RuntimeException("Failed to create list of type " + clazz.getName(), e);
            }
        }).reduce(collection);
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
        return new ReducePipeline<T, Map<K, V>>(transformPipelines, items -> {
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
        }).reduce(collection);
    }

    @SuppressWarnings("unchecked")
    public <K, V> Map<K, List<V>> toGroupedMap(Function<T, K> keyMapper, Function<T, V> valueMapper) {
        return (Map) toGroupedMap(keyMapper, valueMapper, HashMap.class, ArrayList.class);
    }

    @SuppressWarnings("unchecked")
    public <M extends Map<?, ?>, C extends Collection<?>, K, V> Map<K, Collection<V>> toGroupedMap(Function<T, K> keyMapper, Function<T, V> valueMapper, Class<M> mapClass, Class<C> collectionClass) {
        return new ReducePipeline<T, Map<K, Collection<V>>>(transformPipelines, items -> {
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
        }).reduce(collection);
    }

    public T[] toArray() {
        return new ReducePipeline<T, T[]>(transformPipelines, items -> {
            @SuppressWarnings("unchecked")
            T[] array = (T[]) new Object[items.size()];
            int index = 0;
            for (T item : items) {
                array[index++] = item;
            }
            return array;
        }).reduce(collection);
    }

    public String joining() {
        return new ReducePipeline<T, String>(transformPipelines, items -> {
            StringBuilder sb = new StringBuilder();
            for (T item : items) {
                sb.append(item.toString());
            }
            return sb.toString();
        }).reduce(collection);
    }

    public String joining(String separator) {
        return new ReducePipeline<T, String>(transformPipelines, items -> {
            StringBuilder sb = new StringBuilder();
            Iterator<T> iterator = items.iterator();
            while (iterator.hasNext()) {
                sb.append(iterator.next().toString());
                if (iterator.hasNext()) {
                    sb.append(separator);
                }
            }
            return sb.toString();
        }).reduce(collection);
    }

    public Iterator<T> iterator() {
        return new ReducePipeline<T, Iterator<T>>(transformPipelines, Collection::iterator).reduce(collection);
    }

    public void println() {
        forEach(System.out::println);
    }

    public void print() {
        System.out.print(this);
    }

    @Override
    public String toString() {
        return toList().toString();
    }

    public record Pair<K, V>(K key, V value) {
    }
}
