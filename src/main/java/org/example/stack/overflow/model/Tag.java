package org.example.stack.overflow.model;

import java.util.Objects;

public record Tag(
        int tagId,
        String name,
        int questionId) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Tag tag)) return false;
        return tagId == tag.tagId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(tagId);
    }
}
