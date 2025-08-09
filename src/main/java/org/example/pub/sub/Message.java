package org.example.pub.sub;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Message {
    private final String content;
    private final String topicName;
    private final Date timestamp;

    public String getContent() {
        return content;
    }

    public String getTopicName() {
        return topicName;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
