package org.example.stack.overflow.model;

public enum ReputationType {
    QUESTION_UP_VOTE(10),
    QUESTION_DOWN_VOTE(-2),
    ANSWER_UP_VOTE(10),
    ANSWER_DOWN_VOTE(-2),
    ACCEPTED_ANSWER(15),
    QUESTION_ANSWERED(5),
    QUESTION_ASKED(5);

    private final int value;

    ReputationType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
