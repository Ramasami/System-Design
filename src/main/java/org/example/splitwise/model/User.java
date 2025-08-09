package org.example.splitwise.model;

import lombok.Data;

import java.util.List;

@Data
public class User {
    private int id;
    private String name;
    private List<Expense> expenses;

    public void addExpense(Expense expense) {
        expenses.add(expense);
    }
}
