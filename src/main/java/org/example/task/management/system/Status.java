package org.example.task.management.system;

public enum Status {
    PENDING,
    IN_PROGRESS,
    COMPLETED,
    CANCELLED;

    public static Status fromString(String status) {
        for (Status s : Status.values()) {
            if (s.name().equalsIgnoreCase(status)) {
                return s;
            }
        }
        throw new IllegalArgumentException("Unknown status: " + status);
    }
}
