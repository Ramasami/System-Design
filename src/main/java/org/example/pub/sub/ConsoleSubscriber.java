package org.example.pub.sub;

import java.util.function.Consumer;

public class ConsoleSubscriber implements Subscriber{
    @Override
    public Consumer<Message> consume() {
        return this::printMessage;
    }

    private void printMessage(Message message) {
        System.out.println("ConsoleSubscriber received message: " + message.getContent() +
                           " from topic: " + message.getTopicName() +
                           " at " + message.getTimestamp());
    }
}
