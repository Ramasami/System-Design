package org.example.stack.overflow.service;

import lombok.AllArgsConstructor;
import org.example.stack.overflow.model.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class AnswerService {
    private static AnswerService instance;
    private final AtomicInteger answerIdCounter = new AtomicInteger(0);
    private final Map<Integer, Answer> answersMap = new ConcurrentHashMap<>();

    public static AnswerService getInstance() {
        if (instance == null) {
            synchronized (AnswerService.class) {
                if (instance == null) {
                    instance = new AnswerService();
                }
            }
        }
        return instance;
    }

    public Answer addAnswer(int questionId, String content, int author) {
        if (!QuestionService.getInstance().questionExists(questionId)) {
            throw new IllegalArgumentException("Question does not exist");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
        if (!UserService.getInstance().userExists(author)) {
            throw new IllegalArgumentException("Author does not exist");
        }

        int answerId = answerIdCounter.incrementAndGet();
        Answer answer = new Answer(answerId, content, author, questionId, new java.util.Date());
        answersMap.put(answerId, answer);
        QuestionService.getInstance().addAnswerToQuestion(questionId, answer);
        ReputationService.getInstance().recordAnswersGiven(author, answerId);
        return answer;
    }

    public Answer getAnswer(int answerId) {
        if (answerId <= 0) {
            throw new IllegalArgumentException("Answer ID must be positive");
        }
        return answersMap.get(answerId);
    }

    public boolean answerExists(int answerId) {
        return getAnswer(answerId) != null;
    }

    public boolean isAnswerToQuestion(int questionId, int answerId) {
        if (!QuestionService.getInstance().questionExists(questionId)) {
            throw new IllegalArgumentException("Question does not exist");
        }
        if (!answerExists(answerId)) {
            throw new IllegalArgumentException("Answer does not exist");
        }
        return getAnswer(answerId).getQuestionId() == questionId;
    }

    public void addCommentToAnswer(int answerId, Comment comment) {
        if (!answerExists(answerId)) {
            throw new IllegalArgumentException("Answer does not exist");
        }
       if (comment == null) {
            throw new IllegalArgumentException("Comment cannot be null");
        }

        Answer answer = getAnswer(answerId);
        answer.getComments().add(comment);
    }

    public boolean acceptAnswer(int questionId, int answerId, int authorId) {
        if (!QuestionService.getInstance().questionExists(questionId)) {
            throw new IllegalArgumentException("Question does not exist");
        }
        if (!answerExists(answerId)) {
            throw new IllegalArgumentException("Answer does not exist");
        }
        if (!isAnswerToQuestion(questionId, answerId)) {
            throw new IllegalArgumentException("Answer does not belong to the question");
        }
        if (!UserService.getInstance().userExists(authorId)) {
            throw new IllegalArgumentException("Author does not exist");
        }
        if (!QuestionService.getInstance().isQuestionToAuthor(questionId, authorId)) {
            throw new IllegalArgumentException("Author is not the question author");
        }
        Answer answer = getAnswer(answerId);
        if (answer.isAccepted()) {
            throw new IllegalArgumentException("Answer is already accepted");
        } else {
            answer.setAccepted(true);
            ReputationService.getInstance().recordAcceptedAnswerForUser(answerId);
        }
        return true;
    }
}
