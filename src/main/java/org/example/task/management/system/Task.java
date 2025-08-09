package org.example.task.management.system;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;
import java.util.Objects;

@Data
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public final class Task {
    private int id;
    private String title;
    private String description;
    private Date dueDate;
    private Priority priority;
    private Status status;
    private Integer assignedUser;
    private Integer createdBy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Task task)) return false;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
}
