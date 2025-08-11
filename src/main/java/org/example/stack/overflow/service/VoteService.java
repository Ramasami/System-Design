package org.example.stack.overflow.service;

import lombok.AllArgsConstructor;
import org.example.stack.overflow.model.Answer;
import org.example.stack.overflow.model.Vote;
import org.example.stack.overflow.model.VoteType;
import org.example.stack.overflow.model.VotedOn;

@AllArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class VoteService {

    private static VoteService instance;

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

    public void addVoteToQuestion(int questionId, int userId, VoteType voteType) {
        if (!QuestionService.getInstance().questionExists(questionId)) {
            throw new IllegalArgumentException("Question does not exist");
        }
        if (!UserService.getInstance().userExists(userId)) {
            throw new IllegalArgumentException("User does not exist");
        }
        if (voteType == null) {
            throw new IllegalArgumentException("Vote type cannot be null");
        }

        Vote vote = new Vote(questionId, userId, voteType, VotedOn.QUESTION);
        if (QuestionService.getInstance().getQuestion(questionId).getVotes().add(vote)) {
            int authorId = QuestionService.getInstance().getQuestion(questionId).getAuthor();
            ReputationService.getInstance().recordVoteForUser(authorId, vote);
        }
    }


    public int getVoteCountForQuestion(int questionId) {
        if (!QuestionService.getInstance().questionExists(questionId)) {
            throw new IllegalArgumentException("Question does not exist");
        }
        return QuestionService.getInstance().getQuestion(questionId).getVotes().stream()
                .mapToInt(vote -> vote.getVoteType().getValue())
                .sum();
    }

    public boolean removeVoteFromQuestion(int questionId, int userId) {
        Vote vote = new Vote(questionId, userId, null, VotedOn.QUESTION);
        boolean removed = QuestionService.getInstance().getQuestion(questionId).getVotes().remove(vote);
        int authorId = QuestionService.getInstance().getQuestion(questionId).getAuthor();
        ReputationService.getInstance().recordVoteForUser(authorId, vote);
        return removed;
    }

    public void addVoteToAnswer(int answerId, int userId, VoteType voteType) {
        if (!AnswerService.getInstance().answerExists(answerId)) {
            throw new IllegalArgumentException("Answer does not exist");
        }
        if (!UserService.getInstance().userExists(userId)) {
            throw new IllegalArgumentException("User does not exist");
        }

        Answer answer = AnswerService.getInstance().getAnswer(answerId);
        answer.getVotes().add(new Vote(answerId, userId, voteType, VotedOn.ANSWER));
        ReputationService.getInstance().recordVoteForUser(answer.getAuthor(), new Vote(answerId, userId, voteType, VotedOn.ANSWER));
    }

    public int getVoteCountForAnswer(int answerId) {
        if (!AnswerService.getInstance().answerExists(answerId)) {
            throw new IllegalArgumentException("Answer does not exist");
        }
        Answer answer = AnswerService.getInstance().getAnswer(answerId);
        return answer.getVotes().stream()
                .mapToInt(vote -> vote.getVoteType().getValue())
                .sum();
    }

    public boolean removeVoteFromAnswer(int answerId, int userId) {
        Vote vote = new Vote(answerId, userId, null, VotedOn.ANSWER);
        boolean removed = AnswerService.getInstance().getAnswer(answerId).getVotes().remove(vote);
        int authorId = QuestionService.getInstance().getQuestion(answerId).getAuthor();
        ReputationService.getInstance().recordVoteForUser(authorId, vote);
        return removed;
    }

}
