package org.example.logger;

import java.util.Date;

public record Message(Date date, String message, Class<?> clazz, ILevel level) {
}
