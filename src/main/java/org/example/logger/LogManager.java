package org.example.logger;

import lombok.Getter;
import lombok.SneakyThrows;

import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;

public class LogManager {
    @Getter
    private static final LogManager instance = new LogManager();
    private final Map<LogAppender, ILevel> consumers = new HashMap<>();
    private final Queue<Message> messageQueue = new LinkedBlockingQueue<>();
    private final Thread deamonThread;

    private LogManager() {
        deamonThread = new Thread(this::processMessages);
        deamonThread.setDaemon(true);
        deamonThread.start();
    }

    public void registerNewConsumer(LogAppender consumer, ILevel level) {
        consumers.put(consumer, level);
    }

    public void unregisterNewConsumer(LogAppender consumer) {
        consumers.remove(consumer);
    }

    public synchronized void appendMessage(Message message) {
        messageQueue.offer(message);
    }

    @SneakyThrows
    private void processMessages() {
        while (true) {
            pushLogs();
            Thread.sleep(10);
        }
    }

    private synchronized void pushLogs() {
        int size = messageQueue.size();
        List<Message> messages = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            messages.add(messageQueue.poll());
        }
        consumers.forEach((consumer, level) -> {
            List<Message> consumerMessages = messages.stream().filter(message -> message.level().getPriority() >= level.getPriority()).toList();
            consumer.append(consumerMessages);
        });
    }
}
