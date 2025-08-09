package org.example.task.management.system;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class UserTaskManager {

    private static final Map<Integer, Set<Task>> userTasks = new ConcurrentHashMap<>();

    public static void assignTaskToUser(int taskId, int userId) {
        if (UserManager.userExists(userId) && TaskManager.taskExists(taskId)) {
            Task task = TaskManager.getTask(taskId);
            Integer currentAssignedUser = task.getAssignedUser();
            if (TaskManager.assignUser(taskId, userId)) {
                userTasks.computeIfAbsent(userId, k -> new ConcurrentSkipListSet<>()).add(task);
                if (currentAssignedUser != null && currentAssignedUser != userId) {
                    userTasks.compute(currentAssignedUser, (k, v) -> {
                        if (v != null) {
                            v.remove(task);
                            if (v.isEmpty()) {
                                return null;
                            }
                        }
                        return v;
                    });
                    userTasks.get(currentAssignedUser).remove(task);
                }
            }
        }
    }

    public static List<Task> getTasksByAssignedUser(int userId) {
        return userTasks.getOrDefault(userId, Collections.emptySet())
                .stream()
                .sorted(Comparator.comparingInt(Task::getId))
                .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
    }

    public static List<Task> getTasksByPriority(Priority priority) {
        return TaskManager.getTasksByPriority(priority);
    }

    public static List<Task> getTasksByDueDate(Date fromDate, Date toDate) {
        return TaskManager.getTasksByDueDate(fromDate, toDate);
    }
}
