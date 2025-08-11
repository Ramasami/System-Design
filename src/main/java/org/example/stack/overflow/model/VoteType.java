package org.example.stack.overflow.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum VoteType {
    UP(1), DOWN(-1);
    private final int value;
}