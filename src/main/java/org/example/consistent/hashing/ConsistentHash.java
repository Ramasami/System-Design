package org.example.consistent.hashing;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;
import org.w3c.dom.Node;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Map;
import java.util.TreeMap;

@Data
public class ConsistentHash {

    private int size;
    private int virtualNodes;
    private TreeMap<Integer, String> nodes;

    public ConsistentHash(int size, int virtualNodes) {
        this.size = size;
        this.virtualNodes = virtualNodes;
        this.nodes = new TreeMap<>();
    }

    public ConsistentHash addServer(String server) {

        int key;
        int i = 0;
        for (int node = 0; node <= virtualNodes; node++) {
            do {
                key = hash(server, i++);
            } while (nodes.containsKey(key));

            nodes.put(key, server + node);
        }
        return this;
    }

    @SneakyThrows
    public static String getMd5(String input) {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] messageDigest = md.digest(input.getBytes());
        BigInteger no = new BigInteger(1, messageDigest);
        StringBuilder hashText = new StringBuilder(no.toString(16));
        while (hashText.length() < 32) {
            hashText.insert(0, "0");
        }
        return hashText.toString();
    }

    public int hash(String key, Integer salt) {
        return ((getMd5(key + salt.toString()).hashCode()  % size ) + size) % size;
    }

    public String getServer(String message) {
        if (nodes.isEmpty())
            return null;
        int key =  hash(message, 0);
        Map.Entry<Integer, String> serverEntry = nodes.ceilingEntry(key);
        if (serverEntry != null) {
            return serverEntry.getValue();
        } else {
            return nodes.ceilingEntry(0).getValue();
        }

    }

    @Override
    public String toString() {
        return "ConsistentHash{" +
                "nodes=" + nodes +
                '}';
    }
}
