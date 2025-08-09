package org.example.logger;

import java.util.List;

public abstract class LogAppender {

    public final String name;
    public final ILevel level;

    public abstract void append(List<Message> messageList);

    public LogAppender(String name, ILevel level) {
        this.name = name;
        this.level = level;
        register();
    }

    public final void close() {
        LogManager.getInstance().unregisterNewConsumer(this);
    }

    public final void register() {
        LogManager.getInstance().registerNewConsumer(this, level);
    }
}
