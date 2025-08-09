package org.example.pub.sub;

import lombok.SneakyThrows;

import java.util.Date;

public class PubSubApplication {
    @SneakyThrows
    public static void main(String[] args) {
        PubSubService pubSubService = new PubSubService();

        // Create a topic
        String topicName = "exampleTopic";
        pubSubService.createTopic(topicName);

        // Create a subscriber
        Subscriber subscriber1 = () -> message -> System.out.println("Received message 1: " + message.getContent());
        Subscriber subscriber2 = () -> message -> System.out.println("Received message 2: " + message.getContent());
        Subscriber consoleSubscriber = new ConsoleSubscriber();

        // Add the subscriber to the topic
        pubSubService.addSubscriber(topicName, subscriber1);
        pubSubService.addSubscriber(topicName, subscriber2);
        pubSubService.addSubscriber(topicName, consoleSubscriber);

        // Create and add a message to the topic
        Thread t1 = new Thread(() -> {
            publish(topicName, pubSubService);
        });
        Thread t2 = new Thread(() -> {
            publish(topicName, pubSubService);
        });
        t1.start();
        t2.start();
        Thread.sleep(10000); // Wait for messages to be processed
    }

    private static void publish(String topicName, PubSubService pubSubService) {
        for(int i=0;i<1000000;i++) {
            Message message = new Message("Hello, " + i + " World! trhead: " + Thread.currentThread().getName(), topicName, new Date());
            pubSubService.addMessage(topicName, message);
        }
    }
}
