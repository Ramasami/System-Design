package org.example.stack.overflow.model;

import lombok.Data;
import lombok.NonNull;

import java.util.Date;
import java.util.Objects;

@Data
public class Comment {
    private final int commentId;
    private final String content;
    private final int authorId;
    private final int questionId;
    private final int answerId;
    private final Date creationDate;

    public Comment(int commentId, @NonNull String content, int authorId,
                   int questionId, int answerId, @NonNull Date creationDate) {
        validateId(commentId, "Comment ID");
        validateId(authorId, "Author ID");
        validateId(questionId, "Question ID");
        validateId(answerId, "Answer ID");
        validateContent(content);

        this.commentId = commentId;
        this.content = content.trim();
        this.authorId = authorId;
        this.questionId = questionId;
        this.answerId = answerId;
        this.creationDate = creationDate;
    }

    private void validateId(int id, String fieldName) {
        if (id <= 0) {
            throw new IllegalArgumentException(fieldName + " must be positive");
        }
    }

    private void validateContent(String content) {
        if (content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Comment comment)) return false;
        return commentId == comment.commentId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(commentId);
    }
}