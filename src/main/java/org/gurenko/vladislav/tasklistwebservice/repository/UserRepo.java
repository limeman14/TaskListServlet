package org.gurenko.vladislav.tasklistwebservice.repository;

import org.gurenko.vladislav.tasklistwebservice.model.User;
import org.gurenko.vladislav.tasklistwebservice.util.DataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserRepo {

    public static int addUser(User user) {
        int res = 0;
        String sql = "INSERT INTO users (login, password, first_name, last_name) VALUES (?, ?, ?, ?);";
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, user.getLogin());
            statement.setString(2, user.getPassword());
            statement.setString(3, user.getFirstName());
            statement.setString(4, user.getLastName());
            res = statement.executeUpdate();
        }
        catch (SQLException e) {
            System.err.println(e.getMessage());
        }
        return res;
    }

    public static User getUserByLogin(String login) {
        User user = new User();
        String sql = "SELECT * FROM users WHERE login=?;";
        try (Connection connection = DataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, login);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                user.setId(resultSet.getInt(1));
                user.setLogin(resultSet.getString(2));
                user.setPassword(resultSet.getString(3));
                user.setFirstName(resultSet.getString(4));
                user.setLastName(resultSet.getString(5));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
}
