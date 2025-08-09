package org.example.task.management.system;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class TaskManager {

    private static final Map<Integer, Task> tasks = new ConcurrentHashMap<>();
    private static final AtomicInteger taskIdCount = new AtomicInteger(1);

    public static int createTask(String title, String description, Date dueDate, Priority priority, Status status, int assignedUser, int createdBy) {
        int taskId = taskIdCount.getAndIncrement();
        Task task = new Task(taskId, title, description, dueDate, priority, status, assignedUser, createdBy);
        tasks.put(taskId, task);
        return taskId;
    }

    public static boolean deleteTask(int taskId) {
        return Objects.nonNull(tasks.remove(taskId));
    }

    public static boolean updateTitle(int taskId, String title) {
        return Objects.nonNull(tasks.computeIfPresent(taskId, (id, task) -> {
            task.setTitle(title);
            return task;
        }));
    }

    public static boolean updateDescription(int taskId, String description) {
        return Objects.nonNull(tasks.computeIfPresent(taskId, (id, task) -> {
            task.setDescription(description);
            return task;
        }));
    }

    public static boolean updateDueDate(int taskId, Date dueDate) {
        return Objects.nonNull(tasks.computeIfPresent(taskId, (id, task) -> {
            task.setDueDate(dueDate);
            return task;
        }));
    }

    public static boolean updatePriority(int taskId, Priority priority) {
        return Objects.nonNull(tasks.computeIfPresent(taskId, (id, task) -> {
            task.setPriority(priority);
            return task;
        }));
    }

    public static boolean updateStatus(int taskId, Status status) {
        return Objects.nonNull(tasks.computeIfPresent(taskId, (id, task) -> {
            task.setStatus(status);
            return task;
        }));
    }

    public static boolean assignUser(int taskId, Integer user) {
        return Objects.nonNull(tasks.computeIfPresent(taskId, (id, task) -> {
            task.setAssignedUser(user);
            return task;
        }));
    }

    public static boolean unassignUser(int taskId) {
        return Objects.nonNull(tasks.computeIfPresent(taskId, (id, task) -> {
            task.setAssignedUser(null);
            return task;
        }));
    }

    public static Task getTask(int taskId) {
        return tasks.get(taskId);
    }


    public static boolean taskExists(int taskId) {
        return tasks.containsKey(taskId);
    }

    public static Integer getAssignedUser(int taskId) {
        return tasks.get(taskId) != null ? tasks.get(taskId).getAssignedUser() : null;
    }

    public static List<Task> getTasksByPriority(Priority priority) {
        return tasks.values().stream()
                .filter(task -> task.getPriority() == priority)
                .toList();
    }

    public static List<Task> getTasksByDueDate(Date fromDate, Date toDate) {
        return tasks.values().stream()
                .filter(task -> task.getDueDate() != null)
                .filter(task -> fromDate == null || task.getDueDate().after(fromDate))
                .filter(task -> toDate == null || task.getDueDate().before(toDate))
                .toList();
    }
}
