package org.example.stack.overflow.manager;

import lombok.NonNull;
import org.example.stack.overflow.model.*;
import org.example.stack.overflow.service.*;

public class StackOverFlow {
    private static volatile StackOverFlow instance;

    private final UserService userService;
    private final QuestionService questionService;
    private final AnswerService answerService;
    private final TagService tagService;
    private final CommentService commentService;
    private final VoteService voteService;

    private StackOverFlow() {
        this.userService = UserService.getInstance();
        this.questionService = QuestionService.getInstance();
        this.answerService = AnswerService.getInstance();
        this.tagService = TagService.getInstance();
        this.commentService = CommentService.getInstance();
        this.voteService = VoteService.getInstance();
    }

    public static StackOverFlow getInstance() {
        if (instance == null) {
            synchronized (StackOverFlow.class) {
                if (instance == null) {
                    instance = new StackOverFlow();
                }
            }
        }
        return instance;
    }

    public User createUser(@NonNull String username) {
        validateUsername(username);
        return userService.createUser(username);
    }

    public Question createNewQuestion(int author, @NonNull String title, @NonNull String content) {
        validateUserId(author);
        validateContent(title, "Title");
        validateContent(content, "Content");
        return questionService.createNewQuestion(author, title, content);
    }

    public Answer addAnswerToQuestion(int questionId, @NonNull String content, int author) {
        validateIds(questionId, "Question");
        validateUserId(author);
        validateContent(content, "Content");
        return answerService.addAnswer(questionId, content, author);
    }

    public Tag addTag(int questionId, @NonNull String tagName) {
        validateIds(questionId, "Question");
        validateContent(tagName, "Tag name");
        return tagService.addTag(questionId, tagName);
    }

    public Comment addCommentToAnswer(int answerId, @NonNull String content, int authorId) {
        validateIds(answerId, "Answer");
        validateUserId(authorId);
        validateContent(content, "Content");
        return commentService.addComment(answerId, content, authorId);
    }

    public void addVoteToQuestion(int questionId, int userId, @NonNull VoteType voteType) {
        validateIds(questionId, "Question");
        validateUserId(userId);
        voteService.addVoteToQuestion(questionId, userId, voteType);
    }

    public void addVoteToAnswer(int answerId, int userId, @NonNull VoteType voteType) {
        validateIds(answerId, "Answer");
        validateUserId(userId);
        voteService.addVoteToAnswer(answerId, userId, voteType);
    }

    public int getVoteCountForAnswer(int answerId) {
        validateIds(answerId, "Answer");
        return voteService.getVoteCountForAnswer(answerId);
    }

    public int getVoteCountForQuestion(int questionId) {
        validateIds(questionId, "Question");
        return voteService.getVoteCountForQuestion(questionId);
    }

    public boolean removeVoteFromQuestion(int questionId, int userId) {
        validateIds(questionId, "Question");
        validateUserId(userId);
        return voteService.removeVoteFromQuestion(questionId, userId);
    }

    public boolean removeVoteFromAnswer(int answerId, int userId) {
        validateIds(answerId, "Answer");
        validateUserId(userId);
        return voteService.removeVoteFromAnswer(answerId, userId);
    }

    public int getUserReputation(int userId) {
        validateUserId(userId);
        return userService.getReputation(userId);
    }

    public boolean acceptAnswer(int questionId, int answerId, int authorId) {
        validateIds(questionId, "Question");
        validateIds(answerId, "Answer");
        validateUserId(authorId);
        return answerService.acceptAnswer(questionId, answerId, authorId);
    }

    private void validateUserId(int userId) {
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
    }

    private void validateIds(int id, String type) {
        if (id <= 0) {
            throw new IllegalArgumentException(type + " ID must be positive");
        }
    }

    private void validateContent(String content, String field) {
        if (content.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty");
        }
    }

    private void validateUsername(String username) {
        if (username.trim().isEmpty()) {
            throw new IllegalArgumentException("Username cannot be empty");
        }
    }
}