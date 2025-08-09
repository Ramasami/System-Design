package org.example.splitwise.factory;

import org.example.splitwise.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class UserService {
    private static int userIdCounter = 1;
    private static Map<Integer, User> userIdMap = new HashMap<>();

    public synchronized static User createUser(String name) {
        User user = new User();
        user.setId(userIdCounter++);
        user.setName(name);
        user.setExpenses(new ArrayList<>());
        userIdMap.put(user.getId(), user);
        return user;
    }

    public static User getUserById(int id) {
        return userIdMap.get(id);
    }

    public static boolean isUserPresent(int id) {
        return userIdMap.containsKey(id);
    }


}
