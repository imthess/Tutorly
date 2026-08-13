package com.tutorly.patterns.singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton responsible for managing the application's
 * single database connection.
 */
public final class DatabaseSingleton {

    private static DatabaseSingleton instance;

    private Connection connection;

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

    private DatabaseSingleton() {
    }

    public static synchronized DatabaseSingleton getInstance() {
        if (instance == null) {
            instance = new DatabaseSingleton();
        }

        return instance;
    }

    public synchronized Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(
                    URL,
                    USER,
                    PASSWORD
            );
        }

        return connection;
    }

    public synchronized void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException ignored) {
                // Connection is already being closed.
            }

            connection = null;
        }
    }
}
