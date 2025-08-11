package org.example.stack.overflow.model;

import lombok.Getter;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@Getter
public class Reputation {
    private final int userId;
    private final Set<Vote> upVotesForQuestion;
    private final Set<Vote> downVotesForQuestion;
    private final Set<Vote> upVotesForAnswer;
    private final Set<Vote> downVotesForAnswer;
    private final Set<Integer> acceptedAnswers;
    private final Set<Integer> questionsAnswered;
    private final Set<Integer> questionsAsked;
    private final AtomicInteger reputation;

    public Reputation(int userId) {
        this.userId = userId;
        upVotesForQuestion = new HashSet<>();
        downVotesForQuestion = new HashSet<>();
        upVotesForAnswer = new HashSet<>();
        downVotesForAnswer = new HashSet<>();
        acceptedAnswers = new HashSet<>();
        questionsAnswered = new HashSet<>();
        questionsAsked = new HashSet<>();
        reputation = new AtomicInteger(100); // Default reputation
    }

    public void increase(ReputationType reputationType) {
        reputation.addAndGet(reputationType.getValue());
    }

    public void decrease(ReputationType reputationType) {
        reputation.addAndGet(-reputationType.getValue());
    }

    public synchronized void removeVote(Vote vote) {
        if (vote != null) {
            if (upVotesForAnswer.remove(vote)) {
                decrease(ReputationType.ANSWER_UP_VOTE);
            }
            if (upVotesForQuestion.remove(vote)) {
                decrease(ReputationType.QUESTION_UP_VOTE);
            }
            if (downVotesForAnswer.remove(vote)) {
                decrease(ReputationType.ANSWER_DOWN_VOTE);
            }
            if (downVotesForQuestion.remove(vote)) {
                decrease(ReputationType.QUESTION_DOWN_VOTE);
            }
        }
    }

    public synchronized void addVote(Vote vote) {
        if (vote != null) {
            removeVote(vote);

            if (vote.getVoteType() == VoteType.UP && vote.getVotedOn() == VotedOn.ANSWER) {
                upVotesForAnswer.add(vote);
                increase(ReputationType.ANSWER_UP_VOTE);
            } else if (vote.getVoteType() == VoteType.DOWN && vote.getVotedOn() == VotedOn.ANSWER) {
                downVotesForAnswer.add(vote);
                increase(ReputationType.ANSWER_DOWN_VOTE);
            } else if (vote.getVoteType() == VoteType.UP && vote.getVotedOn() == VotedOn.QUESTION) {
                upVotesForQuestion.add(vote);
                increase(ReputationType.QUESTION_UP_VOTE);
            } else if (vote.getVoteType() == VoteType.DOWN && vote.getVotedOn() == VotedOn.QUESTION) {
                downVotesForQuestion.add(vote);
                increase(ReputationType.QUESTION_DOWN_VOTE);
            }
        }
    }

    public synchronized void addAcceptedAnswer(Answer answer) {
        if (answer != null && answer.getAuthor() == userId) {
            if (acceptedAnswers.add(answer.getAnswerId())) {
                increase(ReputationType.ACCEPTED_ANSWER);
            }
        } else {
            throw new IllegalArgumentException("Invalid answer or author does not match user ID");
        }
    }

    public synchronized void addQuestionAnswered(Answer answer) {
        if (answer != null && answer.getAuthor() == userId) {
            if (questionsAnswered.add(answer.getAnswerId())) {
                increase(ReputationType.QUESTION_ANSWERED);
            }
        } else {
            throw new IllegalArgumentException("Invalid answer or author does not match user ID");
        }
    }

    public synchronized void addQuestionAsked(Question question) {
        if (question != null && question.getAuthor() == userId) {
            if (questionsAsked.add(question.getQuestionId())) {
                increase(ReputationType.QUESTION_ASKED);
            }
        } else {
            throw new IllegalArgumentException("Invalid question or author does not match user ID");
        }
    }
}
