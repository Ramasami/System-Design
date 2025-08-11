package org.example.stack.overflow.model;

import lombok.Data;

import java.util.Objects;

@Data
public final class Vote {
    private final Integer voterId;
    private final int userId;
    private final VoteType voteType;
    private final VotedOn votedOn;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vote vote)) return false;
        return userId == vote.userId && Objects.equals(voterId, vote.voterId) && votedOn == vote.votedOn;
    }

    @Override
    public int hashCode() {
        return Objects.hash(voterId, userId, votedOn);
    }
}
