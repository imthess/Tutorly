package com.tutorly.patterns.singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class DatabaseSingleton {

    private static final String URL =
            System.getenv().getOrDefault(
                    "TUTORLY_DB_URL",
                    "jdbc:mysql://localhost:3306/tutorly"
            );

    private static final String USER =
            System.getenv().getOrDefault(
                    "TUTORLY_DB_USER",
                    "root"
            );

    private static final String PASSWORD =
            System.getenv().getOrDefault(
                    "TUTORLY_DB_PASSWORD",
                    ""
            );

    private final ThreadLocal<Connection> threadLocalConnection = new ThreadLocal<>();

    private DatabaseSingleton() {
    }

    private static class InstanceHolder {
        private static final DatabaseSingleton INSTANCE = new DatabaseSingleton();
    }

    public static DatabaseSingleton getInstance() {
        return InstanceHolder.INSTANCE;
    }

    public Connection getConnection() throws SQLException {
        Connection connection = threadLocalConnection.get();

        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            threadLocalConnection.set(connection);
        }

        return connection;
    }

    public void closeConnection() {
        Connection connection = threadLocalConnection.get();

        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException ignored) {
            } finally {
                threadLocalConnection.remove();
            }
        }
    }
}