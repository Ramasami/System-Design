package org.example.stack.overflow.service;

import lombok.AllArgsConstructor;
import org.example.stack.overflow.model.Question;
import org.example.stack.overflow.model.User;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UserService {
    private static UserService instance;
    private final AtomicInteger userIdCounter = new AtomicInteger(0);
    private final Map<Integer, User> users = new ConcurrentHashMap<>();
    private final Map<Integer, List<Question>> userQuestions = new ConcurrentHashMap<>();

    public static UserService getInstance() {
        if (instance == null) {
            synchronized (UserService.class) {
                if (instance == null) {
                    instance = new UserService();
                }
            }
        }
        return instance;
    }

    public User createUser(String username) {
        if (username == null || username.isBlank()) {
            throw new IllegalArgumentException("Username cannot be null or empty");
        }
        int userId = userIdCounter.incrementAndGet();
        User user = new User(userId, username);
        users.put(userId, user);
        return user;
    }

    public User getUser(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        return users.get(userId);
    }

    public boolean userExists(int userId) {
        return getUser(userId) != null;
    }

    public boolean addQuestionToUser(int userId, Question question) {
        if (!userExists(userId)) {
            throw new IllegalArgumentException("User does not exist");
        }
        if (question == null) {
            throw new IllegalArgumentException("Question cannot be null");
        }
        userQuestions.computeIfAbsent(userId, k -> new java.util.ArrayList<>()).add(question);
        return true;
    }

    public List<Question> getUserQuestions(int userId) {
        if (!userExists(userId)) {
            throw new IllegalArgumentException("User does not exist");
        }
        return userQuestions.getOrDefault(userId, new java.util.ArrayList<>());
    }

    public int getReputation(int userId) {
        if (!userExists(userId)) {
            throw new IllegalArgumentException("User does not exist");
        }
        User user = getUser(userId);
        return user.getReputation().getReputation().get();
    }
}
