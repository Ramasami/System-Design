package org.example.stack.overflow.service;

import lombok.AllArgsConstructor;
import org.example.stack.overflow.model.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class QuestionService {
    private static QuestionService instance;
    private final AtomicInteger questionCounter = new AtomicInteger(0);
    private final Map<Integer, Question> questions = new ConcurrentHashMap<>();

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

    public Question createNewQuestion(int author, String title, String content) {
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or empty");
        }
        if (content == null || content.isBlank()) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
        if (!UserService.getInstance().userExists(author)) {
            throw new IllegalArgumentException("Author does not exist");
        }

        int questionId = questionCounter.incrementAndGet();
        Question question = new Question(questionId, title, content, author, new java.util.Date());
        questions.put(questionId, question);
        UserService.getInstance().addQuestionToUser(author, question);
        ReputationService.getInstance().recordQuestionsAsked(author, questionId);
        return question;
    }

    public Question getQuestion(int questionId) {
        if (questionId <= 0) {
            throw new IllegalArgumentException("Post ID must be positive");
        }
        return questions.get(questionId);
    }

    public boolean questionExists(int questionId) {
        return getQuestion(questionId) != null;
    }

    public boolean addAnswerToQuestion(int questionId, Answer answer) {
        if (!questionExists(questionId)) {
            throw new IllegalArgumentException("Question does not exist");
        }
        if (answer == null) {
            throw new IllegalArgumentException("Content cannot be null or empty");
        }
        getQuestion(questionId).getAnswers().add(answer);
        return true;
    }

    public void addTagToQuestion(int questionId, Tag tag) {
        if (!questionExists(questionId)) {
            throw new IllegalArgumentException("Question does not exist");
        }
        if (tag == null) {
            throw new IllegalArgumentException("Tag cannot be null");
        }
        getQuestion(questionId).getTags().add(tag);
    }

    public boolean isQuestionToAuthor(int questionId, int authorId) {
        if (!questionExists(questionId)) {
            throw new IllegalArgumentException("Question does not exist");
        }
        if (!UserService.getInstance().userExists(authorId)) {
            throw new IllegalArgumentException("Author does not exist");
        }
        return getQuestion(questionId).getAuthor() == authorId;
    }

    public boolean removeVoteFromQuestion(int questionId, int userId) {
        Vote vote = new Vote(questionId, userId, null, VotedOn.QUESTION);
        if (QuestionService.getInstance().getQuestion(questionId).getVotes().remove(vote)) {
            int authorId = QuestionService.getInstance().getQuestion(questionId).getAuthor();
            ReputationService.getInstance().recordVoteForUser(authorId, vote);
            return true;
        }
        return false;
    }
}
