# Custom Stream Implementation

A Java implementation of a Stream-like processing library that supports both sequential and parallel operations on collections.

## Overview

This package provides a custom implementation of stream processing with support for:
- Lazy evaluation
- Parallel processing
- Collection transformations
- Terminal operations
- Type-safe operations

## Core Components

### Stream<T>
The main class that provides stream processing capabilities.

#### Key Features
- **Lazy Evaluation**: Operations are not executed until a terminal operation is called
- **Parallel Processing**: Support for parallel execution using thread pools
- **Chainable Operations**: Fluent interface for method chaining

#### Stream Operations

##### Intermediate Operations
- `filter(Predicate<T>)`: Filters elements based on a predicate
- `map(Function<T,U>)`: Transforms elements from one type to another
- `flatMap(Function<T,Stream<U>>)`: Flattens and transforms nested streams
- `distinct()`: Removes duplicate elements
- `sorted()`: Sorts elements in natural order
- `limit(int)`: Limits the stream to specified size
- `skip(int)`: Skips specified number of elements
- `peek(Consumer<T>)`: Performs an action on each element while maintaining the stream

##### Terminal Operations
- `reduce(BiFunction<T,T,T>, T)`: Reduces stream to single value
- `forEach(Consumer<T>)`: Performs action for each element
- `toList()`: Collects elements into a List
- `toSet()`: Collects elements into a Set
- `toMap()`: Collects elements into a Map
- `count()`: Returns the count of elements

### Pipeline System

#### TransformPipeline<I,O>
Handles intermediate operations that transform the stream elements.
- Maintains transformation function
- Supports type-safe transformations

#### ReducePipeline<I,O>
Manages terminal operations that produce final results.
- Executes all pending transformations
- Produces final result

### Future Implementation

#### AbstractStreamFuture<T>
Base class for handling operation results.

#### SimpleFuture<T>
Handles sequential operation results.

#### ParallelFuture<T>
Manages parallel operation results using Java's Future API.

## Usage Examples

```java
// Creating a stream
Stream<Integer> stream = Stream.of(1, 2, 3, 4, 5);

// Sequential processing
List<Integer> evenNumbers = stream
    .filter(n -> n % 2 == 0)
    .map(n -> n * 2)
    .toList();

// Parallel processing
Stream<String> parallelStream = Stream.of("a", "b", "c")
    .parallel()
    .map(String::toUpperCase);

// Complex transformations
Stream<Integer> flatMapped = Stream.of(List.of(1,2), List.of(3,4))
    .flatMap(Collection::stream)
    .distinct();
   
```
### Threading Model
- Sequential operations: Executed in the calling thread
- Parallel operations: Executed using:
   - Default ForkJoinPool
   - User-provided ExecutorService
   
### Performance Considerations
- Lazy evaluation prevents unnecessary computations
- Parallel processing beneficial for:
   - Large datasets
   - Computationally intensive operations
- Memory efficiency through pipeline architecture
   
### Implementation Details
- Type-safe generic implementations
- Thread-safe parallel processing
- Efficient collection transformations
- Minimal object creation

### Requirements
- Java 11 or higher
- No external dependencies

### Best Practices
1. Prefer sequential processing for small datasets
2. Use parallel processing for CPU-intensive operations
3. Close streams when using custom ExecutorService
4. Handle exceptions in terminal operations