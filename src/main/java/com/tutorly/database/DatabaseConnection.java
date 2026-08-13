package com.tutorly.database;

import com.tutorly.patterns.singleton.DatabaseSingleton;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Application-facing database connection utility.
 *
 * The actual connection lifecycle is managed by DatabaseSingleton.
 */
public final class DatabaseConnection {

    private DatabaseConnection() {
    }

    public static Connection getConnection() throws SQLException {
        return DatabaseSingleton.getInstance().getConnection();
    }

    public static void close() {
        DatabaseSingleton.getInstance().closeConnection();
    }
}
