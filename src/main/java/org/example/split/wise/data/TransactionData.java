package org.example.split.wise.data;

import lombok.NoArgsConstructor;
import org.example.split.wise.factory.TransactionFactory;
import org.example.split.wise.model.Group;
import org.example.split.wise.model.PaymentType;
import org.example.split.wise.model.Transaction;
import org.example.split.wise.model.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class TransactionData {

    private static volatile TransactionData INSTANCE;
    private final Map<Integer, Transaction> transactions = new ConcurrentHashMap<>();
    private final AtomicInteger transactionId = new AtomicInteger(0);

    public static TransactionData getInstance() {
        if (INSTANCE == null) {
            synchronized (TransactionData.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TransactionData();
                }
            }
        }
        return INSTANCE;
    }

    public Transaction addTransaction(PaymentType paymentType, int lenderId, String description, double amount, Map<Integer, Double> lendeeShares, Integer groupId) {
        int id = transactionId.incrementAndGet();
        Transaction transaction = TransactionFactory.createTransaction(paymentType, id, lenderId, description, amount, lendeeShares, groupId);
        transactions.put(id, transaction);
        return transaction;
    }

    public Transaction getTransaction(int id) {
        return transactions.get(id);
    }




}
