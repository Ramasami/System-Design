package org.example.logger;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum Level implements ILevel {
    DEBUG(100),
    INFO(200),
    WARN(300),
    ERROR(400),
    FATAL(500);

    private final int priority;

    @Override
    public int getPriority() {
        return priority;
    }
}
