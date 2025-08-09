package org.example.splitwise.factory;

import org.example.splitwise.model.Expense;
import org.example.splitwise.model.SplitEnum;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

public class ExpenseService {
    private static int expenseIdCounter = 1;


    public static boolean addSingleExpense(Integer fromUserId, Integer toUserId, Double amount, SplitEnum splitType) {
        if (!UserService.isUserPresent(fromUserId) || !UserService.isUserPresent(toUserId)) {
            return false;
        }
        Map<Integer, Double> paidTo;
        if (splitType == SplitEnum.EQUAL) {
            paidTo = splitSimple(Map.of(fromUserId, amount), toUserId);
        } else if (splitType == SplitEnum.EXACT) {
            paidTo = Map.of(toUserId, amount);
        } else {
            return false;
        }
        if (paidTo == null) {
            return false;
        }
        Expense expense = new Expense(expenseIdCounter++, amount, null, Map.of(fromUserId, amount), paidTo);

        UserService.getUserById(fromUserId).addExpense(expense);
        UserService.getUserById(toUserId).addExpense(expense);
        return true;
    }

    public static boolean addMultiplePayExpense(Map<Integer, Double> fromUserId, Map<Integer, Double> toUserId, Double amount, Integer groupId) {
        if (Stream.concat(fromUserId.keySet().stream(), toUserId.keySet().stream())
                .distinct()
                .anyMatch(userId -> !UserService.isUserPresent(userId)))
            return false;

        if (groupId != null
                && (!GroupService.isGroupPresent(groupId)
                || !GroupService.getGroupById(groupId).getUsers().containsAll(fromUserId.keySet())
                || !GroupService.getGroupById(groupId).getUsers().containsAll(toUserId.keySet()))) {
            return false;
        }

        Expense expense = new Expense(expenseIdCounter++, amount, groupId, fromUserId, toUserId);

        Stream.concat(fromUserId.keySet().stream(), toUserId.keySet().stream())
                .distinct()
                .forEach(userId -> UserService.getUserById(userId).addExpense(expense));

        if (groupId != null) {
            GroupService.getGroupById(groupId).addExpense(expense);
        }

        return true;
    }

    public static void getExpenses(int userId) {
        if (!UserService.isUserPresent(userId)) {
            return;
        }
//        Set.of()
        UserService.getUserById(userId).getExpenses().forEach(System.out::println);
    }

    public static void getExpenses(int fromUser, int toUserId) {
        if (!UserService.isUserPresent(fromUser) || !UserService.isUserPresent(toUserId)) {
            return;
        }
//        UserService.getUserById(fromUser).getExpenses().stream()
//                .filter(expense -> paidBy(fromUser, toUserId, expense) || paidBy(toUserId, fromUser, expense))
//                .map(expense -> {
//                    calculateAmount(expense, fromUser, toUserId);
//                })
//                .forEach(System.out::println);
    }

    private static Double calculateAmount(Expense expense, int fromUser, int toUserId) {
        Double totalPaid = expense.getPayedBy().values().stream().reduce(0.0, Double::sum);
        if (expense.getPayedBy().containsKey(fromUser) && expense.getPayedTo().containsKey(toUserId)) {
            return (expense.getPayedBy().get(fromUser) / totalPaid);
        } else if (expense.getPayedBy().containsKey(toUserId) && expense.getPayedTo().containsKey(fromUser)) {
            return (totalPaid / 2) - expense.getPayedBy().get(toUserId);
        }
        return null;
    }

    private static boolean paidBy(int fromUser, int toUserId, Expense expense) {
        return expense.getPayedBy().containsKey(fromUser) && expense.getPayedTo().containsKey(toUserId);
    }

    public static Map<Integer, Double> splitSimple(Map<Integer, Double> paidBy, int... paidTo) {
        double totalAmount = 0;
        for (double amount : paidBy.values()) {
            totalAmount += amount;
        }
        double splitAmount = totalAmount / paidTo.length;
        Map<Integer, Double> splitMap = new HashMap<>();

        for (int userId : paidTo) {
            if (!UserService.isUserPresent(userId)) {
                return null;
            }
            splitMap.put(userId, splitAmount);
        }
        return splitMap;
    }
}
