package org.example.consistent.hashing;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ConsistentHashingApp {

    public static double calculateStandardDeviation(Collection<Double> array) {

        // get the sum of array
        double sum = 0.0;
        for (double i : array) {
            sum += i;
        }

        // get the mean of array
        int length = array.size();
        double mean = sum / length;

        // calculate the standard deviation
        double standardDeviation = 0.0;
        for (double num : array) {
            standardDeviation += Math.pow(num - mean, 2);
        }

        return Math.sqrt(standardDeviation / length);
    }

    public static void main(String[] args) {
        ConsistentHash consistentHash = new ConsistentHash(256, 32)
                .addServer("A")
                .addServer("B")
                .addServer("C")
                .addServer("D");

        System.out.println(consistentHash);

        Map<String, Double> frequencies = new HashMap<>();
        int n = 1000000;
        for (int i = 0; i < n; i++) {
            String message = String.valueOf(i);
            String server = consistentHash.getServer(message);
//            System.out.println(consistentHash.hash(message, 0) + " " + server);
            frequencies.compute(server, (k, v) -> v == null ? 1 : v + 1);
        }
        System.out.println(frequencies);
        System.out.println(100 * calculateStandardDeviation(frequencies.values())/ n);
    }
}
