package org.gurenko.vladislav.tasklistwebservice.util;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

public class MyContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        System.out.println("************** Starting up! **************");

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        System.out.println("************** Shutting down! **************");

        //Closing connections in connection pool

        System.out.println("Closing All Connections from Connection Pool");
        try {
            ConnectionPool.getInstance().clearConnectionQueue();
        } catch (ConnectionPoolException e) {
            e.printStackTrace();
        }

        //Manually deregistering MySQL JDBC driver

        System.out.println("Destroying Context...");
        System.out.println("Calling MySQL AbandonedConnectionCleanupThread checkedShutdown");
        AbandonedConnectionCleanupThread.checkedShutdown();

        ClassLoader cl = Thread.currentThread().getContextClassLoader();

        Enumeration<Driver> drivers = DriverManager.getDrivers();
        while (drivers.hasMoreElements()) {
            Driver driver = drivers.nextElement();

            if (driver.getClass().getClassLoader() == cl) {
                try {
                    System.out.println("Deregistering JDBC driver " + driver.toString());
                    DriverManager.deregisterDriver(driver);

                } catch (SQLException ex) {
                    System.out.println("Error deregistering JDBC driver " + driver.toString());
                    ex.printStackTrace();
                }
            } else {
                System.out.println("Not deregistering JDBC driver as it does not belong to this webapp's ClassLoader");
            }
        }
    }

}