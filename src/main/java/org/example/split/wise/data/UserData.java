package org.example.split.wise.data;

import lombok.NoArgsConstructor;
import org.example.split.wise.model.Split;
import org.example.split.wise.model.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class UserData {

    private static volatile UserData INSTANCE;
    private final Map<Integer, User> users = new ConcurrentHashMap<>();
    private final AtomicInteger userId = new AtomicInteger(0);

    public static UserData getInstance() {
        if (INSTANCE == null) {
            synchronized (UserData.class) {
                if (INSTANCE == null) {
                    INSTANCE = new UserData();
                }
            }
        }
        return INSTANCE;
    }

    public User addUser(String name, String email) {
        int id = userId.incrementAndGet();
        User user = new User(id, name, email);
        users.put(id, user);
        return user;
    }


    public boolean isUserExists(int userId) {
        return users.containsKey(userId);
    }

    public User getUser(int userId) {
        return users.get(userId);
    }

    public void addSplit(int userId, Split split) {
        if (!isUserExists(userId)) {
            throw new IllegalArgumentException("User does not exist");
        }
        if (split == null) {
            throw new IllegalArgumentException("Invalid split parameter");
        }
        if (split.getLenderId() != userId && split.getBorrowerId() != userId) {
            throw new IllegalArgumentException("Split doesn't belong to the user");
        }
        getUser(userId).getSplits().add(split);
    }
}
