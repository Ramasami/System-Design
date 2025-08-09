package org.example.pub.sub;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class PubSubService {

    private static final Map<String, Topic> topics = new ConcurrentHashMap<>();

    public boolean addMessage(String topicName, Message message) {
        if (message == null || topicName == null || topicName.isEmpty()) {
            throw new IllegalArgumentException("Message or topic name cannot be null or empty");
        }
        return Objects.nonNull(topics.computeIfPresent(topicName, (_key, topic) -> {
            topic.addMessage(message);
            return topic;
        }));
    }

    public void createTopic(String topicName) {
        if (topicName == null || topicName.isEmpty()) {
            throw new IllegalArgumentException("Topic name cannot be null or empty");
        }
        topics.computeIfAbsent(topicName, Topic::new);
    }

    public boolean addSubscriber(String topicName, Subscriber subscriber) {
        if (subscriber == null || topicName == null || topicName.isEmpty()) {
            throw new IllegalArgumentException("Subscriber or topic name cannot be null or empty");
        }
        Topic topic = topics.get(topicName);
        if (topic != null) {
            return topic.addSubscriber(subscriber);
        } else {
            throw new IllegalArgumentException("Topic does not exist: " + topicName);
        }
    }

    public boolean deleteSubscriber(String topicName, Subscriber subscriber) {
        if (subscriber == null || topicName == null || topicName.isEmpty()) {
            throw new IllegalArgumentException("Subscriber or topic name cannot be null or empty");
        }
        Topic topic = topics.get(topicName);
        if (topic != null) {
            return topic.removeSubscriber(subscriber);
        } else {
            throw new IllegalArgumentException("Topic does not exist: " + topicName);
        }
    }
}
