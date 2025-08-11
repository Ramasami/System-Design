package org.example.split.wise.model;

import lombok.Data;
import lombok.Setter;

@Data
public class Split {
    private final int transactionId;
    private final Integer groupId;
    private final int lenderId;
    private final int borrowerId;
    private final double amount;
    private boolean enabled;

    public Split(int transactionId, Integer groupId, int lenderId, int borrowerId, double amount) {
        this.transactionId = transactionId;
        this.groupId = groupId;
        this.lenderId = lenderId;
        this.borrowerId = borrowerId;
        this.amount = amount;
        this.enabled = lenderId != borrowerId;
    }
}
