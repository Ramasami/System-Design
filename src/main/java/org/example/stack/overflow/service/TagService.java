package org.example.stack.overflow.service;

import lombok.AllArgsConstructor;
import org.example.stack.overflow.model.Tag;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class TagService {
    private static TagService instance;
    private final AtomicInteger tagCounter = new AtomicInteger(0);
    private final Map<Integer, Tag> tags = new ConcurrentHashMap<>();

    public static TagService getInstance() {
        if (instance == null) {
            synchronized (TagService.class) {
                if (instance == null) {
                    instance = new TagService();
                }
            }
        }
        return instance;
    }

    public Tag addTag(int questionId, String tagName) {
        if (!QuestionService.getInstance().questionExists(questionId)) {
            throw new IllegalArgumentException("Question does not exist");
        }
        if (tagName == null || tagName.isBlank()) {
            throw new IllegalArgumentException("Tag name cannot be null or empty");
        }
        int tagId = tagCounter.incrementAndGet();
        Tag tag = new Tag(tagId, tagName, questionId);
        QuestionService.getInstance().addTagToQuestion(questionId, tag);
        return tag;
    }
}
