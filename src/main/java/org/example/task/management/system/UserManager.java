package org.example.task.management.system;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class UserManager {

    private static final Map<Integer, User> users = new ConcurrentHashMap<>();
    private static final AtomicInteger userIdCounter = new AtomicInteger(1);

    public static Integer createUser(String username, String email) {
        int userId = userIdCounter.getAndIncrement();
        User user = new User(userId, username, email);
        users.put(userId, user);
        return userId;
    }

    public static boolean updateUserName(int userId, String newUsername) {
        return Objects.nonNull(users.computeIfPresent(userId, (id, user) -> {
            user.setUsername(newUsername);
            return user;
        }));
    }

    public static boolean updateEmail(int userId, String newEmail) {
        return Objects.nonNull(users.computeIfPresent(userId, (id, user) -> {
            user.setEmail(newEmail);
            return user;
        }));
    }

    public static boolean deleteUser(int userId) {
        return Objects.nonNull(users.remove(userId));
    }

    public static boolean userExists(int userId) {
        return users.containsKey(userId);
    }
}
