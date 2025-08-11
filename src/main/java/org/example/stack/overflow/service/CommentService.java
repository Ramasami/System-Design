package org.example.stack.overflow.service;

import lombok.NonNull;
import org.example.stack.overflow.model.Comment;

import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class CommentService {
    private static final Logger logger = Logger.getLogger(CommentService.class.getName());
    private static volatile CommentService instance;

    private final AtomicInteger commentIdCounter;
    private final Map<Integer, Comment> comments;
    private final UserService userService;
    private final AnswerService answerService;

    private CommentService() {
        this.commentIdCounter = new AtomicInteger(0);
        this.comments = new ConcurrentHashMap<>();
        this.userService = UserService.getInstance();
        this.answerService = AnswerService.getInstance();
    }

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

    public Comment addComment(int answerId, @NonNull String content, int author) {
        validateContent(content);
        userService.getUser(author); // Validates user exists

        int commentId = commentIdCounter.incrementAndGet();
        Comment comment = new Comment(commentId, content, author, answerService.getAnswer(answerId).getQuestionId(), answerId, new Date());

        comments.put(commentId, comment);
        answerService.addCommentToAnswer(answerId, comment);

        logger.info(() -> String.format("Added comment %d to answer %d by user %d", commentId, answerId, author));

        return comment;
    }

    public Comment getComment(int commentId) {
        validateCommentId(commentId);
        Comment comment = comments.get(commentId);
        if (comment == null) {
            throw new IllegalArgumentException("Comment not found with ID: " + commentId);
        }
        return comment;
    }

    private void validateCommentId(int commentId) {
        if (commentId <= 0) {
            throw new IllegalArgumentException("Comment ID must be positive");
        }
    }

    private void validateContent(String content) {
        if (content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }
    }
}