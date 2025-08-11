package org.example.split.wise;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.example.split.wise.manager.SplitWiseManager;
import org.example.split.wise.model.*;
import org.example.stack.overflow.StackOverFlowApplication;

import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class SplitWiseApplication {

    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger logger = Logger.getLogger(StackOverFlowApplication.class.getName());

    @SneakyThrows
    public static void main(String[] args) {
        User johnDoe = SplitWiseManager.getInstance().createUser("john_doe", "john_doe@email.com");
        logger.info("Created user: " + mapper.writeValueAsString(johnDoe));

        User janeDoe = SplitWiseManager.getInstance().createUser("jane_doe", "jane_doe@email.com");
        logger.info("Created user: " + mapper.writeValueAsString(janeDoe));

        User aliceSmith = SplitWiseManager.getInstance().createUser("alice_smith", "alice_smith@email.com");
        logger.info("Created user: " + mapper.writeValueAsString(aliceSmith));

        Transaction transaction1 = SplitWiseManager.getInstance().createTransaction(PaymentType.EQUAL, johnDoe.getId(), "transaction1", 100, Map.of(johnDoe.getId(), 1.0, janeDoe.getId(), 1.0, aliceSmith.getId(), 1.0), null);
        logger.info("Created transaction 1: " + mapper.writeValueAsString(transaction1));

        janeDoe = SplitWiseManager.getInstance().getUser(janeDoe.getId());
        logger.info("User Jane Doe: " + mapper.writeValueAsString(janeDoe));

        Group group1 = SplitWiseManager.getInstance().createGroup("group1", "description1");
        logger.info("Created group: " + mapper.writeValueAsString(group1));

        SplitWiseManager.getInstance().addUserToGroup(group1.getId(), johnDoe.getId());
        SplitWiseManager.getInstance().addUserToGroup(group1.getId(), janeDoe.getId());
        SplitWiseManager.getInstance().addUserToGroup(group1.getId(), aliceSmith.getId());

        group1 = SplitWiseManager.getInstance().getGroup(group1.getId());
        logger.info("Group 1 Details: " + mapper.writeValueAsString(group1));

        Transaction transaction2 = SplitWiseManager.getInstance().createTransaction(PaymentType.PERCENTAGE, aliceSmith.getId(), "transaction1", 100, Map.of(johnDoe.getId(), 33.0, janeDoe.getId(), 33.0, aliceSmith.getId(), 34.0), group1.getId());
        logger.info("Created transaction 2: " + mapper.writeValueAsString(transaction2));

        group1 = SplitWiseManager.getInstance().getGroup(group1.getId());
        logger.info("Group 1 Details: " + mapper.writeValueAsString(group1));

        janeDoe = SplitWiseManager.getInstance().getUser(janeDoe.getId());
        logger.info("User Jane Doe: " + mapper.writeValueAsString(janeDoe));

        List<Ledger> ledgers1 = SplitWiseManager.getInstance().getTransactions(janeDoe.getId());
        logger.info("User Jane Doe Ledgers: " + mapper.writeValueAsString(ledgers1));

        Ledger ledgers2 = SplitWiseManager.getInstance().getTransactions(johnDoe.getId(), janeDoe.getId());
        logger.info("User John Dow to Jane Doe Ledgers: " + mapper.writeValueAsString(ledgers2));

        List<Ledger> ledgers3 = SplitWiseManager.getInstance().getGroupTransactions(group1.getId());
        logger.info("Group 1 Ledgers: " + mapper.writeValueAsString(ledgers3));

        List<Ledger> ledgers4 = SplitWiseManager.getInstance().getGroupTransactions(group1.getId(), aliceSmith.getId());
        logger.info("Group 1 Ledgers 4: " + mapper.writeValueAsString(ledgers4));

        List<Ledger> ledgers5 = SplitWiseManager.getInstance().getGroupTransactions(group1.getId(), aliceSmith.getId(), janeDoe.getId());
        logger.info("Group 1 Ledgers 5: " + mapper.writeValueAsString(ledgers5));

        SplitWiseManager.getInstance().settleExpenses(aliceSmith.getId(), janeDoe.getId());

        List<Ledger> ledgers6 = SplitWiseManager.getInstance().getGroupTransactions(group1.getId(), aliceSmith.getId(), janeDoe.getId());
        logger.info("Group 1 Ledgers 6: " + mapper.writeValueAsString(ledgers6));
    }
}
