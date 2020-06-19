package org.gurenko.vladislav.tasklistwebservice.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Использование пула соединений с базой данных на случай многопользовательского использования
 * Альтернативно можно использовать пул, предоставляемый tomcat, через META-INF/context.xml
 *
 * @author Gurenko Vladislav
 */
public final class ConnectionPool {

    private static ConnectionPool instance;
    private BlockingQueue<Connection> availableConnections;
    private BlockingQueue<Connection> usedConnections;

    private final String DRIVER_NAME = "com.mysql.cj.jdbc.Driver";
    private final String URL = "jdbc:mysql://localhost:3306/tasklist?characterEncoding=utf8&serverTimezone=Europe/Moscow";
    private final String USER_NAME = "root";
    private final String PASSWORD = "testtest";
    private final int POOL_SIZE = 5;


    private ConnectionPool() throws ConnectionPoolException {
        try {
            Class.forName(DRIVER_NAME); //выполнение инициализации статической части драйвера
            availableConnections = new ArrayBlockingQueue<>(POOL_SIZE);
            usedConnections = new ArrayBlockingQueue<>(POOL_SIZE);
            for (int i = 0; i < POOL_SIZE; i++) {
                availableConnections.add(DriverManager.getConnection(URL, USER_NAME, PASSWORD));
            }
        } catch (ClassNotFoundException e) {
            throw new ConnectionPoolException("Can't find DB driver class", e);
        } catch (SQLException e) {
            throw new ConnectionPoolException("SQLException in ConnectionPool", e);
        }
    }

    public static ConnectionPool getInstance() throws ConnectionPoolException {
        if (instance == null) {
            instance = new ConnectionPool();
        }
        return instance;
    }

    public synchronized Connection retrieveConnection() throws ConnectionPoolException {
        Connection connection = null;
        try {
            connection = availableConnections.take();
            usedConnections.add(connection);
        } catch (InterruptedException e) {
            throw new ConnectionPoolException("Error connecting to datasource", e);
        }
        return connection;
    }

    public synchronized void returnConnection(Connection c) {
        if (c != null) {
            if (usedConnections.remove(c)) {
                availableConnections.offer(c);
            } else {
                throw new NullPointerException("Connection is not in use");
            }
        }
    }

    public void clearConnectionQueue() {
        try {
            closeConnectionsQueue(usedConnections);
            closeConnectionsQueue(availableConnections);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void closeConnectionsQueue(BlockingQueue<Connection> queue) throws SQLException {
        Connection connection;
        while ((connection = queue.poll()) != null) {
            connection.close();
        }
    }
}
