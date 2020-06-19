package org.gurenko.vladislav.tasklistwebservice.service;

import org.gurenko.vladislav.tasklistwebservice.model.User;
import org.gurenko.vladislav.tasklistwebservice.util.ConnectionPool;
import org.gurenko.vladislav.tasklistwebservice.util.ConnectionPoolException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserService {

    public static int addUser(User user) {
        int res = 0;
        try {
            Connection connection = ConnectionPool.getInstance().retrieveConnection();

            String sql = "INSERT INTO users (login, password, first_name, last_name, role) VALUES (?, ?, ?, ?, ?);";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, user.getLogin());
                statement.setString(2, user.getPassword());
                statement.setString(3, user.getFirstName());
                statement.setString(4, user.getLastName());
                statement.setString(5, user.getRole().name());
                res = statement.executeUpdate();
            }
            ConnectionPool.getInstance().returnConnection(connection);
        } catch (SQLException | ConnectionPoolException e) {
            System.err.println(e.getMessage());
        }
        return res;
    }

    public static User getUserByLogin(String login) {
        User user = new User();
        try {
            Connection connection = ConnectionPool.getInstance().retrieveConnection();

            String sql = "SELECT * FROM users WHERE login=?;";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, login);
                ResultSet resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    user.setId(resultSet.getInt(1));
                    user.setLogin(resultSet.getString(2));
                    user.setPassword(resultSet.getString(3));
                    user.setFirstName(resultSet.getString(4));
                    user.setLastName(resultSet.getString(5));
                    user.setRole(Enum.valueOf(User.UserRole.class, resultSet.getString(6)));
                }
                resultSet.close();
            }
            ConnectionPool.getInstance().returnConnection(connection);
        } catch (SQLException | ConnectionPoolException e) {
            e.printStackTrace();
        }
        return user;
    }
}
