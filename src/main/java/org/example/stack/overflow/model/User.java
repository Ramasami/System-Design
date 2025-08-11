package org.example.stack.overflow.model;

import lombok.Data;

import java.util.Objects;

@Data
public class User {
    private final int userId;
    private final String username;
    private final Reputation reputation;

    public User(int userId, String username) {
        this.userId = userId;
        this.username = username;
        this.reputation = new Reputation(userId); // Default reputation
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User user)) return false;
        return userId == user.userId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userId);
    }
}
