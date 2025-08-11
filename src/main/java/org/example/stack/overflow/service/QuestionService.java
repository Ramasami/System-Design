package org.example.stack.overflow.service;

import lombok.NonNull;
import org.example.stack.overflow.model.*;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

public class QuestionService {
    private static final Logger logger = Logger.getLogger(QuestionService.class.getName());
    private static volatile QuestionService instance;

    private final AtomicInteger questionCounter;
    private final Map<Integer, Question> questions;
    private final UserService userService;

    private QuestionService() {
        this.questionCounter = new AtomicInteger(0);
        this.questions = new ConcurrentHashMap<>();
        this.userService = UserService.getInstance();
    }

    public static QuestionService getInstance() {
        if (instance == null) {
            synchronized (QuestionService.class) {
                if (instance == null) {
                    instance = new QuestionService();
                }
            }
        }
        return instance;
    }

    public Question createNewQuestion(int author, @NonNull String title, @NonNull String content) {
        validateContent(title, "Title");
        validateContent(content, "Content");
        userService.getUser(author); // Validates user exists

        int questionId = questionCounter.incrementAndGet();
        Question question = new Question(questionId, title, content, author, new java.util.Date());
        questions.put(questionId, question);

        userService.addQuestionToUser(author, question);
        ReputationService.getInstance().recordQuestionsAsked(author, questionId);
        logger.info(() -> String.format("Created question %d by user %d: %s",
                questionId, author, title));

        return question;
    }

    public Question getQuestion(int questionId) {
        validateQuestionId(questionId);
        Question question = questions.get(questionId);
        if (question == null) {
            throw new IllegalArgumentException("Question not found with ID: " + questionId);
        }
        return question;
    }

    public void addAnswerToQuestion(int questionId, @NonNull Answer answer) {
        Question question = getQuestion(questionId);
        if (question.getAnswers().add(answer)) {
            logger.info(() -> String.format("Added answer %d to question %d",
                    answer.getAnswerId(), questionId));
        }
    }

    public void addTagToQuestion(int questionId, @NonNull Tag tag) {
        Question question = getQuestion(questionId);
        if (question.getTags().add(tag)) {
            logger.info(() -> String.format("Added tag '%s' to question %d",
                    tag.getName(), questionId));
        }
    }

    public boolean isQuestionAuthor(int questionId, int authorId) {
        Question question = getQuestion(questionId);
        userService.getUser(authorId); // Validates user exists
        return question.getAuthor() == authorId;
    }

    public boolean removeVoteFromQuestion(int questionId, int userId) {
        Question question = getQuestion(questionId);
        Vote vote = new Vote(questionId, userId, VoteType.REVOKE, VoteFor.QUESTION);

        if (question.getVotes().remove(vote)) {
            ReputationService.getInstance().recordVoteForUser(question.getAuthor(), vote);
            logger.info(() -> String.format("Removed vote from question %d by user %d",
                    questionId, userId));
            return true;
        }
        return false;
    }

    private void validateQuestionId(int questionId) {
        if (questionId <= 0) {
            throw new IllegalArgumentException("Question ID must be positive");
        }
    }

    private void validateContent(String content, String field) {
        if (content.trim().isEmpty()) {
            throw new IllegalArgumentException(field + " cannot be empty");
        }
    }
}