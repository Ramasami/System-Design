# SplitWise

This document describes the technical design, architecture, and usage patterns for the SplitWise package. It focuses on domain modeling, service orchestration, data flow, validation, algorithms, concurrency, and extensibility.

## Overview

SplitWise models expense sharing between users with support for:
- Multiple split strategies: EQUAL, EXACT, PERCENTAGE
- Optional grouping of transactions under a Group
- Per-user and per-pair ledgers (net amounts owed/receivable)
- Settlement (disabling a set of splits between two users)
- Thread-safe, in-memory storage and a facade for orchestration

Technology notes:
- Java 17
- Lombok for boilerplate reduction
- In-memory, thread-safe collections as the data store

## Core Domain

- User
    - id, name, email, splits (all splits involving the user)
    - Equality based on id
- Group
    - id, name, description, createdAt, members, transactions
- Transaction (abstract)
    - id, lenderId, description, amount, paymentType, lendeeShares, createdAt, groupId, splits
    - Template method flow: validate -> calculateCharges -> produce Splits
- Split
    - transactionId, groupId, lenderId, borrowerId, amount, enabled flag
    - Enabled denotes active debt; settlement toggles to disabled
- Ledger
    - fromUser, toUser, cost (net amount from perspective of fromUser)
- PaymentType
    - EQUAL, PERCENTAGE, EXACT

Important invariants:
- For EXACT, sum(lendeeShares) must equal the transaction amount.
- For PERCENTAGE, sum(lendeeShares) must equal 100.0.
- For EQUAL, each participant gets amount / participantCount.
- lenderId may be part of lendeeShares; if lender == borrower, the generated split is disabled (no-op).
- Group (if provided) must exist, and all referenced users must exist.

## Architecture

- Facade
    - SplitWiseManager: entry point that orchestrates use cases, delegates to services, and simplifies client usage.
- Services
    - UserService: user lifecycle and validation, attaches splits to users.
    - GroupService: group lifecycle, membership, and group-transaction association.
    - TransactionService: creates transactions, validates inputs, generates splits, provides user and group-level ledgers, and performs settlement.
- Data Stores (in-memory singletons)
    - UserData: users and user splits.
    - GroupData: groups and their transactions.
    - TransactionData: transactions.

Pattern highlights:
- Factory pattern for transaction creation per PaymentType.
- Template method pattern inside Transaction: base validation + type-specific validation + charge calculation.
- Facade pattern via SplitWiseManager to coordinate multiple services.

## Data Flow

Create Transaction:
1. Client calls SplitWiseManager.createTransaction(...).
2. TransactionService.validateInputs(...) checks users, group, amount, description, and paymentType.
3. TransactionData.addTransaction(...) creates a type-specific transaction via factory and runs validations:
    - Transaction.validateInputs(...)
    - Transaction.validateAdditionalLenderInformation(...)
    - Transaction.calculateCharges() produces Splits.
4. UserService.addTransactionToUsers(...) attaches generated Splits to lender and all borrowers.
5. If groupId is present, GroupService.addTransactionToGroup(...) registers the transaction in the group.

Query Ledgers:
- TransactionService.getTransactions(fromId) aggregates the user’s splits into net costs per counterparty:
    - If user is lender in a split → positive amount (receivable).
    - If user is borrower in a split → negative amount (payable).
- Pairwise ledger (fromId, toId) reduces to a single net Ledger.

Settle Expenses:
- TransactionService.settleExpenses(fromId, toId) retrieves the pairwise ledger and, if positive from the perspective of fromId, marks matching splits between the pair as disabled (effectively settling those obligations).

## Validation

Input validation happens at service boundaries and transaction creation:
- User and Group IDs must be positive and exist.
- Transaction parameters (description, amount, paymentType) must be valid.
- lendeeShares must not be null/empty; all lendee IDs must exist.
- Type-specific constraints:
    - EXACT: sum of shares equals amount.
    - PERCENTAGE: shares are non-negative and sum to 100.0.
    - EQUAL: derived automatically by participant count.

Error handling:
- Throw IllegalArgumentException with descriptive messages for invalid inputs and state violations.

## Ledger Computation

User-centric ledger:
- For each enabled split involving user U:
    - If U == lender, add +amount to the ledger from U to borrower.
    - If U == borrower, add −amount to the ledger from U to lender.
- Aggregation:
    - Reduce all splits into unique ledgers per target counterparty (sum of costs).
    - Filter out zeros (fully settled pairs).

