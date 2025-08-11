package org.example.split.wise.service;

import lombok.NoArgsConstructor;
import org.example.split.wise.data.TransactionData;
import org.example.split.wise.model.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class TransactionService {

    private static volatile TransactionService INSTANCE;

    public static TransactionService getInstance() {
        if (INSTANCE == null) {
            synchronized (TransactionService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TransactionService();
                }
            }
        }
        return INSTANCE;
    }

    public Transaction createTransaction(PaymentType paymentType, int lenderId, String description, double amount, Map<Integer, Double> lendeeShares, Integer groupId) {
        validateInputs(paymentType, lenderId, description, amount, lendeeShares, groupId);
        Transaction transaction = TransactionData.getInstance().addTransaction(paymentType, lenderId, description, amount, lendeeShares, groupId);
        UserService.getInstance().addTransactionToUsers(transaction);
        if (groupId != null) {
            GroupService.getInstance().addTransactionToGroup(groupId, transaction);
        }
        return transaction;
    }

    private void validateInputs(PaymentType paymentType, int lenderId, String description, double amount, Map<Integer, Double> lendeeShares, Integer groupId) {
        if (lenderId < 0) {
            throw new IllegalArgumentException("lenderId can't be negative");
        }
        if (!UserService.getInstance().isUserExists(lenderId)) {
            throw new IllegalArgumentException("Lender does not exist");
        }
        if (lendeeShares == null || lendeeShares.isEmpty()) {
            throw new IllegalArgumentException("Lendee shares cannot be null or empty");
        }
        lendeeShares.keySet().forEach(lendeeId -> {
            if (!UserService.getInstance().isUserExists(lendeeId)) {
                throw new IllegalArgumentException("Lender does not exist");
            }
        });
        if (amount <= 0 || description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Invalid transaction parameters");
        }
        if (paymentType == null) {
            throw new IllegalArgumentException("Payment type cannot be null");
        }
        if (groupId != null && !GroupService.getInstance().isGroupExists(groupId)) {
            throw new IllegalArgumentException("Group does not exist");
        }
    }


    public List<Ledger> getTransactions(int fromId) {
        validateUserId(fromId);
        if (!UserService.getInstance().isUserExists(fromId)) {
            throw new IllegalArgumentException("User does not exist");
        }
        User user = UserService.getInstance().getUser(fromId);
        return user.getSplits().stream()
                .filter(Split::isEnabled)
                .map(split -> createLedger(fromId, split))
                .collect(Collectors.toMap(Ledger::getToUser, Function.identity(), this::aggregateLedger))
                .values().stream().filter(ledger -> ledger.getCost() != 0).toList();
    }


    public Ledger getTransactions(int fromId, int toId) {
        validateUserId(fromId);
        validateUserId(toId);
        User user = UserService.getInstance().getUser(fromId);
        return user.getSplits().stream()
                .filter(split -> (split.getLenderId() == toId && split.getBorrowerId() == fromId) || (split.getLenderId() == fromId && split.getBorrowerId() == toId))
                .filter(Split::isEnabled)
                .map(split -> createLedger(fromId, split))
                .reduce(new Ledger(fromId, toId), this::aggregateLedger);

    }

    private void validateTransactionId(int transactionId) {
        if (transactionId < 0) {
            throw new IllegalArgumentException("transactionId can't be negative");
        }
    }

    private Ledger createLedger(int userId, Split split) {
        if (split.getLenderId() == userId) {
            return new Ledger(userId, split.getBorrowerId()).addCost(split.getAmount());
        } else if (split.getBorrowerId() == userId) {
            return new Ledger(userId, split.getLenderId()).addCost(-split.getAmount());
        } else {
            throw new IllegalArgumentException("Invalid split id");
        }
    }

    private Ledger aggregateLedger(Ledger l1, Ledger l2) {
        return l1.addCost(l2.getCost());
    }

    private void validateUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("UserId can't be negative");
        }
        if (!UserService.getInstance().isUserExists(userId)) {
            throw new IllegalArgumentException("User does not exist");
        }
    }

    public List<Ledger> getGroupTransactions(int groupId) {
        validateGroupId(groupId);
        Group group = GroupService.getInstance().getGroup(groupId);
        return group.getTransactions().stream()
                .map(Transaction::getSplits)
                .flatMap(Collection::stream)
                .filter(Split::isEnabled)
                .map(split -> Arrays.asList(createLedger(split.getBorrowerId(), split), createLedger(split.getLenderId(), split)))
                .flatMap(Collection::stream)
                .collect(Collectors.toMap(ledger -> ledger.getFromUser() + "~" + ledger.getToUser(), Function.identity(), this::aggregateLedger))
                .values().stream().filter(ledger -> ledger.getCost() != 0)
                .toList();
    }

    private void validateGroupId(int groupId) {
        if (groupId <= 0) {
            throw new IllegalArgumentException("GroupId can't be negative");
        }
        if (!GroupService.getInstance().isGroupExists(groupId)) {
            throw new IllegalArgumentException("Group does not exist");
        }
    }

    public boolean settleExpenses(int fromId, int toId) {
        validateUserId(fromId);
        validateUserId(toId);
        Ledger ledger = getTransactions(fromId, toId);
        if (ledger.getCost() <= 0) return false;
        User user = UserService.getInstance().getUser(fromId);
        user.getSplits().stream()
                .filter(s -> s.getLenderId() == toId || s.getBorrowerId() == toId)
                .filter(s -> s.getLenderId() == fromId || s.getBorrowerId() == fromId)
                .forEach(split -> split.setEnabled(false));
        return true;
    }
}
