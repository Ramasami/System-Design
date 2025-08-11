package org.example.stack.overflow.model;

import java.util.Date;
import java.util.Objects;

public record Comment(
        int commentId,
        String content,
        int authorId,
        int questionId,
        int answerId,
        Date creationDate) {

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
