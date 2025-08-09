package org.example.logger;


import java.util.List;

public class ConsoleAppender extends LogAppender {

    public ConsoleAppender(String name, ILevel level) {
        super(name, level);
    }

    @Override
    public void append(List<Message> message) {
        for (Message msg : message) {
            append(msg);
        }
    }

    public void append(Message message) {
        System.out.printf("%s [%s] [%5s]: %s%n", message.date(), message.clazz().getSimpleName(), message.level(), message.message());
    }
}
