package org.example.split.wise.model;

import lombok.Data;

@Data
public class Ledger {
    private final int fromUser;
    private final int toUser;
    private double cost;

    public Ledger addCost(double cost) {
        this.cost+=cost;
        return this;
    }

}
