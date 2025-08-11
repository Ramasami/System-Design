package org.example.split.wise.service;

import lombok.NoArgsConstructor;
import org.example.split.wise.data.GroupData;
import org.example.split.wise.model.Group;
import org.example.split.wise.model.Transaction;

import java.util.Objects;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class GroupService {

    private static volatile GroupService INSTANCE;

    public static GroupService getInstance() {
        if (INSTANCE == null) {
            synchronized (GroupService.class) {
                if (INSTANCE == null) {
                    INSTANCE = new GroupService();
                }
            }
        }
        return INSTANCE;
    }

    public Group addGroup(String name, String description) {
        validateInputs(name, description);
        return GroupData.getInstance().addGroup(name, description);
    }

    public boolean addUserToGroup(int groupId, int userId) {
        validateUser(userId);
        validateGroupId(groupId);
        if (!UserService.getInstance().isUserExists(userId)) {
            throw new IllegalArgumentException("User not present");
        }
        if (!isGroupExists(groupId)) {
            throw new IllegalArgumentException("Group not present");
        }
        return getGroup(groupId).addMember(userId);
    }

    private void validateUser(int userId) {
        if (userId < 0) {
            throw new IllegalArgumentException("UserId is negative");
        }
    }

    public boolean isGroupExists(int groupId) {
        validateGroupId(groupId);
        return GroupData.getInstance().isGroupExists(groupId);
    }

    public Group getGroup(int groupId) {
        validateGroupId(groupId);
        Group group = GroupData.getInstance().getGroup(groupId);
        if (group == null) {
            throw new IllegalArgumentException("Group with ID " + groupId + " does not exist");
        }
        return group;
    }

    private void validateGroupId(int groupId) {
        if (groupId < 0) {
            throw new IllegalArgumentException("groupId cannot be negative");
        }
    }

    private void validateInputs(String name, String description) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be null or empty");
        }
    }

    public void addTransactionToGroup(Integer groupId, Transaction transaction) {
        validateGroupId(groupId);
        validateTransactionInput(groupId, transaction);
        if (!isGroupExists(groupId)) {
            throw  new IllegalArgumentException("Group with ID " + groupId + " does not exist");
        }
        getGroup(groupId).addTransaction(transaction);
    }

    private void validateTransactionInput(Integer groupId, Transaction transaction) {
        if (transaction == null) {
            throw new IllegalArgumentException("transaction cannot be null");
        }
        if (!Objects.equals(transaction.getGroupId(), groupId)) {
            throw new IllegalArgumentException("transaction groupId does not match");
        }
    }
}
