package org.example.logger;

import lombok.AllArgsConstructor;

import java.util.Date;

@AllArgsConstructor
public class Logger {
    private final Class<?> clazz;

    public void log(ILevel level, String message) {
        LogManager.getInstance().appendMessage(new Message(new Date(), message, clazz, level));
    }

    public void debug(String message) {
        log(Level.DEBUG, message);
    }

    public void info(String message) {
        log(Level.INFO, message);
    }

    public void warn(String message) {
        log(Level.WARN, message);
    }

    public void error(String message) {
        log(Level.ERROR, message);
    }

    public void fatal(String message) {
        log(Level.FATAL, message);
    }
}
