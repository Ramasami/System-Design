package org.example.split.wise.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
public abstract class Transaction {
    protected final int id;
    protected final int lenderId;
    protected final Map<Integer, Double> lendeeShares;
    protected final Date createdAt;
    protected final Integer groupId;
    protected String description;
    protected double amount;
    protected PaymentType paymentType;
    protected List<Split> splits = Collections.synchronizedList(new ArrayList<>());


    public Transaction(int id, int lenderId, String description, double amount, PaymentType paymentType, Map<Integer, Double> lendeeShares, Integer groupId) {
        validateLenders(lendeeShares);
        validateInputs(id, lenderId, description, amount);
        validateAdditionalLenderInformation(lenderId, lendeeShares, amount);
        this.id = id;
        this.lenderId = lenderId;
        this.description = description;
        this.amount = amount;
        this.paymentType = paymentType;
        this.lendeeShares = new ConcurrentHashMap<>(lendeeShares);
        this.createdAt = new Date();
        this.groupId = groupId;
        calculateCharges();
    }

    public static void validateInputs(int id, int lenderId, String description, double amount) {
        if (lenderId < 0 || id <= 0 || amount <= 0 || description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Invalid transaction parameters");
        }
    }

    private static void validateLenders(Map<Integer, Double> lendeeShares) {
        if (lendeeShares == null || lendeeShares.isEmpty()) {
            throw new IllegalArgumentException("Lendee shares cannot be null or empty");
        }
    }

    public abstract void validateAdditionalLenderInformation(int lender, Map<Integer, Double> lendeeShares, double amount);

    @JsonIgnore
    public List<Split> getParticipantShares() {
        return splits;
    }

    public abstract void calculateCharges();
}
