package org.example.pub.sub;


import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Topic {
    private final String name;
    private final Set<Subscriber> subscribers = new HashSet<>();
    private final LinkedBlockingQueue<Message> messages = new LinkedBlockingQueue<>();
    private final Thread daemonThread;
    private final AtomicBoolean running = new AtomicBoolean(true);

    public Topic(String name) {
        this.name = name;
        daemonThread = new Thread(this::publishToSubscribers);
        daemonThread.setDaemon(true);
        daemonThread.start();
    }

    private void publishToSubscribers() {
        while (running.get()) {
            try {
                Message message = messages.take();
                for (Subscriber subscriber : subscribers) {
                    subscriber.consume().accept(message);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running.set(false);
            } catch (Exception e) {
            }
        }
    }

    public boolean addSubscriber(Subscriber subscriber) {
        if (subscriber == null) {
            throw new IllegalArgumentException("Subscriber cannot be null");
        }
        return subscribers.add(subscriber);
    }

    public boolean removeSubscriber(Subscriber subscriber) {
        if (subscriber == null) {
            throw new IllegalArgumentException("Subscriber cannot be null");
        }
        return subscribers.remove(subscriber);
    }

    public boolean addMessage(Message message) {
        if (message == null || message.getTopicName() == null || !message.getTopicName().equals(name)) {
            throw new IllegalArgumentException("Message is null or does not belong to this topic: " + name);
        }
        return messages.offer(message);
    }

    public void shutdown() {
        running.set(false);
        daemonThread.interrupt();
    }
}
