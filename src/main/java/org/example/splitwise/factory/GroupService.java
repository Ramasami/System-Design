package org.example.splitwise.factory;

import org.example.splitwise.model.Group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class GroupService {
    private static int groupIdCounter = 1;
    private static Map<Integer, Group> groupIdMap = new HashMap<>();

    public synchronized static Group createGroup(String groupName) {
        Group group = new Group();
        group.setId(groupIdCounter++);
        group.setGroupName(groupName);
        group.setUsers(new HashSet<>());
        group.setExpenses(new ArrayList<>());
        groupIdMap.put(group.getId(), group);
        return group;
    }

    public static Group getGroupById(int id) {
        return groupIdMap.get(id);
    }

    public static void addUserToGroup(int groupId, int userId) {
        Group group = getGroupById(groupId);
        if (group != null) {
            group.getUsers().add(userId);
        }
    }

    public static boolean isGroupPresent(int id) {
        return groupIdMap.containsKey(id);
    }
}
