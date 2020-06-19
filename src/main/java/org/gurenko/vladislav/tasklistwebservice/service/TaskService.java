package org.gurenko.vladislav.tasklistwebservice.service;

import org.gurenko.vladislav.tasklistwebservice.model.Task;
import org.gurenko.vladislav.tasklistwebservice.util.ConnectionPool;
import org.gurenko.vladislav.tasklistwebservice.util.ConnectionPoolException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TaskService {

    public static List<Task> getUserAllTasks(int creatorId) {
        List<Task> tasks = new ArrayList<>();
        try {
            Connection connection = ConnectionPool.getInstance().retrieveConnection();

            String sql = "SELECT * FROM tasks WHERE creator_id = ?;";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, creatorId);
                ResultSet rs = statement.executeQuery();
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String taskName = rs.getString("task_name");
                    String description = rs.getString("description");
                    LocalDateTime dueDate = rs.getTimestamp("due_date") == null ? null : rs.getTimestamp("due_date").toLocalDateTime();
                    boolean isDone = rs.getBoolean("is_done");
                    rs.getInt("creator_id");
                    tasks.add(new Task(id, taskName, description, dueDate, isDone, creatorId));
                }
                rs.close();
            }
            ConnectionPool.getInstance().returnConnection(connection);
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
            System.err.println(e.getMessage());
        } catch (ConnectionPoolException e) {
            System.err.println(e.getMessage());
        }
        return tasks;
    }

    public static int addTask(Task task) {
        int res = 0;
        try {
            Connection connection = ConnectionPool.getInstance().retrieveConnection();

            String sql = "INSERT INTO tasks (task_name, description, due_date, is_done, creator_id) VALUES (?, ?, ?, ?, ?);";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, task.getTaskName());
                statement.setString(2, task.getDescription());
                statement.setObject(3, task.getDueDate());
                statement.setBoolean(4, task.getDone());
                statement.setInt(5, task.getCreatorId());
                res = statement.executeUpdate();
            }
            ConnectionPool.getInstance().returnConnection(connection);
        } catch (SQLException e) {
            System.err.println(e.getSQLState());
            System.err.println(e.getMessage());
        } catch (ConnectionPoolException e) {
            System.err.println(e.getMessage());
        }
        return res;
    }

    public static Task getUserTaskById(int taskId, int creatorId) {
        Task task = new Task();
        try {
            Connection connection = ConnectionPool.getInstance().retrieveConnection();
            String sql = "SELECT * FROM tasks WHERE id=? AND creator_id = ?;";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setInt(1, taskId);
                statement.setInt(2, creatorId);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    task.setId(resultSet.getInt(1));
                    task.setTaskName(resultSet.getString(2));
                    task.setDescription(resultSet.getString(3));
                    task.setDueDate(resultSet.getTimestamp(4) == null ? null : resultSet.getTimestamp(4).toLocalDateTime());
                    task.setDone(resultSet.getBoolean(5));
                    task.setCreatorId(resultSet.getInt(6));
                }
                resultSet.close();
            }
            ConnectionPool.getInstance().returnConnection(connection);
        } catch (SQLException | ConnectionPoolException e) {
            e.printStackTrace();
        }
        return task;
    }

    public static Task editTask(int taskId, Task newTask) {
        try {
            Connection connection = ConnectionPool.getInstance().retrieveConnection();
            String sql = "UPDATE tasks SET task_name = ?, description = ?, due_date = ?, is_done = ? WHERE id = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, newTask.getTaskName());
                statement.setString(2, newTask.getDescription());
                statement.setObject(3, newTask.getDueDate());
                statement.setBoolean(4, newTask.getDone());
                statement.setInt(5, taskId);
                statement.executeUpdate();
            }
            ConnectionPool.getInstance().returnConnection(connection);
        } catch (SQLException | ConnectionPoolException e) {
            e.printStackTrace();
        }
        return getUserTaskById(taskId, newTask.getCreatorId());
    }
}
