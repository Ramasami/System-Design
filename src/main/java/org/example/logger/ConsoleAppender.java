package org.example.logger;


import java.util.List;

/**
 * ConsoleAppender is a concrete implementation of LogAppender that outputs log messages to the console.
 * It formats the log messages with the date, class name, log level, and the actual message.
 */
public class ConsoleAppender extends LogAppender {

    /**
     * Constructs a ConsoleAppender with the specified name and log level.
     *
     * @param name  the name of the appender
     * @param level the log level for this appender
     */
    public ConsoleAppender(String name, ILevel level) {
        super(name, level);
    }

    /**
     * Appends a list of log messages to the console.
     * Each message is printed in a formatted manner.
     *
     * @param message the list of messages to append
     */
    @Override
    public void append(List<Message> message) {
        for (Message msg : message) {
            append(msg);
        }
    }

    /**
     * Appends a single log message to the console.
     * The message is formatted with the date, class name, log level, and the actual message.
     *
     * @param message the message to append
     */
    public void append(Message message) {
        System.out.printf("%s [%s] [%5s]: %s%n", message.date(), message.clazz().getSimpleName(), message.level(), message.message());
    }
}
