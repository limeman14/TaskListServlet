package org.gurenko.vladislav.tasklistwebservice.service;

import org.gurenko.vladislav.tasklistwebservice.model.Task;

import java.util.List;

public interface TaskService {

    Task getSingleTaskOfUser(int taskId, int userId);

    List<Task> getAllTasksOfUser(int userId);

    int addTask(Task taskToAdd);

    Task editTask(int taskId, Task newTask);

    int deleteTask(int taskId);
}
