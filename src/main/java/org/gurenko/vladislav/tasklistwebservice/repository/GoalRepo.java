package org.gurenko.vladislav.tasklistwebservice.repository;

import org.gurenko.vladislav.tasklistwebservice.model.Goal;
import org.gurenko.vladislav.tasklistwebservice.util.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class GoalRepo {

    public static Goal getUserGoalById(int goalId, int creatorId) {
        Goal goal = null;
        String sql = "SELECT * FROM goals WHERE id=? AND creator_id = ?;";
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, goalId);
            statement.setInt(2, creatorId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int id = resultSet.getInt("id");
                goal = Goal.builder()
                        .id(id)
                        .parentGoal(resultSet.getInt("parent_goal"))
                        .creatorId(resultSet.getInt("creator_id"))
                        .description(resultSet.getString("description"))
                        .subGoalsCounter(countSubGoals(id))
                        .assignedTasks(TaskRepo.getGoalAllTasks(id))
                        .build();
            }
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return goal;
    }

    public static List<Goal> getAllUserGoals(int creatorId) {
        List<Goal> goals = new ArrayList<>();
        String sql = "SELECT * FROM goals WHERE creator_id = ?;";
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, creatorId);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                Integer id = rs.getInt("id");
                String description = rs.getString("description");
                Integer parentGoal = rs.getInt("parent_goal");
                goals.add(Goal.builder()
                        .creatorId(creatorId)
                        .id(id)
                        .description(description)
                        .parentGoal(parentGoal)
                        .subGoalsCounter(countSubGoals(id))
                        .assignedTasks(TaskRepo.getGoalAllTasks(id))         //Получение заданий, связанных с целью
                        .build());
            }
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return goals;
    }

    private static Integer countSubGoals(int parentGoalId) {
        int counter = 0;
        String sql = "SELECT COUNT(*) FROM goals WHERE parent_goal = ?;";
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, parentGoalId);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                counter = rs.getInt(1);
            }
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return counter;
    }

    public static int addGoal(Goal goal) {
        int res = 0;
        String sql = "INSERT INTO goals (description, creator_id) VALUES (?, ?);";
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, goal.getDescription());
            statement.setInt(2, goal.getCreatorId());
            res = statement.executeUpdate();
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return res;
    }

    public static int deleteGoal(int goalId) {
        int res = 0;
        String sqlToDeleteGoal = "DELETE FROM goals WHERE id = ?;";
        String sqlToClearTasks = "UPDATE tasks SET goal_id = NULL WHERE goal_id = ?;";
        try (Connection connection = DataSource.getConnection();
             PreparedStatement disableSafety = connection.prepareStatement("SET SQL_SAFE_UPDATES = 0;");
             PreparedStatement statement = connection.prepareStatement(sqlToDeleteGoal);
             PreparedStatement statement2 = connection.prepareStatement(sqlToClearTasks)) {
            disableSafety.executeUpdate();
            statement.setInt(1, goalId);
            res = statement.executeUpdate();
            statement2.setInt(1, goalId);
            statement2.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        return res;
    }

    public static int addOrRemoveParentGoal(int parentGoalId, int goalId) {
        String sql = "UPDATE goals SET parent_goal = ? WHERE id = ?";
        int res = 0;
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setInt(1, parentGoalId);
            statement.setInt(2, goalId);
            res = statement.executeUpdate();
        }
        catch (SQLException e) {
            e.printStackTrace();
            System.err.println(e.getMessage());
        }
        return res;
    }

}
