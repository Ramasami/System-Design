package org.example.streams;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;

import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class StreamTest {
    private List<Integer> numbers;
    private List<String> strings;
    private ExecutorService executor;

    @Before
    public void setUp() {
        numbers = Arrays.asList(1, 2, 3, 4, 5, 2, 3);
        strings = Arrays.asList("apple", "banana", "cherry", "date");
        executor = Executors.newFixedThreadPool(2);
    }

    @Test
    public void testFilterOperation() {
        List<Integer> evenNumbers = new Stream<>(numbers)
                .filter(n -> n % 2 == 0)
                .toList();
        assertEquals(Arrays.asList(2, 4, 2), evenNumbers);
    }

    @Test
    public void testMapOperation() {
        List<Integer> doubled = new Stream<>(numbers)
                .map(n -> n * 2)
                .toList();
        assertEquals(Arrays.asList(2, 4, 6, 8, 10, 4, 6), doubled);
    }

    @Test
    public void testDistinctOperation() {
        List<Integer> distinct = new Stream<>(numbers)
                .distinct()
                .toList();
        assertEquals(Arrays.asList(1, 2, 3, 4, 5), distinct);
    }

    @Test
    public void testFlatMapOperation() {
        List<List<Integer>> nested = Arrays.asList(
                Arrays.asList(1, 2),
                Arrays.asList(3, 4)
        );
        List<Integer> flattened = new Stream<>(nested)
                .flatMap(list -> new Stream<>(list))
                .toList();
        assertEquals(Arrays.asList(1, 2, 3, 4), flattened);
    }

    @Test
    public void testSortedOperation() {
        List<Integer> sorted = new Stream<>(numbers)
                .sorted()
                .toList();
        assertEquals(Arrays.asList(1, 2, 2, 3, 3, 4, 5), sorted);
    }

    @Test
    public void testLimitOperation() {
        List<Integer> limited = new Stream<>(numbers)
                .limit(3)
                .toList();
        assertEquals(Arrays.asList(1, 2, 3), limited);
    }

    @Test
    public void testSkipOperation() {
        List<Integer> skipped = new Stream<>(numbers)
                .skip(2)
                .toList();
        assertEquals(Arrays.asList(3, 4, 5, 2, 3), skipped);
    }

    @Test
    public void testReduceOperation() {
        Integer sum = new Stream<>(numbers)
                .reduce(Integer::sum, 0);
        assertEquals(Integer.valueOf(20), sum);
    }

    @Test
    public void testReduceOperationWithAccumulator() {
        Integer product = new Stream<>(numbers)
                .reduce((a, b) -> a * b).orElse(1);
        assertEquals(Integer.valueOf(720), product);
    }

    @Test
    public void testReduceOperationWithAccumulatorAndEmptyInput() {
        Integer product = new Stream<>(numbers)
                .limit(0)
                .reduce((a, b) -> a * b).orElse(1);
        assertEquals(Integer.valueOf(1), product);
    }

    @Test
    public void testReduceOperationWithAccumulatorAndIdentity() {
        Integer product = new Stream<>(numbers)
                .reduce((a, b) -> a * b, 2);
        assertEquals(Integer.valueOf(1440), product);
    }

    @Test
    public void testCountOperation() {
        long count = new Stream<>(numbers)
                .filter(n -> n > 2)
                .count();
        assertEquals(4, count);
    }

    @Test
    public void testToSetOperation() {
        Set<Integer> set = new Stream<>(numbers)
                .toSet();
        assertEquals(new HashSet<>(Arrays.asList(1, 2, 3, 4, 5)), set);
    }

    @Test
    public void testToMapOperation() {
        Map<Integer, String> map = new Stream<>(numbers)
                .distinct()
                .toMap(Function.identity(), n -> "Number: " + n);
        assertEquals(5, map.size());
        assertEquals("Number: 1", map.get(1));
        assertEquals("Number: 2", map.get(2));
        assertEquals("Number: 3", map.get(3));
        assertEquals("Number: 4", map.get(4));
        assertEquals("Number: 5", map.get(5));
    }

    @Test
    public void testToMapWithAccumulatorOperation() {
        Map<Integer, String> map = new Stream<>(numbers)
                .toMap(Function.identity(), n -> "Number: " + n, (a, b) -> a);
        assertEquals(5, map.size());
        assertEquals("Number: 1", map.get(1));
        assertEquals("Number: 2", map.get(2));
        assertEquals("Number: 3", map.get(3));
        assertEquals("Number: 4", map.get(4));
        assertEquals("Number: 5", map.get(5));
    }

    @Test
    public void testParallelFilterOperation() {
        List<Integer> result = new Stream<>(numbers)
                .parallel(executor)
                .filter(n -> n > 2)
                .toList();
        assertEquals(Arrays.asList(3, 4, 5, 3), result);
    }

    @Test
    public void testPrintOperation() {
        new Stream<>(strings)
                .print();
    }

    @Test
    public void testParallelMapOperation() {
        List<String> result = new Stream<>(numbers)
                .parallel()
                .map(String::valueOf)
                .toList();
        assertEquals(7, result.size());
    }

    @Test
    public void testJoiningOperation() {

        String joined = new Stream<>(numbers)
                .map(String::valueOf)
                .joining();
        assertEquals("1234523", joined);
    }

    @Test
    public void testJoiningOperationWithSeperator() {
        String joined = new Stream<>(strings)
                .joining(",");
        assertEquals("apple,banana,cherry,date", joined);
    }

    @Test
    public void testGroupingOperation() {
        Map<Integer, List<String>> grouped = new Stream<>(strings)
                .toGroupedMap(
                        String::length,
                        s -> s
                );
        assertEquals(3, grouped.size());
        assertTrue(grouped.containsKey(4));
        assertTrue(grouped.containsKey(5));
        assertTrue(grouped.containsKey(6));
    }

    @Test(expected = NullPointerException.class)
    public void testNullCollection() {
        new Stream<>(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNullPredicate() {
        new Stream<>(numbers).filter(null);
    }

    @Test
    public void testPeekOperation() {
        List<Integer> peeked = new ArrayList<>();
        List<Integer> result = new Stream<>(numbers)
                .peek(peeked::add)
                .filter(n -> n > 2)
                .toList();
        assertEquals(numbers, peeked);
        assertEquals(Arrays.asList(3, 4, 5, 3), result);
    }

    @Test
    public void testMinOperation() {
        Integer min = new Stream<>(numbers)
                .min(Integer::compareTo);
        assertEquals(Integer.valueOf(1), min);
    }

    @Test
    public void testSumOperation() {
        int sum = new Stream<>(strings)
                .map(String::length)
                .sum(x-> x)
                .intValue();
        assertEquals(21, sum);
    }

    @Test
    public void testMaxOperation() {
        Integer max = new Stream<>(numbers)
                .max(Integer::compareTo);
        assertEquals(Integer.valueOf(5), max);
    }

    @Test
    public void testAverageOperation() {
        Double avg = new Stream<>(numbers)
                .average(Integer::doubleValue);
        assertEquals(2.857142857142857, avg, 0.000001);
    }

    @Test
    public void testFindOperation() {
        Optional<Integer> first = new Stream<>(numbers)
                .find(n -> n % 2 == 0);
        assertTrue(first.isPresent());
    }

    @Test
    public void testAnyMatchOperation() {
        boolean hasEven = new Stream<>(numbers)
                .anyMatch(n -> n % 2 == 0);
        assertTrue(hasEven);
    }

    @Test
    public void testAllMatchOperation() {
        boolean allPositive = new Stream<>(numbers)
                .allMatch(n -> n > 0);
        assertTrue(allPositive);
    }

    @Test
    public void testNoneMatchOperation() {
        boolean noNegatives = new Stream<>(numbers)
                .noneMatch(n -> n < 0);
        assertTrue(noNegatives);
    }

    @Test
    public void testIteratorOperation() {
        Iterator<Integer> iterator = new Stream<>(numbers)
                .iterator();
        List<Integer> result = new ArrayList<>();
        while (iterator.hasNext()) {
            result.add(iterator.next());
        }
        assertEquals(numbers, result);
    }

    @Test
    public void testToArrayOperation() {
        Object[] array = new Stream<>(numbers)
                .toArray();
        assertArrayEquals(numbers.toArray(), array);
    }

    @Test
    public void testIterateOperation() {
        List<Stream.Pair<Long, Integer>> iterated = new Stream<>(numbers)
                .iterate()
                .toList();
        assertEquals(7, iterated.size());
        for (int i = 0; i < iterated.size(); i++) {
            assertEquals(Long.valueOf(i), iterated.get(i).key());
            assertEquals(numbers.get(i % numbers.size()), iterated.get(i).value());
        }
    }
}