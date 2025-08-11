package org.example.split.wise.data;

import lombok.NoArgsConstructor;
import org.example.split.wise.model.Split;
import org.example.split.wise.model.Group;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
public class GroupData {

    private static volatile GroupData INSTANCE;
    private final Map<Integer, Group> groups = new ConcurrentHashMap<>();
    private final AtomicInteger groupId = new AtomicInteger(0);

    public static GroupData getInstance() {
        if (INSTANCE == null) {
            synchronized (GroupData.class) {
                if (INSTANCE == null) {
                    INSTANCE = new GroupData();
                }
            }
        }
        return INSTANCE;
    }

    public Group addGroup(String name, String description) {
        int id = groupId.incrementAndGet();
        Group group = new Group(id, name, description);
        groups.put(id, group);
        return group;
    }


    public boolean isGroupExists(int groupId) {
        return groups.containsKey(groupId);
    }

    public Group getGroup(int groupId) {
        return groups.get(groupId);
    }
}
