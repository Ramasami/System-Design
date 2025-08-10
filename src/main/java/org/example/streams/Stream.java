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

/**
 * A custom implementation of a Stream-like processing library that supports both sequential
 * and parallel operations on collections.
 * <p>
 * Example usage:
 * <pre>
 * Stream<Integer> stream = new Stream<>(Arrays.asList(1, 2, 3, 4, 5))
 *     .filter(n -> n % 2 == 0)
 *     .map(n -> n * 2)
 *     .toList(); // Returns [4, 8]
 * </pre>
 *
 * @param <T> The type of elements in the stream
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Stream<T> {

    /** Default thread pool for parallel operations */
    private static final ExecutorService executorService = Executors.newFixedThreadPool(8);

    /** The underlying collection being processed */
    private final Collection<?> collection;

    /** List of transformation operations to be applied */
    private final List<TransformPipeline<?, ?>> transformPipelines;

    /** Flag indicating if operations should be executed in parallel */
    private final boolean isParallel;

    /** Optional user-provided executor service for parallel operations */
    private final ExecutorService userProvidedExecutorService;

    /**
     * Creates a new Stream with the given collection
     * @param collection The source collection
     * @throws IllegalArgumentException if collection is null
     */
    public Stream(Collection<T> collection) {
        this.collection = new ArrayList<>(collection);
        this.transformPipelines = new ArrayList<>();
        this.isParallel = false;
        this.userProvidedExecutorService = null;
    }

    /**
     * Adds a transformation pipeline to the stream
     * @param transformPipeline Pipeline to append
     * @return New stream with appended pipeline
     */
    private <U> Stream<U> appendPipeline(TransformPipeline<T, U> transformPipeline) {
        if (transformPipeline == null) {
            throw new IllegalArgumentException("TransformPipeline cannot be null");
        }
        transformPipelines.add(transformPipeline);
        return new Stream<>(collection, transformPipelines, isParallel, userProvidedExecutorService);
    }

    /**
     * Filters elements based on the given predicate
     * <p>
     * Example:
     * <pre>
     * Stream<Integer> stream = new Stream<>(Arrays.asList(1, 2, 3, 4))
     *     .filter(n -> n > 2); // Returns [3, 4]
     * </pre>
     *
     * @param predicate Condition to test elements against
     * @return Stream containing only elements that match the predicate
     * @throws IllegalArgumentException if predicate is null
     */
    public Stream<T> filter(Predicate<T> predicate) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate cannot be null");
        }
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

    /**
     * Maps elements to new values using the provided function
     * <p>
     * Example:
     * <pre>
     * Stream<String> stream = new Stream<>(Arrays.asList(1, 2, 3))
     *     .map(String::valueOf); // Returns ["1", "2", "3"]
     * </pre>
     *
     * @param mapper Function to transform elements
     * @return Stream containing transformed elements
     * @throws IllegalArgumentException if mapper is null
     */
    public <U> Stream<U> map(Function<T, U> mapper) {
        if (mapper == null) {
            throw new IllegalArgumentException("Mapper cannot be null");
        }
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

    /**
     * Iterates over the stream and returns a stream of pairs containing the index and the element
     * <p>
     * Example:
     * <pre>
     * Stream<Pair<Long, Integer>> indexed = new Stream<>(Arrays.asList(1, 2, 3))
     *     .iterate(); // Returns [(0, 1), (1, 2), (2, 3)]
     * </pre>
     *
     * @return Stream of pairs with index and element
     */
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

    /**
     * Flattens nested streams into a single stream
     * <p>
     * Example:
     * <pre>
     * List<List<Integer>> nested = Arrays.asList(
     *     Arrays.asList(1, 2),
     *     Arrays.asList(3, 4)
     * );
     * Stream<Integer> flattened = new Stream<>(nested)
     *     .flatMap(list -> new Stream<>(list)); // Returns [1, 2, 3, 4]
     * </pre>
     *
     * @param flatMapper Function that produces streams to flatten
     * @return Flattened stream
     * @throws IllegalArgumentException if flatMapper is null
     */
    public <U> Stream<U> flatMap(Function<T, Stream<U>> flatMapper) {
        if (flatMapper == null) {
            throw new IllegalArgumentException("FlatMapper cannot be null");
        }
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

    /**
     * Peeks at each element in the stream and applies the given consumer
     * <p>
     * Example:
     * <pre>
     * Stream<Integer> stream = new Stream<>(Arrays.asList(1, 2, 3))
     *     .peek(System.out::println); // Prints each number
     * </pre>
     *
     * @param consumer Consumer to apply to each element
     * @return Stream with the consumer applied to each element
     * @throws IllegalArgumentException if consumer is null
     */
    public Stream<T> peek(Consumer<T> consumer) {
        if (consumer == null) {
            throw new IllegalArgumentException("Consumer cannot be null");
        }
        return map(item -> {
            consumer.accept(item);
            return item;
        });
    }

    /**
     * Returns a stream sorted in natural order
     * <p>
     * Example:
     * <pre>
     * Stream<Integer> stream = new Stream<>(Arrays.asList(3, 1, 4, 2))
     *     .sorted(); // Returns [1, 2, 3, 4]
     * </pre>
     *
     * @return Stream sorted in natural order
     */
    public Stream<T> sorted() {
        return sorted(null);
    }

    /**
     * Returns a stream sorted according to the provided comparator
     * <p>
     * Example:
     * <pre>
     * Stream<Integer> stream = new Stream<>(Arrays.asList(3, 1, 4, 2))
     *     .sorted(Integer::compareTo); // Returns [1, 2, 3, 4]
     * </pre>
     *
     * @param comparator Comparator to determine the order of elements
     * @return Stream sorted according to the comparator
     */
    public Stream<T> sorted(Comparator<T> comparator) {
        TransformPipeline<T, T> transformPipeline = new TransformPipeline<>(items -> {
            List<T> sorted = new ArrayList<>(items);
            sorted.sort(comparator);
            return sorted;
        });
        return appendPipeline(transformPipeline);
    }

    /**
     * Returns a stream with distinct elements
     * <p>
     * Example:
     * <pre>
     * Stream<Integer> stream = new Stream<>(Arrays.asList(1, 2, 2, 3, 4, 4))
     *     .distinct(); // Returns [1, 2, 3, 4]
     * </pre>
     *
     * @return Stream with distinct elements
     */
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

    /**
     * Skips the first i elements in the stream
     * <p>
     * Example:
     * <pre>
     * Stream<Integer> stream = new Stream<>(Arrays.asList(1, 2, 3, 4, 5))
     *     .skip(2); // Returns [3, 4, 5]
     * </pre>
     *
     * @param i Number of elements to skip
     * @return Stream with the first i elements skipped
     */
    public Stream<T> skip(int i) {
        if (i < 0) {
            return skipLast(-i);
        } else if (i == 0) {
            return this; // No need to skip anything
        }
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

    /**
     * Skips the last i elements in the stream
     * <p>
     * Example:
     * <pre>
     * Stream<Integer> stream = new Stream<>(Arrays.asList(1, 2, 3, 4, 5))
     *     .skipLast(2); // Returns [1, 2, 3]
     * </pre>
     *
     * @param i Number of elements to skip from the end
     * @return Stream with the last i elements skipped
     */
    public Stream<T> skipLast(int i) {
        if (i < 0) {
            return skip(-i);
        } else if (i == 0) {
            return this;
        }
        TransformPipeline<T, T> transformPipeline = new TransformPipeline<>(items -> {
            List<T> skipped = new ArrayList<>();
            int count = 0;
            int n = items.size() - i;
            for (T item : items) {
                if (count++ < n) {
                    skipped.add(item);
                }
            }
            return skipped;
        });
        return appendPipeline(transformPipeline);
    }

    /**
     * Limits the first i elements in the stream
     * <p>
     * Example:
     * <pre>
     * Stream<Integer> stream = new Stream<>(Arrays.asList(1, 2, 3, 4, 5))
     *     .limit(3); // Returns [1, 2, 3]
     * </pre>
     *
     * @param i Number of elements to limit
     * @return Stream containing the first i elements
     */
    public Stream<T> limit(int i) {
        if (i < 0) {
            return last(-i);
        } else if (i == 0) {
            return new Stream<>(Collections.emptyList(), transformPipelines, isParallel, userProvidedExecutorService); // Return empty stream
        }
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

    /**
     * Limits the last i elements in the stream
     * <p>
     * Example:
     * <pre>
     * Stream<Integer> stream = new Stream<>(Arrays.asList(1, 2, 3, 4, 5))
     *     .limitLast(3); // Returns [3, 4, 5]
     * </pre>
     *
     * @param i Number of elements to limit from the end
     * @return Stream containing the last i elements
     */
    public Stream<T> last(int i) {
        if (i < 0) {
            return limit(-i);
        } else if (i == 0) {
            return new Stream<>(Collections.emptyList(), transformPipelines, isParallel, userProvidedExecutorService); // Return empty stream
        }
        TransformPipeline<T, T> transformPipeline = new TransformPipeline<>(items -> {
            List<T> limited = new ArrayList<>();
            int count = 0;
            int n = items.size() - i;
            for (T item : items) {
                if (count++ >= n) {
                    limited.add(item);
                }
            }
            return limited;
        });
        return appendPipeline(transformPipeline);
    }

    /**
     * Reduces stream elements to a single value using an accumulator function
     * and returns the result
     * <p>
     * Example:
     * <pre>
     * Integer sum = new Stream<>(Arrays.asList(1, 2, 3, 4))
     *     .reduce(Integer::sum, 0); // Returns 10
     * </pre>
     *
     * @param accumulator Function to combine elements
     * @param identity Initial value for reduction
     * @return Reduced result
     * @throws IllegalArgumentException if accumulator is null
     */
    public T reduce(BiFunction<T, T, T> accumulator, T identity) {
        if (accumulator == null) {
            throw new IllegalArgumentException("Accumulator cannot be null");
        }
        return new ReducePipeline<>(transformPipelines, items -> {
            T result = identity;
            for (T item : items) {
                result = accumulator.apply(result, item);
            }
            return result;
        }, this).reduce(collection);
    }

    /**
     * Reduces stream elements to a single value using an accumulator function
     * and returns an Optional result
     * <p>
     * Example:
     * <pre>
     * Optional<Integer> sum = new Stream<>(Arrays.asList(1, 2, 3, 4))
     *     .reduce(Integer::sum); // Returns Optional.of(10)
     * </pre>
     *
     * @param accumulator Function to combine elements
     * @return Optional containing reduced result or empty if stream is empty
     * @throws IllegalArgumentException if accumulator is null
     */
    public Optional<T> reduce(BiFunction<T, T, T> accumulator) {
        if (accumulator == null) {
            throw new IllegalArgumentException("Accumulator cannot be null");
        }
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

    /**
     * Finds the maximum element using the provided comparator
     * <p>
     * Example:
     * <pre>
     * Integer max = new Stream<>(Arrays.asList(1, 2, 3))
     *     .max(Integer::compareTo); // Returns 3
     * </pre>
     *
     * @param comparator Comparator to determine maximum
     * @return Maximum element or null if stream is empty
     * @throws IllegalArgumentException if comparator is null
     */
    public T max(Comparator<T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("Comparator cannot be null");
        }
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

    /**
     * Finds the minimum element in the stream using the provided comparator
     * <p>
     * Example:
     * <pre>
     * Integer min = new Stream<>(Arrays.asList(3, 1, 4, 2))
     *     .min(Integer::compareTo); // Returns 1
     * </pre>
     *
     * @param comparator Comparator to determine the minimum element
     * @return Minimum element or null if the stream is empty
     * @throws IllegalArgumentException if comparator is null
     */
    public T min(Comparator<T> comparator) {
        if (comparator == null) {
            throw new IllegalArgumentException("Comparator cannot be null");
        }
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

    /**
     * Calculates the sum of elements using the provided mapper function
     * <p>
     * Example:
     * <pre>
     * Double sum = new Stream<>(Arrays.asList(1.0, 2.0, 3.0))
     *     .sum(Function.identity()); // Returns 6.0
     * </pre>
     *
     * @param mapper Function to extract numeric value from each element
     * @return Sum of the mapped elements
     * @throws IllegalArgumentException if mapper is null
     */
    public Double sum(Function<T, Double> mapper) {
        if (mapper == null) {
            throw new IllegalArgumentException("Mapper cannot be null");
        }
        return new ReducePipeline<>(transformPipelines, items -> {
            Iterator<T> iterator = items.iterator();
            double sum = 0.0;
            while (iterator.hasNext()) {
                sum += mapper.apply(iterator.next());
            }
            return sum;
        }, this).reduce(collection);
    }

    /**
     * Calculates the average of elements using the provided mapper function
     * <p>
     * Example:
     * <pre>
     * Double average = new Stream<>(Arrays.asList(1.0, 2.0, 3.0))
     *     .average(Function.identity()); // Returns 2.0
     * </pre>
     *
     * @param mapper Function to extract numeric value from each element
     * @return Average value of the mapped elements
     * @throws IllegalArgumentException if mapper is null
     */
    public Double average(Function<T, Double> mapper) {
        if (mapper == null) {
            throw new IllegalArgumentException("Mapper cannot be null");
        }
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

    /**
     * Finds the first element that matches the given predicate
     * <p>
     * Example:
     * <pre>
     * Optional<Integer> firstEven = new Stream<>(Arrays.asList(1, 2, 3, 4))
     *     .find(n -> n % 2 == 0); // Returns Optional.of(2)
     * </pre>
     *
     * @param predicate Predicate to test elements against
     * @return Optional containing the first matching element, or empty if none found
     * @throws IllegalArgumentException if predicate is null
     */
    public Optional<T> find(Predicate<T> predicate) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate cannot be null");
        }
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

    /**
     * Executes a consumer for each element in the stream
     * <p>
     * Example:
     * <pre>
     * new Stream<>(Arrays.asList("apple", "banana", "cherry"))
     *     .forEach(System.out::println); // Prints each fruit
     * </pre>
     *
     * @param consumer Consumer to apply to each element
     * @throws IllegalArgumentException if consumer is null
     */
    public void forEach(Consumer<T> consumer) {
        if (consumer == null) {
            throw new IllegalArgumentException("Consumer cannot be null");
        }
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

    /**
     * Checks if any element matches the given predicate
     * <p>
     * Example:
     * <pre>
     * boolean hasEven = new Stream<>(Arrays.asList(1, 2, 3))
     *     .anyMatch(n -> n % 2 == 0); // Returns true
     * </pre>
     *
     * @param predicate Predicate to test elements against
     * @return true if any element matches the predicate, false otherwise
     * @throws IllegalArgumentException if predicate is null
     */
    public boolean anyMatch(Predicate<T> predicate) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate cannot be null");
        }
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

    /**
     * Checks if all elements match the given predicate
     * <p>
     * Example:
     * <pre>
     * boolean allEven = new Stream<>(Arrays.asList(2, 4, 6))
     *     .allMatch(n -> n % 2 == 0); // Returns true
     * </pre>
     *
     * @param predicate Predicate to test elements against
     * @return true if all elements match the predicate, false otherwise
     * @throws IllegalArgumentException if predicate is null
     */
    public boolean allMatch(Predicate<T> predicate) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate cannot be null");
        }
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

    /**
     * Checks if no elements match the given predicate
     * <p>
     * Example:
     * <pre>
     * boolean noneEven = new Stream<>(Arrays.asList(1, 3, 5))
     *     .noneMatch(n -> n % 2 == 0); // Returns true
     * </pre>
     *
     * @param predicate Predicate to test elements against
     * @return true if no elements match the predicate, false otherwise
     * @throws IllegalArgumentException if predicate is null
     */
    public boolean noneMatch(Predicate<T> predicate) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate cannot be null");
        }
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

    /**
     * Converts the stream to a list
     * <p>
     * Example:
     * <pre>
     * List<String> list = new Stream<>(Arrays.asList("apple", "banana", "cherry"))
     *     .toList(ArrayList.class); // Returns ["apple", "banana", "cherry"]
     * </pre>
     *
     * @param clazz Class of the list to create
     * @return List containing elements from the stream
     */
    @SuppressWarnings("unchecked")
    public <L extends List<?>> List<T> toList(Class<L> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("List class cannot be null");
        }
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

    /**
     * Converts the stream to a set
     * <p>
     * Example:
     * <pre>
     * Set<String> set = new Stream<>(Arrays.asList("apple", "banana", "cherry"))
     *     .toSet(); // Returns {"apple", "banana", "cherry"}
     * </pre>
     *
     * @return Set containing elements from the stream
     */
    public Set<T> toSet() {
        return toSet(HashSet.class);
    }

    /**
     * Converts the stream to a set
     * <p>
     * Example:
     * <pre>
     * Set<String> set = new Stream<>(Arrays.asList("apple", "banana", "cherry"))
     *     .toSet(HashSet.class); // Returns {"apple", "banana", "cherry"}
     * </pre>
     *
     * @param clazz Class of the set to create
     * @return Set containing elements from the stream
     */
    @SuppressWarnings("unchecked")
    public <S extends Set<?>> Set<T> toSet(Class<S> clazz) {
        if (clazz == null) {
            throw new IllegalArgumentException("Set class cannot be null");
        }
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

    /**
     * Converts the stream to a map with specified key and value mappers
     * <p>
     * Example:
     * <pre>
     * Map<Integer, String> map = new Stream<>(Arrays.asList("apple", "banana", "cherry"))
     *     .toMap(
     *         String::length,
     *         s -> s
     *     ); // Returns {5="apple", 6="banana", 6="cherry"}
     * </pre>
     *
     * @param keyMapper Function to extract the key
     * @param valueMapper Function to extract the value
     * @return Map containing elements from the stream
     * @throws IllegalArgumentException if any parameter is null
     */
    public <K, V> Map<K, V> toMap(Function<T, K> keyMapper, Function<T, V> valueMapper) {
        if (keyMapper == null || valueMapper == null) {
            throw new IllegalArgumentException("KeyMapper and ValueMapper cannot be null");
        }
        return toMap(keyMapper, valueMapper, HashMap.class);
    }

    /**
     * Converts the stream to a map with specified key and value mappers
     * <p>
     * Example:
     * <pre>
     * Map<Integer, String> map = new Stream<>(Arrays.asList("apple", "banana", "cherry"))
     *     .toMap(
     *         String::length,
     *         s -> s
     *     ); // Returns {5="apple", 6="banana", 6="cherry"}
     * </pre>
     *
     * @param keyMapper Function to extract the key
     * @param valueMapper Function to extract the value
     * @param mapClass Class of the map to create
     * @return Map containing elements from the stream
     * @throws IllegalArgumentException if any parameter is null
     */
    public <M extends Map<?, ?>, K, V> Map<K, V> toMap(Function<T, K> keyMapper, Function<T, V> valueMapper, Class<M> mapClass) {
        if (keyMapper == null || valueMapper == null || mapClass == null) {
            throw new IllegalArgumentException("KeyMapper, ValueMapper and MapClass cannot be null");
        }
        return toMap(keyMapper, valueMapper, (a, b) -> {
            throw new IllegalStateException("Duplicate key found for " + keyMapper.apply(a) + " and " + keyMapper.apply(b));
        }, mapClass);
    }

    /**
     * Converts the stream to a map with specified key and value mappers
     * <p>
     * Example:
     * <pre>
     * Map<Integer, String> map = new Stream<>(Arrays.asList("apple", "banana", "cherry"))
     *     .toMap(
     *         String::length,
     *         s -> s
     *     ); // Returns {5="apple", 6="banana", 6="cherry"}
     * </pre>
     *
     * @param keyMapper Function to extract the key
     * @param valueMapper Function to extract the value
     * @return Map containing elements from the stream
     * @throws IllegalArgumentException if any parameter is null
     */
    public <K, V> Map<K, V> toMap(Function<T, K> keyMapper, Function<T, V> valueMapper, BiFunction<T, T, T> accumulator) {
        if (keyMapper == null || valueMapper == null || accumulator == null) {
            throw new IllegalArgumentException("KeyMapper, ValueMapper and Accumulator cannot be null");
        }
        return toMap(keyMapper, valueMapper, accumulator, HashMap.class);
    }

    /**
     * Converts the stream to a map with specified key and value mappers and an accumulator for duplicate keys
     * <p>
     * Example:
     * <pre>
     * Map<Integer, String> map = new Stream<>(Arrays.asList("apple", "banana", "cherry"))
     *     .toMap(
     *         String::length,
     *         s -> s,
     *         (s1, s2) -> s1 + "," + s2
     *     ); // Returns {5="apple", 6="banana,cherry"}
     * </pre>
     *
     * @param keyMapper Function to extract the key
     * @param valueMapper Function to extract the value
     * @param accumulator Function to combine values for duplicate keys
     * @param mapClass Class of the map to create
     * @return Map containing elements from the stream
     * @throws IllegalArgumentException if any parameter is null
     */
    @SuppressWarnings("unchecked")
    public <M extends Map<?, ?>, K, V> Map<K, V> toMap(Function<T, K> keyMapper, Function<T, V> valueMapper, BiFunction<T, T, T> accumulator, Class<M> mapClass) {
        if (keyMapper == null || valueMapper == null || accumulator == null || mapClass == null) {
            throw new IllegalArgumentException("KeyMapper, ValueMapper, Accumulator and MapClass cannot be null");
        }
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

    /**
     * Groups elements by key into a map
     * <p>
     * Example:
     * <pre>
     * Map<Integer, List<String>> grouped = new Stream<>(Arrays.asList("a", "bb", "ccc"))
     *     .toGroupedMap(
     *         String::length,
     *         s -> s
     *     ); // Returns {1=["a"], 2=["bb"], 3=["ccc"]}
     * </pre>
     *
     * @param keyMapper Function to extract the key
     * @param valueMapper Function to extract the value
     * @return Map containing grouped elements
     * @throws IllegalArgumentException if any parameter is null
     */
    @SuppressWarnings("unchecked")
    public <K, V> Map<K, List<V>> toGroupedMap(Function<T, K> keyMapper, Function<T, V> valueMapper) {
        if (keyMapper == null || valueMapper == null) {
            throw new IllegalArgumentException("KeyMapper and ValueMapper cannot be null");
        }
        return (Map) toGroupedMap(keyMapper, valueMapper, HashMap.class, ArrayList.class);
    }

    /**
     * Groups elements by key into a map with specified map and collection types
     * <p>
     * Example:
     * <pre>
     * Map<Integer, Set<String>> grouped = new Stream<>(Arrays.asList("a", "bb", "ccc"))
     *     .toGroupedMap(
     *         String::length,
     *         s -> s,
     *         HashMap.class,
     *         HashSet.class
     *     ); // Returns {1=["a"], 2=["bb"], 3=["ccc"]}
     * </pre>
     *
     * @param keyMapper Function to extract the key
     * @param valueMapper Function to extract the value
     * @param mapClass Class of the map to create
     * @param collectionClass Class of the collection to use for values
     * @return Map containing grouped elements
     * @throws IllegalArgumentException if any parameter is null
     */
    @SuppressWarnings("unchecked")
    public <M extends Map<?, ?>, C extends Collection<?>, K, V> Map<K, Collection<V>> toGroupedMap(Function<T, K> keyMapper, Function<T, V> valueMapper, Class<M> mapClass, Class<C> collectionClass) {
        if (keyMapper == null || valueMapper == null || mapClass == null || collectionClass == null) {
            throw new IllegalArgumentException("KeyMapper, ValueMapper, MapClass and CollectionClass cannot be null");
        }
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

    /**
     * Converts the stream to an array
     * <p>
     * Example:
     * <pre>
     * String[] array = new Stream<>(Arrays.asList("apple", "banana", "cherry"))
     *     .toArray(); // Returns ["apple", "banana", "cherry"]
     * </pre>
     *
     * @return Array containing all elements in the stream
     */
    public T[] toArray() {
        if (collection.isEmpty()) {
            return (T[]) new Object[0]; // Return empty array if collection is empty
        }
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

    /**
     * Joins elements into a single string
     * <p>
     * Example:
     * <pre>
     * String joined = new Stream<>(Arrays.asList("apple", "banana", "cherry"))
     *     .joining(); // Returns "applebananacherry"
     * </pre>
     *
     * @return Joined string
     */
    public String joining() {
        return new ReducePipeline<>(transformPipelines, items -> {
            StringBuilder sb = new StringBuilder();
            for (T item : items) {
                sb.append(item.toString());
            }
            return sb.toString();
        }, this).reduce(collection);
    }

    /**
     * Joins elements into a single string with a specified separator
     * <p>
     * Example:
     * <pre>
     * String joined = new Stream<>(Arrays.asList("apple", "banana", "cherry"))
     *     .joining(","); // Returns "apple,banana,cherry"
     * </pre>
     *
     * @param separator Separator to use between elements
     * @return Joined string
     * @throws IllegalArgumentException if separator is null
     */
    public String joining(String separator) {
        if (separator == null) {
            throw new IllegalArgumentException("Separator cannot be null");
        }
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

    /**
     * Returns an iterator over the elements in the stream
     * This method is used to iterate through the stream elements
     * <p>
     * Example:
     * <pre>
     * for (Iterator<String> it = new Stream<>(Arrays.asList("apple", "banana", "cherry")).iterator(); it.hasNext(); ) {
     *             String fruit = it.next();
     *             System. out. println(fruit);
     * }
     * </pre>
     *
     * @return Iterator over the stream elements
     * @throws NullPointerException if the collection is null
     */
    public Iterator<T> iterator() {
        if (collection == null) {
            throw new NullPointerException("Collection cannot be null");
        }
        return new ReducePipeline<>(transformPipelines, Collection::iterator, this).reduce(collection);
    }

    /**
     * Prints each element of the stream to the console
     * This is a convenience method that calls {@link #forEach(Consumer)} with System.out::println
     * Example:
     * <pre>
     *     new Stream<>(Arrays.asList("apple", "banana", "cherry"))
     *     .println(); // Prints each fruit on a new line
     * </pre>
     */
    public void println() {
        forEach(System.out::println);
    }

    /**
     * Prints the stream elements to the console
     * This is a convenience method that calls {@link #toString()} and prints the result
     * Example:
     * <pre>
     *     new Stream<>(Arrays.asList("apple", "banana", "cherry"))
     *     .print(); // Prints the string representation of the stream
     * </pre>
     */
    public void print() {
        System.out.print(this);
    }

    /**
     * Executes stream operations in parallel using a thread pool
     * <p>
     * Example:
     * <pre>
     * List<Integer> result = new Stream<>(Arrays.asList(1, 2, 3, 4))
     *     .parallel()
     *     .map(n -> n * 2)
     *     .toList(); // Processes in parallel
     * </pre>
     *
     * @return Stream configured for parallel execution
     */
    public Stream<T> parallel() {
        return new Stream<>(collection, transformPipelines, true, null);
    }

    /**
     * Executes stream operations in parallel using a user-provided executor service
     * <p>
     * Example:
     * <pre>
     * ExecutorService executor = Executors.newFixedThreadPool(4);
     * List<Integer> result = new Stream<>(Arrays.asList(1, 2, 3, 4))
     *     .parallel(executor)
     *     .map(n -> n * 2)
     *     .toList(); // Processes in parallel using the provided executor
     * </pre>
     *
     * @param executorService User-provided executor service for parallel execution
     * @return Stream configured for parallel execution with the given executor
     */
    public Stream<T> parallel(ExecutorService executorService) {
        if (executorService == null) {
            throw new IllegalArgumentException("ExecutorService cannot be null");
        }
        return new Stream<>(collection, transformPipelines, true, executorService);
    }

    /**
     * Executes stream operations sequentially
     * <p>
     * Example:
     * <pre>
     * List<Integer> result = new Stream<>(Arrays.asList(1, 2, 3, 4))
     *     .sequential()
     *     .map(n -> n * 2)
     *     .toList(); // Processes sequentially
     * </pre>
     *
     * @return Stream configured for sequential execution
     */
    public Stream<T> sequential() {
        return new Stream<>(collection, transformPipelines, false, null);
    }

    /**
     * Executes a predicate against an item in the stream
     *
     * @param predicate Predicate to test the item
     * @param item Item to test
     * @return Future that resolves to true if the predicate matches, false otherwise
     * @throws IllegalArgumentException if predicate is null
     */
    private AbstractStreamFuture<Boolean> execute(Predicate<T> predicate, T item) {
        if (predicate == null) {
            throw new IllegalArgumentException("Predicate cannot be null");
        }
        return execute((Function<T, Boolean>) t -> predicate.test(item), item);
    }

    /**
     * Executes a function against an item in the stream
     *
     * @param mapper Function to apply to the item
     * @param item Item to process
     * @return Future that resolves to the result of the function
     * @throws IllegalArgumentException if mapper is null
     */
    private <U> AbstractStreamFuture<U> execute(Function<T, U> mapper, T item) {
        if (mapper == null) {
            throw new IllegalArgumentException("Mapper cannot be null");
        }
        if (!isParallel) {
            return new SimpleFuture<>(mapper.apply(item));
        } else
            return new ParallelFuture<>(Objects.requireNonNullElse(userProvidedExecutorService, executorService)
                    .submit(() -> mapper.apply(item)));
    }

    /**
     * Executes a consumer against an item in the stream
     *
     * @param consumer Consumer to apply to the item
     * @param item Item to process
     * @return Future that resolves when the consumer has been applied
     * @throws IllegalArgumentException if consumer is null
     */
    private AbstractStreamFuture<Void> execute(Consumer<T> consumer, T item) {
        if (consumer == null) {
            throw new IllegalArgumentException("Consumer cannot be null");
        }
        return execute((Function<T, Void>) t -> {
            consumer.accept(item);
            return null;
        }, item);
    }

    /**
     * Converts the stream to a list
     *
     * @return List containing all elements in the stream
     */
    @Override
    public String toString() {
        return toList().toString();
    }

    /**
     * A simple pair class to hold key-value pairs
     *
     * @param <K> Type of the key
     * @param <V> Type of the value
     */
    public record Pair<K, V>(K key, V value) {
    }

    /**
     * Closes the stream and releases any resources held by it
     * This is particularly important if a user-provided executor service was used
     */
    protected void close() {
        if (userProvidedExecutorService != null) {
            userProvidedExecutorService.shutdown();
        }
    }
}
