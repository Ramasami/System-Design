package org.example.stack.overflow.service;

import lombok.NonNull;
import org.example.stack.overflow.model.Tag;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class TagService {
    private static final Logger logger = Logger.getLogger(TagService.class.getName());
    private static volatile TagService instance;

    private final AtomicInteger tagIdCounter;
    private final Map<Integer, Tag> tags;
    private final QuestionService questionService;

    private TagService() {
        this.tagIdCounter = new AtomicInteger(0);
        this.tags = new ConcurrentHashMap<>();
        this.questionService = QuestionService.getInstance();
    }

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

    public Tag addTag(int questionId, @NonNull String tagName) {
        validateTagName(tagName);
        questionService.getQuestion(questionId); // Validates question exists

        int tagId = tagIdCounter.incrementAndGet();
        Tag tag = new Tag(tagId, tagName, questionId);
        tags.put(tagId, tag);

        questionService.addTagToQuestion(questionId, tag);
        logger.info(() -> String.format("Added tag '%s' to question %d", tagName, questionId));

        return tag;
    }

    public Tag getTag(int tagId) {
        validateTagId(tagId);
        Tag tag = tags.get(tagId);
        if (tag == null) {
            throw new IllegalArgumentException("Tag not found with ID: " + tagId);
        }
        return tag;
    }

    private void validateTagId(int tagId) {
        if (tagId <= 0) {
            throw new IllegalArgumentException("Tag ID must be positive");
        }
    }

    private void validateTagName(String tagName) {
        if (tagName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tag name cannot be empty");
        }
    }
}