package org.example.stack.overflow.model;

import lombok.Data;
import lombok.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class Question {
    private final int questionId;
    private final String title;
    private final String content;
    private final int author;
    private final Date creationDate;
    private final List<Tag> tags = Collections.synchronizedList(new ArrayList<>());
    private final List<Answer> answers = Collections.synchronizedList(new ArrayList<>());
    private final Set<Vote> votes = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public Question(int questionId, @NonNull String title, @NonNull String content,
                    int author, @NonNull Date creationDate) {
        validateId(questionId, "Question ID");
        validateId(author, "Author ID");
        validateText(title, "Title");
        validateText(content, "Content");

        this.questionId = questionId;
        this.title = title.trim();
        this.content = content.trim();
        this.author = author;
        this.creationDate = creationDate;
    }

    private void validateId(int id, String fieldName) {
        if (id <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive");
        }
    }

    private void validateText(String text, String fieldName) {
        if (text.trim().isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
    }

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