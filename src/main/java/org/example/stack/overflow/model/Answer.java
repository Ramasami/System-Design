package org.example.stack.overflow.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
@RequiredArgsConstructor
public final class Answer {
    private final int answerId;
    private final String content;
    private final int author;
    private final int questionId;
    private final Date creationDate;
    private final List<Comment> comments = Collections.synchronizedList(new ArrayList<>());
    private final Set<Vote> votes = Collections.newSetFromMap(new ConcurrentHashMap<>());
    private boolean accepted = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Answer answer)) return false;
        return answerId == answer.answerId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(answerId);
    }
}