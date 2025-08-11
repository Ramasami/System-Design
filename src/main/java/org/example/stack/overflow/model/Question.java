package org.example.stack.overflow.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.*;

@Data
@RequiredArgsConstructor
public final class Question {
    private final int questionId;
    private final String title;
    private final String content;
    private final int author;
    private final Date creationDate;
    private final List<Tag> tags = Collections.synchronizedList(new ArrayList<>());
    private final List<Answer> answers = Collections.synchronizedList(new ArrayList<>());
    private final Set<Vote> votes = new HashSet<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Question question)) return false;
        return questionId == question.questionId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(questionId);
    }
}
