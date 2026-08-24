package com.tutorly.patterns.singleton;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Singleton responsible for managing the application's
 * database connection.
 *
 * Thread-safe implementation using an explicit mutex
 * (ReentrantLock).
 */
public final class DatabaseSingleton {

    private static volatile DatabaseSingleton instance;

    private Connection connection;

    /*
     * Mutex protecting Singleton initialization.
     */
    private static final ReentrantLock INSTANCE_LOCK =
            new ReentrantLock();

    /*
     * Mutex protecting the database connection.
     */
    private final ReentrantLock CONNECTION_LOCK =
            new ReentrantLock();

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

    /**
     * Thread-safe Singleton access using
     * double-checked locking and an explicit mutex.
     */
    public static DatabaseSingleton getInstance() {

        DatabaseSingleton result = instance;

        if (result == null) {

            INSTANCE_LOCK.lock();

            try {

                result = instance;

                if (result == null) {

                    result = new DatabaseSingleton();

                    instance = result;
                }

            } finally {

                INSTANCE_LOCK.unlock();
            }
        }

        return result;
    }

    /**
     * Returns the database connection.
     *
     * The mutex guarantees that only one thread can
     * create or modify the shared connection at a time.
     */
    public Connection getConnection() throws SQLException {

        CONNECTION_LOCK.lock();

        try {

            if (connection == null ||
                    connection.isClosed()) {

                connection = DriverManager.getConnection(
                        URL,
                        USER,
                        PASSWORD
                );
            }

            return connection;

        } finally {

            CONNECTION_LOCK.unlock();
        }
    }

    /**
     * Safely closes the database connection.
     */
    public void closeConnection() {

        CONNECTION_LOCK.lock();

        try {

            if (connection != null) {

                try {
                    connection.close();

                } catch (SQLException ignored) {
                    // Connection is already being closed.
                }

                connection = null;
            }

        } finally {

            CONNECTION_LOCK.unlock();
        }
    }
}
