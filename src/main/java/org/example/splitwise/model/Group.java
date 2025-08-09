package org.example.splitwise.model;

import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
public class Group {
    private int id;
    private String groupName;
    private Set<Integer> users;
    private List<Expense> expenses;

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }
}
