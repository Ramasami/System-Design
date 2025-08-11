package org.example.split.wise.factory;

import lombok.extern.slf4j.Slf4j;
import org.example.split.wise.model.Split;
import org.example.split.wise.model.Transaction;

import java.util.Map;

import static org.example.split.wise.model.PaymentType.PERCENTAGE;


@Slf4j
public class PercentageTransaction extends Transaction {

    public PercentageTransaction(int id, int lender, String description, double amount, Map<Integer, Double> lendeeShares, Integer group) {
        super(id, lender, description, amount, PERCENTAGE, lendeeShares, group);
    }

    @Override
    public void validateAdditionalLenderInformation(int lender, Map<Integer, Double> lendeeShares, double amount) {
        lendeeShares.values().stream().filter(share -> share < 0).findAny().ifPresent(share -> {
            throw new IllegalArgumentException("Percentage shares cannot be negative");
        });
        double totalShare = lendeeShares.values().stream().reduce(0.0, Double::sum);
        if (totalShare != 100.0) {
            throw new IllegalArgumentException("Total percentage shares must equal 100%");
        }
    }

    @Override
    public void calculateCharges() {
        for (Map.Entry<Integer, Double> entry : lendeeShares.entrySet()) {
            Integer userId = entry.getKey();
            double sharePercentage = entry.getValue();
            double shareAmount = (sharePercentage / 100) * amount;
            splits.add(new Split(id, groupId, lenderId, userId, shareAmount));
        }
    }
}

