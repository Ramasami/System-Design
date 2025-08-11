package org.example.split.wise.factory;

import lombok.extern.slf4j.Slf4j;
import org.example.split.wise.model.Split;
import org.example.split.wise.model.Transaction;

import java.util.Map;

import static org.example.split.wise.model.PaymentType.EQUAL;


@Slf4j
public class EqualTransaction extends Transaction {

    public EqualTransaction(int id, int lenderId, String description, double amount, Map<Integer, Double> lendeeShares, Integer group) {
        super(id, lenderId, description, amount, EQUAL, lendeeShares, group);
    }

    @Override
    public void validateAdditionalLenderInformation(int lenderId, Map<Integer, Double> lendeeShares, double amount) {
        // Default implementation does not require additional validation for EqualTransaction
    }

    @Override
    public void calculateCharges() {
        double individualShare = amount / lendeeShares.size();
        log.info("Calculating charges for EqualTransaction with ID: {}", id);
        for (Map.Entry<Integer, Double> entry : lendeeShares.entrySet()) {
            Integer userId = entry.getKey();
            splits.add(new Split(id, groupId, lenderId, userId, individualShare));
        }
    }
}

