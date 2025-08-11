package org.example.stack.overflow.service;

import lombok.NonNull;
import org.example.stack.overflow.model.Answer;
import org.example.stack.overflow.model.Comment;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class AnswerService {
    private static final Logger logger = Logger.getLogger(AnswerService.class.getName());
    private static volatile AnswerService instance;

    private final AtomicInteger answerIdCounter;
    private final Map<Integer, Answer> answers;
    private final QuestionService questionService;
    private final UserService userService;

    private AnswerService() {
        this.answerIdCounter = new AtomicInteger(0);
        this.answers = new ConcurrentHashMap<>();
        this.questionService = QuestionService.getInstance();
        this.userService = UserService.getInstance();
    }

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

    public Answer addAnswer(int questionId, @NonNull String content, int author) {
        validateContent(content);
        questionService.getQuestion(questionId); // Validates question exists
        userService.getUser(author); // Validates user exists

        int answerId = answerIdCounter.incrementAndGet();
        Answer answer = new Answer(answerId, content, author, questionId, new Date());
        answers.put(answerId, answer);

        questionService.addAnswerToQuestion(questionId, answer);
        ReputationService.getInstance().recordAnswersGiven(author, answerId);
        logger.info(() -> String.format("Added answer %d to question %d by user %d",
                answerId, questionId, author));

        return answer;
    }

    public Answer getAnswer(int answerId) {
        validateAnswerId(answerId);
        Answer answer = answers.get(answerId);
        if (answer == null) {
            throw new IllegalArgumentException("Answer not found with ID: " + answerId);
        }
        return answer;
    }

    public boolean isAnswerToQuestion(int questionId, int answerId) {
        Answer answer = getAnswer(answerId);
        questionService.getQuestion(questionId); // Validates question exists
        return answer.getQuestionId() == questionId;
    }

    public void addCommentToAnswer(int answerId, @NonNull Comment comment) {
        Answer answer = getAnswer(answerId);
        if (answer.getComments().add(comment)) {
            logger.info(() -> String.format("Added comment %d to answer %d",
                    comment.getCommentId(), answerId));
        }
    }

    public boolean acceptAnswer(int questionId, int answerId, int authorId) {
        Answer answer = getAnswer(answerId);
        userService.getUser(authorId); // Validates user exists

        if (!isAnswerToQuestion(questionId, answerId)) {
            throw new IllegalArgumentException("Answer does not belong to the question");
        }
        if (!questionService.isQuestionAuthor(questionId, authorId)) {
            throw new IllegalArgumentException("User is not the question author");
        }
        if (answer.isAccepted()) {
            throw new IllegalArgumentException("Answer is already accepted");
        }

        answer.setAccepted(true);
        ReputationService.getInstance().recordAcceptedAnswerForUser(answerId);
        logger.info(() -> String.format("Answer %d accepted by user %d",
                answerId, authorId));

        return true;
    }

    private void validateAnswerId(int answerId) {
        if (answerId <= 0) {
            throw new IllegalArgumentException("Answer ID must be positive");
        }
    }

    private void validateContent(String content) {
        if (content.trim().isEmpty()) {
            throw new IllegalArgumentException("Content cannot be empty");
        }
    }
}