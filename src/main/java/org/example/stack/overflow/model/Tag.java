package org.example.stack.overflow.model;

import lombok.Data;

import java.util.Objects;

@Data
public final class Tag {
    private final int tagId;
    private final String name;
    private final int questionId;

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
