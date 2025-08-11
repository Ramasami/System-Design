package org.example.stack.overflow.service;

import lombok.AllArgsConstructor;
import org.example.stack.overflow.model.Comment;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class CommentService {
    private static CommentService instance;
    private final AtomicInteger commentIdCounter = new AtomicInteger(0);
    private final Map<Integer, Comment> commentsMap = new ConcurrentHashMap<>();

    public static CommentService getInstance() {
        if (instance == null) {
            synchronized (CommentService.class) {
                if (instance == null) {
                    instance = new CommentService();
                }
            }
        }
        return instance;
    }

    public Comment addComment(int answerId, String content, int author) {
        if (!AnswerService.getInstance().answerExists(answerId)) {
            throw new IllegalArgumentException("Answer does not exist");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
        if (!UserService.getInstance().userExists(author)) {
            throw new IllegalArgumentException("Author does not exist");
        }

        int commentId = commentIdCounter.incrementAndGet();
        Comment comment = new Comment(commentId, content, author, AnswerService.getInstance().getAnswer(answerId).getQuestionId(), answerId, new java.util.Date());
        commentsMap.put(commentId, comment);

        AnswerService.getInstance().addCommentToAnswer(answerId, comment);
        return comment;
    }
}
