package org.example.split.wise.factory;

import org.example.split.wise.model.PaymentType;
import org.example.split.wise.model.Transaction;

import java.util.Map;

public class TransactionFactory {
    public static Transaction createTransaction(PaymentType paymentType, int id, int lenderId, String description, double amount, Map<Integer, Double> lendeeShares, Integer groupId) {
        validateInputs(paymentType, id, lenderId, description, amount, lendeeShares);
        return switch (paymentType) {
            case EQUAL -> new EqualTransaction(id, lenderId, description, amount, lendeeShares, groupId);
            case EXACT -> new ExactTransaction(id, lenderId, description, amount, lendeeShares, groupId);
            case PERCENTAGE -> new PercentageTransaction(id, lenderId, description, amount, lendeeShares, groupId);
        };
    }

    private static void validateInputs(PaymentType paymentType, int id, int lenderId, String description, double amount, Map<Integer, Double> lendeeShares) {
        if (lendeeShares == null || lendeeShares.isEmpty()) {
            throw new IllegalArgumentException("Lendee shares cannot be null or empty");
        }
        if (lenderId < 0 || id <= 0 || amount <= 0 || description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Invalid transaction parameters");
        }
        if (paymentType == null) {
            throw new IllegalArgumentException("Payment type cannot be null");
        }
    }
}
