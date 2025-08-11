package org.example.stack.overflow.service;

import lombok.AllArgsConstructor;
import org.example.stack.overflow.model.Answer;
import org.example.stack.overflow.model.Reputation;
import org.example.stack.overflow.model.User;
import org.example.stack.overflow.model.Vote;

@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class ReputationService {
    private static ReputationService instance;

    public static ReputationService getInstance() {
        if (instance == null) {
            synchronized (ReputationService.class) {
                if (instance == null) {
                    instance = new ReputationService();
                }
            }
        }
        return instance;
    }

    public void recordVoteForUser(int userId, Vote vote) {
        if (!UserService.getInstance().userExists(userId)) {
            throw new IllegalArgumentException("User does not exist");
        }
        if (vote == null) {
            throw new IllegalArgumentException("Vote cannot be null");
        }
        User user = UserService.getInstance().getUser(userId);
        Reputation reputation = user.getReputation();
        reputation.addVote(vote);
    }

    public void recordAcceptedAnswerForUser(int answerId) {
        Answer answer = AnswerService.getInstance().getAnswer(answerId);
        if (answer == null || answer.getAuthor() <= 0) {
            throw new IllegalArgumentException("Invalid answer or author");
        }
        User user = UserService.getInstance().getUser(answer.getAuthor());
        if (user == null) {
            throw new IllegalArgumentException("User does not exist");
        }
        Reputation reputation = user.getReputation();
        reputation.addAcceptedAnswer(answer);
    }

    public void recordQuestionsAsked(int userId, int questionId) {
        if (!UserService.getInstance().userExists(userId)) {
            throw new IllegalArgumentException("User does not exist");
        }
        if (!QuestionService.getInstance().questionExists(questionId)) {
            throw new IllegalArgumentException("Question does not exist");
        }
        User user = UserService.getInstance().getUser(userId);
        Reputation reputation = user.getReputation();
        reputation.addQuestionAsked(QuestionService.getInstance().getQuestion(questionId));
    }

    public void recordAnswersGiven(int userId, int answerId) {
        if (!UserService.getInstance().userExists(userId)) {
            throw new IllegalArgumentException("User does not exist");
        }
        if (!AnswerService.getInstance().answerExists(answerId)) {
            throw new IllegalArgumentException("Answer does not exist");
        }
        User user = UserService.getInstance().getUser(userId);
        Reputation reputation = user.getReputation();
        reputation.addQuestionAnswered(AnswerService.getInstance().getAnswer(answerId));
    }
}
