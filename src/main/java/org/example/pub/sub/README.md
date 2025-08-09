classDiagram
class PubSubService {
- Map~String, Topic~ topics
+ createTopic(String topicName)
+ addMessage(String topicName, Message message) : boolean
+ addSubscriber(String topicName, Subscriber subscriber) : boolean
+ deleteSubscriber(String topicName, Subscriber subscriber) : boolean
}

    class Topic {
        - String name
        - Set~Subscriber~ subscribers
        - LinkedBlockingQueue~Message~ messages
        - Thread daemonThread
        - AtomicBoolean running
        + Topic(String name)
        + addSubscriber(Subscriber subscriber) : boolean
        + removeSubscriber(Subscriber subscriber) : boolean
        + addMessage(Message message) : boolean
        + shutdown()
    }

    class Subscriber {
        <<interface>>
        + consume() : Consumer~Message~
    }

    class Message {
        - String content
        - String topicName
        - Date timestamp
        + getContent() : String
        + getTopicName() : String
        + getTimestamp() : Date
    }

    PubSubService --> Topic : manages
    Topic --> Subscriber : notifies
    Topic --> Message : processes
    Subscriber --> Message : consumes