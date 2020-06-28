package org.gurenko.vladislav.tasklistwebservice.service.implementations;

import org.gurenko.vladislav.tasklistwebservice.model.Task;
import org.gurenko.vladislav.tasklistwebservice.service.TaskService;

import java.util.List;

public class TaskServiceImpl implements TaskService {
    @Override
    public Task getSingleTaskOfUser(int taskId, int userId) {
        return null;
    }

    @Override
    public List<Task> getAllTasksOfUser(int userId) {
        return null;
    }

    @Override
    public int addTask(Task taskToAdd) {
        return 0;
    }

    @Override
    public Task editTask(int taskId, Task newTask) {
        return null;
    }

    @Override
    public int deleteTask(int taskId) {
        return 0;
    }
}
