package org.example.split.wise.manager;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.split.wise.model.*;
import org.example.split.wise.service.GroupService;
import org.example.split.wise.service.TransactionService;
import org.example.split.wise.service.UserService;

import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SplitWiseManager {

    private static volatile SplitWiseManager INSTANCE;

    public static SplitWiseManager getInstance() {
        if (INSTANCE == null) {
            synchronized (SplitWiseManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new SplitWiseManager();
                }
            }
        }
        return INSTANCE;
    }

    public User createUser(String name, String email) {
        return UserService.getInstance().createUser(name, email);
    }

    public Transaction createTransaction(PaymentType paymentType, int lenderId, String description, double amount, Map<Integer, Double> lendeeShares, Integer groupId) {
        return TransactionService.getInstance().createTransaction(paymentType, lenderId, description, amount, lendeeShares, groupId);
    }

    public User getUser(int userId) {
        return  UserService.getInstance().getUser(userId);
    }

    public Group createGroup(String name, String description) {
        return GroupService.getInstance().addGroup(name, description);
    }

    public boolean addUserToGroup(int groupId, int userId) {
        return GroupService.getInstance().addUserToGroup(groupId, userId);
    }

    public Group getGroup(int groupId) {
        return GroupService.getInstance().getGroup(groupId);
    }

    public List<Ledger> getTransactions(int fromId) {
        return TransactionService.getInstance().getTransactions(fromId);
    }

    public Ledger getTransactions(int fromId, int toId) {
        return TransactionService.getInstance().getTransactions(fromId, toId);
    }

    public List<Ledger> getGroupTransactions(int groupId) {
        return TransactionService.getInstance().getGroupTransactions(groupId);
    }

    public List<Ledger> getGroupTransactions(int groupId, int fromId) {
        return TransactionService.getInstance().getGroupTransactions(groupId)
                .stream().filter(ledger -> ledger.getFromUser() == fromId)
                .toList();
    }

    public List<Ledger> getGroupTransactions(int groupId, int fromId, int toId) {
        return TransactionService.getInstance().getGroupTransactions(groupId)
                .stream()
                .filter(ledger -> ledger.getFromUser() == fromId)
                .filter(ledger -> ledger.getToUser() == toId)
                .toList();
    }

    public boolean settleExpenses(int fromId, int toId) {
        return TransactionService.getInstance().settleExpenses(fromId, toId);
    }
}
