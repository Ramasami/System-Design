package org.example.stack.overflow.model;

import lombok.Getter;
import lombok.NonNull;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

@Getter
public class Reputation {
    private static final Logger logger = Logger.getLogger(Reputation.class.getName());
    private static final int INITIAL_REPUTATION = 100;

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
        if (userId <= 0) {
            throw new IllegalArgumentException("User ID must be positive");
        }
        this.userId = userId;
        upVotesForQuestion = Collections.newSetFromMap(new ConcurrentHashMap<>());
        downVotesForQuestion = Collections.newSetFromMap(new ConcurrentHashMap<>());
        upVotesForAnswer = Collections.newSetFromMap(new ConcurrentHashMap<>());
        downVotesForAnswer = Collections.newSetFromMap(new ConcurrentHashMap<>());
        acceptedAnswers = Collections.newSetFromMap(new ConcurrentHashMap<>());
        questionsAnswered = Collections.newSetFromMap(new ConcurrentHashMap<>());
        questionsAsked = Collections.newSetFromMap(new ConcurrentHashMap<>());
        reputation = new AtomicInteger(INITIAL_REPUTATION);
    }

    public void updateReputation(@NonNull ReputationType type, boolean increase) {
        int delta = increase ? type.getValue() : -type.getValue();
        int newValue = reputation.addAndGet(delta);
        logger.info(() -> String.format("User %d reputation %s by %d to %d",
                userId, increase ? "increased" : "decreased", Math.abs(delta), newValue));
    }

    public void removeVote(@NonNull Vote vote) {
        if (upVotesForAnswer.remove(vote)) {
            updateReputation(ReputationType.ANSWER_UP_VOTE, false);
        } else if (upVotesForQuestion.remove(vote)) {
            updateReputation(ReputationType.QUESTION_UP_VOTE, false);
        } else if (downVotesForAnswer.remove(vote)) {
            updateReputation(ReputationType.ANSWER_DOWN_VOTE, false);
        } else if (downVotesForQuestion.remove(vote)) {
            updateReputation(ReputationType.QUESTION_DOWN_VOTE, false);
        }
    }

    public void addVote(@NonNull Vote vote) {
        removeVote(vote);
        switch (vote.getVoteType()) {
            case UP:
                handleUpVote(vote);
                break;
            case DOWN:
                handleDownVote(vote);
                break;
            case REVOKE:
                break;
            default:
                logger.warning(() -> "Unknown vote type: " + vote.getVoteType());
        }
    }

    private void handleUpVote(Vote vote) {
        if (vote.getVoteFor() == VoteFor.ANSWER) {
            upVotesForAnswer.add(vote);
            updateReputation(ReputationType.ANSWER_UP_VOTE, true);
        } else {
            upVotesForQuestion.add(vote);
            updateReputation(ReputationType.QUESTION_UP_VOTE, true);
        }
    }

    private void handleDownVote(Vote vote) {
        if (vote.getVoteFor() == VoteFor.ANSWER) {
            downVotesForAnswer.add(vote);
            updateReputation(ReputationType.ANSWER_DOWN_VOTE, true);
        } else {
            downVotesForQuestion.add(vote);
            updateReputation(ReputationType.QUESTION_DOWN_VOTE, true);
        }
    }

    public void addAcceptedAnswer(@NonNull Answer answer) {
        validateUserAction(answer.getAuthor());
        if (acceptedAnswers.add(answer.getAnswerId())) {
            updateReputation(ReputationType.ACCEPTED_ANSWER, true);
        }
    }


    public void addQuestionAnswered(@NonNull Answer answer) {
        validateUserAction(answer.getAuthor());
        if (questionsAnswered.add(answer.getAnswerId())) {
            updateReputation(ReputationType.QUESTION_ANSWERED, true);
        }
    }

    public void addQuestionAsked(@NonNull Question question) {
        validateUserAction(question.getAuthor());
        if (questionsAsked.add(question.getQuestionId())) {
            updateReputation(ReputationType.QUESTION_ASKED, true);
        }
    }

    private void validateUserAction(int authorId) {
        if (authorId != userId) {
            throw new IllegalArgumentException("Author ID does not match user ID");
        }
    }
}
