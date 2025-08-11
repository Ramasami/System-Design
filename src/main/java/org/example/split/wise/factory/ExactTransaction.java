package org.example.split.wise.factory;

import lombok.extern.slf4j.Slf4j;
import org.example.split.wise.model.Split;
import org.example.split.wise.model.Transaction;

import java.util.Map;

import static org.example.split.wise.model.PaymentType.EXACT;

@Slf4j
public class ExactTransaction extends Transaction {

    public ExactTransaction(int id, int lender, String description, double amount, Map<Integer, Double> lendeeShares, Integer group) {
        super(id, lender, description, amount, EXACT, lendeeShares, group);
    }

    @Override
    public void validateAdditionalLenderInformation(int lender, Map<Integer, Double> lendeeShares, double amount) {
        Double lentAmount = lendeeShares.values().stream().reduce(0.0, Double::sum);
        if (lentAmount != amount) {
            throw new IllegalArgumentException("Total lent amount does not match the transaction amount");
        }
    }

    @Override
    public void calculateCharges() {
        log.info("Calculating charges for ExactTransaction with ID: {}", id);
        for (Map.Entry<Integer, Double> entry : lendeeShares.entrySet()) {
            Integer userId = entry.getKey();
            splits.add(new Split(id, groupId, lenderId, userId, entry.getValue()));
        }
    }
}
