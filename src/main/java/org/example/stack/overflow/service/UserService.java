package org.example.stack.overflow.service;

import lombok.NonNull;
import org.example.stack.overflow.model.Question;
import org.example.stack.overflow.model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class UserService {
    private static final Logger logger = Logger.getLogger(UserService.class.getName());
    private static volatile UserService instance;

    private final AtomicInteger userIdCounter;
    private final Map<Integer, User> users;
    private final Map<Integer, List<Question>> userQuestions;

    private UserService() {
        this.userIdCounter = new AtomicInteger(0);
        this.users = new ConcurrentHashMap<>();
        this.userQuestions = new ConcurrentHashMap<>();
    }

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

    public User createUser(@NonNull String username) {
        validateUsername(username);
        int userId = userIdCounter.incrementAndGet();
        User user = new User(userId, username);
        users.put(userId, user);
        logger.info(() -> String.format("Created user: %s with ID: %d", username, userId));
        return user;
    }

    public User getUser(int userId) {
        validateUserId(userId);
        User user = users.get(userId);
        if (user == null) {
            throw new IllegalArgumentException("User not found with ID: " + userId);
        }
        return user;
    }

    public boolean addQuestionToUser(int userId, @NonNull Question question) {
        User user = getUser(userId);
        userQuestions.computeIfAbsent(userId, k -> Collections.synchronizedList(new ArrayList<>()))
                .add(question);
        logger.info(() -> String.format("Added question ID: %d to user ID: %d",
                question.getQuestionId(), userId));
        return true;
    }

    public List<Question> getUserQuestions(int userId) {
        getUser(userId); // Validate user exists
        return Collections.unmodifiableList(
                userQuestions.getOrDefault(userId, Collections.emptyList())
        );
    }

    public int getReputation(int userId) {
        User user = getUser(userId);
        return user.getReputation().getReputation().get();
    }

    private void validateUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
    }

    private void validateUsername(String username) {
        if (username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
    }
}