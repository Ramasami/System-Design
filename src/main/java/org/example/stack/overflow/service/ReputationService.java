package org.example.stack.overflow.service;

import lombok.NonNull;
import org.example.stack.overflow.model.Answer;
import org.example.stack.overflow.model.Question;
import org.example.stack.overflow.model.User;
import org.example.stack.overflow.model.Vote;

import java.util.logging.Logger;

public class ReputationService {
    private static final Logger logger = Logger.getLogger(ReputationService.class.getName());
    private static volatile ReputationService instance;

    private final UserService userService;
    private final QuestionService questionService;
    private final AnswerService answerService;

    private ReputationService() {
        this.userService = UserService.getInstance();
        this.questionService = QuestionService.getInstance();
        this.answerService = AnswerService.getInstance();
    }

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

    public void recordVoteForUser(int userId, @NonNull Vote vote) {
        User user = userService.getUser(userId);
        user.getReputation().addVote(vote);
        logger.info(() -> String.format("Recorded %s vote for user %d",
                vote.getVoteType(), userId));
    }

    public void recordAcceptedAnswerForUser(int answerId) {
        Answer answer = answerService.getAnswer(answerId);
        User user = userService.getUser(answer.getAuthor());
        user.getReputation().addAcceptedAnswer(answer);
        logger.info(() -> String.format("Recorded accepted answer %d for user %d",
                answerId, answer.getAuthor()));
    }

    public void recordQuestionsAsked(int userId, int questionId) {
        User user = userService.getUser(userId);
        Question question = questionService.getQuestion(questionId);
        user.getReputation().addQuestionAsked(question);
        logger.info(() -> String.format("Recorded question %d asked by user %d",
                questionId, userId));
    }

    public void recordAnswersGiven(int userId, int answerId) {
        User user = userService.getUser(userId);
        Answer answer = answerService.getAnswer(answerId);
        user.getReputation().addQuestionAnswered(answer);
        logger.info(() -> String.format("Recorded answer %d given by user %d",
                answerId, userId));
    }
}