Group-centric ledger:
- For all transactions in the group:
    - Flatten splits and create two ledger views for each split (from borrower perspective and from lender perspective) to make per-user queries simple.
    - Aggregate by (fromUser, toUser) key and filter zeros.

Note on numerical precision:
- Current model uses double for amounts. For production, prefer BigDecimal with currency-specific scale and rounding mode (e.g., HALF_EVEN) to avoid floating-point errors.

## Concurrency

- In-memory stores use thread-safe collections:
    - ConcurrentHashMap for entities
    - Synchronized lists/sets for collections in entities
- Splits lists are synchronized at the collection level. For high concurrency, consider:
    - Minimizing shared mutable state
    - Using immutable value objects for splits and transactions
    - Defining more granular synchronization strategies or a persistence layer with transactions

## Extensibility

- Additional split strategies:
    - RATIO-based splits, discounts, taxes, or multi-currency normalization
- Persistence:
    - Replace in-memory stores with repository interfaces and a database layer
- Settlement:
    - Introduce cash-flow minimization algorithms to suggest optimized payments across multiple users
- Audit and soft delete:
    - Keep historical transactions and settlement records; never mutate splits, only mark or append events

## Usage Examples

Create users, a group, add members, create transactions, inspect ledgers, and settle. Example demonstrates API usage via the facade.

```java
// java
import org.example.split.wise.manager.SplitWiseManager;
import org.example.split.wise.model.*;

import java.util.List;
import java.util.Map;

public class Demo {
    public static void main(String[] args) {
        SplitWiseManager api = SplitWiseManager.getInstance();

        // Users
        User u1 = api.createUser("alice", "alice@example.com");
        User u2 = api.createUser("bob", "bob@example.com");
        User u3 = api.createUser("carol", "carol@example.com");

        // Group
        Group trip = api.createGroup("NYC Trip", "Friends weekend trip");
        api.addUserToGroup(trip.getId(), u1.getId());
        api.addUserToGroup(trip.getId(), u2.getId());
        api.addUserToGroup(trip.getId(), u3.getId());

        // Equal split: 90 across 3 → each owes 30 to lender (u1)
        api.createTransaction(
            PaymentType.EQUAL,
            u1.getId(),
            "Dinner day 1",
            90.0,
            Map.of(u1.getId(), 1.0, u2.getId(), 1.0, u3.getId(), 1.0),
            trip.getId()
        );

        // Percentage split: 120, alice 50%, bob 25%, carol 25% (lender u2)
        api.createTransaction(
            PaymentType.PERCENTAGE,
            u2.getId(),
            "City Pass",
            120.0,
            Map.of(u1.getId(), 50.0, u2.getId(), 25.0, u3.getId(), 25.0),
            trip.getId()
        );

        // Exact split: 100 with exact amounts (lender u3)
        api.createTransaction(
            PaymentType.EXACT,
            u3.getId(),
            "Taxi",
            100.0,
            Map.of(u1.getId(), 40.0, u2.getId(), 30.0, u3.getId(), 30.0),
            trip.getId()
        );

        // Ledgers for alice
        List<Ledger> aliceLedgers = api.getTransactions(u1.getId());
        // Pairwise ledger: alice vs bob
        Ledger aliceBob = api.getTransactions(u1.getId(), u2.getId());

        // Group-level ledgers
        List<Ledger> groupLedgers = api.getGroupTransactions(trip.getId());

        // Settle alice -> bob if alice owes bob
        api.settleExpenses(u1.getId(), u2.getId());

        // Check pairwise again after settlement
        Ledger after = api.getTransactions(u1.getId(), u2.getId());
    }
}
```


Notes:
- Passing groupId as null creates a personal transaction outside groups.
- lendeeShares map keys must include all participants (including lender if they share the cost).

## Observability and Errors

- Favor structured logging around transaction creation and settlement.
- Service methods throw IllegalArgumentException for invalid inputs; wrap or translate at a higher layer if exposing APIs.

## Recommendations for Production Hardening

- Replace double with BigDecimal for monetary values.
- Add domain-specific exceptions and error codes.
- Introduce repositories and transactions with a real database.
- Implement idempotency for transaction creation.
- Add authorization and constraints (e.g., only group members can post to the group).
- Add pagination for listing group transactions and user splits.

## Summary

SplitWise provides a clean, layered design for expense sharing:
- Clear domain model with Splits as the atomic ledger unit
- Type-specific transaction creation through a factory and template method
- Simple yet effective ledger aggregation and settlement semantics
- Extensible foundation ready for persistence, richer settlement, and production concerns