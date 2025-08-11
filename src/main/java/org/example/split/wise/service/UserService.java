package org.example.split.wise.service;

import lombok.NoArgsConstructor;
import org.example.split.wise.data.UserData;
import org.example.split.wise.model.Split;
import org.example.split.wise.model.Transaction;
import org.example.split.wise.model.User;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UserService {

    private static volatile UserService INSTANCE;

    public static UserService getInstance() {
        if (INSTANCE == null) {
            synchronized (UserService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserService();
                }
            }
        }
        return INSTANCE;
    }

    public User createUser(String name, String email) {
        validateInputs(name, email);
        return UserData.getInstance().addUser(name, email);
    }

    public boolean isUserExists(int userId) {
        validateUserId(userId);
        return UserData.getInstance().isUserExists(userId);
    }

    public User getUser(int userId) {
        validateUserId(userId);
        User user = UserData.getInstance().getUser(userId);
        if (user == null) {
            throw new IllegalArgumentException("User with ID " + userId + " does not exist");
        }
        return user;
    }

    private void validateUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be greater than zero");
        }
    }

    private void validateInputs(String name, String email) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (email == null || email.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
    }

    public void addTransactionToUsers(Transaction transaction) {
        transaction.getSplits().stream().filter(Split::isEnabled).forEach(split -> {
            UserData.getInstance().addSplit(split.getBorrowerId(), split);
            UserData.getInstance().addSplit(split.getLenderId(), split);
        });
    }
}
