package org.example.splitwise.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

@Data
@AllArgsConstructor
public class Expense {
    private int id;
    private double amount;
    private Integer groupId;
    private Map<Integer, Double> payedBy;
    private Map<Integer, Double> payedTo;
}
