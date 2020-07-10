package org.gurenko.vladislav.tasklistwebservice.repository;

import org.gurenko.vladislav.tasklistwebservice.model.Task;
import org.gurenko.vladislav.tasklistwebservice.util.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskRepo {

    public static Task getUserTaskById(int taskId, int creatorId) {
        Task task = new Task();
        String sql = "SELECT * FROM tasks WHERE id=? AND creator_id = ?;";
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, taskId);
            statement.setInt(2, creatorId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                task.setId(resultSet.getInt(1));
                task.setTaskName(resultSet.getString(2));
                task.setDescription(resultSet.getString(3));
                task.setDueDate(resultSet.getTimestamp(4) == null ? null : resultSet.getTimestamp(4).toLocalDateTime());
                task.setIsDone(resultSet.getBoolean(5));
                task.setCreatorId(resultSet.getInt(6));
                task.setGoalId(resultSet.getInt(7));
            }
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return task;
    }

    public static List<Task> getUserAllTasks(int creatorId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE creator_id = ?;";
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, creatorId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                String taskName = rs.getString("task_name");
                String description = rs.getString("description");
                LocalDateTime dueDate = rs.getTimestamp("due_date") == null ? null : rs.getTimestamp("due_date").toLocalDateTime();
                boolean isDone = rs.getBoolean("is_done");
                int goalId = rs.getInt("goal_id");
                tasks.add(Task.builder()
                        .creatorId(creatorId)
                        .id(id)
                        .taskName(taskName)
                        .description(description)
                        .dueDate(dueDate)
                        .isDone(isDone)
                        .goalId(goalId)
                        .build());
            }
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return tasks;
    }

    public static List<Task> getGoalAllTasks (int goalId) {
        List<Task> tasks = new ArrayList<>();
        String sql = "SELECT * FROM tasks WHERE goal_id = ?;";
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, goalId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                int id = rs.getInt("id");
                int creatorId = rs.getInt("creator_id");
                String taskName = rs.getString("task_name");
                String description = rs.getString("description");
                LocalDateTime dueDate = rs.getTimestamp("due_date") == null ? null : rs.getTimestamp("due_date").toLocalDateTime();
                boolean isDone = rs.getBoolean("is_done");
                tasks.add(Task.builder()
                        .creatorId(creatorId)
                        .id(id)
                        .taskName(taskName)
                        .description(description)
                        .dueDate(dueDate)
                        .isDone(isDone)
                        .goalId(goalId)
                        .build());
            }
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return tasks;
    }

    public static int addTask(Task task) {
        int res = 0;
        String sql = "INSERT INTO tasks (task_name, description, due_date, is_done, creator_id) VALUES (?, ?, ?, ?, ?);";
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, task.getTaskName());
            statement.setString(2, task.getDescription());
            statement.setObject(3, task.getDueDate());
            statement.setBoolean(4, task.getIsDone());
            statement.setInt(5, task.getCreatorId());
            res = statement.executeUpdate();
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return res;
    }

    public static Task editTask(int taskId, Task newTask) {
        String sql = "UPDATE tasks SET task_name = ?, description = ?, due_date = ?, is_done = ?, goal_id = ? WHERE id = ?";
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, newTask.getTaskName());
            statement.setString(2, newTask.getDescription());
            statement.setObject(3, newTask.getDueDate());
            statement.setBoolean(4, newTask.getIsDone());
            statement.setInt(5, newTask.getGoalId());
            statement.setInt(6, taskId);
            statement.executeUpdate();
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return getUserTaskById(taskId, newTask.getCreatorId());
    }

    public static int deleteTask(int taskId) {
        int res = 0;
        String sql = "DELETE FROM tasks WHERE id = ?;";
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, taskId);
            res = statement.executeUpdate();
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return res;
    }
}
