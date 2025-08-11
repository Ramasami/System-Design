package org.example.stack.overflow.service;

import lombok.NonNull;
import org.example.stack.overflow.model.*;

import java.util.Set;
import java.util.logging.Logger;

public class VoteService {
    private static final Logger logger = Logger.getLogger(VoteService.class.getName());
    private static volatile VoteService instance;

    private final QuestionService questionService;
    private final AnswerService answerService;
    private final UserService userService;
    private final ReputationService reputationService;

    private VoteService() {
        this.questionService = QuestionService.getInstance();
        this.answerService = AnswerService.getInstance();
        this.userService = UserService.getInstance();
        this.reputationService = ReputationService.getInstance();
    }

    public static VoteService getInstance() {
        if (instance == null) {
            synchronized (VoteService.class) {
                if (instance == null) {
                    instance = new VoteService();
                }
            }
        }
        return instance;
    }

    public void addVoteToQuestion(int questionId, int userId, @NonNull VoteType voteType) {
        validateIds(questionId, userId);
        Question question = questionService.getQuestion(questionId);
        Vote vote = new Vote(questionId, userId, voteType, VoteFor.QUESTION);

        if (question.getVotes().add(vote)) {
            reputationService.recordVoteForUser(question.getAuthor(), vote);
            logger.info(() -> String.format("Added %s vote to question %d by user %d",
                    voteType, questionId, userId));
        }
    }

    public void addVoteToAnswer(int answerId, int userId, @NonNull VoteType voteType) {
        validateIds(answerId, userId);
        Answer answer = answerService.getAnswer(answerId);
        Vote vote = new Vote(answerId, userId, voteType, VoteFor.ANSWER);

        if (answer.getVotes().add(vote)) {
            reputationService.recordVoteForUser(answer.getAuthor(), vote);
            logger.info(() -> String.format("Added %s vote to answer %d by user %d",
                    voteType, answerId, userId));
        }
    }

    public int getVoteCountForQuestion(int questionId) {
        Question question = questionService.getQuestion(questionId);
        return calculateVoteCount(question.getVotes());
    }

    public int getVoteCountForAnswer(int answerId) {
        Answer answer = answerService.getAnswer(answerId);
        return calculateVoteCount(answer.getVotes());
    }

    public boolean removeVoteFromQuestion(int questionId, int userId) {
        validateIds(questionId, userId);
        Question question = questionService.getQuestion(questionId);
        Vote vote = new Vote(questionId, userId, VoteType.REVOKE, VoteFor.QUESTION);

        if (question.getVotes().remove(vote)) {
            reputationService.recordVoteForUser(question.getAuthor(), vote);
            logger.info(() -> String.format("Removed vote from question %d by user %d",
                    questionId, userId));
            return true;
        }
        return false;
    }

    public boolean removeVoteFromAnswer(int answerId, int userId) {
        validateIds(answerId, userId);
        Answer answer = answerService.getAnswer(answerId);
        Vote vote = new Vote(answerId, userId, VoteType.REVOKE, VoteFor.ANSWER);

        if (answer.getVotes().remove(vote)) {
            reputationService.recordVoteForUser(answer.getAuthor(), vote);
            logger.info(() -> String.format("Removed vote from answer %d by user %d",
                    answerId, userId));
            return true;
        }
        return false;
    }

    private int calculateVoteCount(Set<Vote> votes) {
        return votes.stream()
                .mapToInt(vote -> vote.getVoteType().getValue())
                .sum();
    }

    private void validateIds(int contentId, int userId) {
        if (contentId <= 0) {
            throw new IllegalArgumentException("Content ID must be positive");
        }
        userService.getUser(userId); // This will throw if user doesn't exist
    }
}