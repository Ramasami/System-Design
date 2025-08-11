package org.example.split.wise.model;

import lombok.Data;

import java.util.*;

@Data
public class Group {
    final private int id;
    final private String name;
    final private String description;
    final private Date createdAt;
    final private Set<Integer> members;
    final private List<Transaction> transactions;

    public Group(int id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.createdAt = new Date();
        this.members = Collections.synchronizedSet(new HashSet<>());
        this.transactions = Collections.synchronizedList(new ArrayList<>());
    }

    public boolean addMember(Integer user) {
        return members.add(user);
    }

    public boolean addTransaction(Transaction transaction) {
        return transactions.add(transaction);
    }
}
