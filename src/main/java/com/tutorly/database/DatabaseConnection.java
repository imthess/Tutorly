package com.tutorly.database;

import com.tutorly.patterns.singleton.DatabaseSingleton;

import java.sql.Connection;
import java.sql.SQLException;

public final class DatabaseConnection {

    private DatabaseConnection() {
    }

    public static Connection getConnection()
            throws SQLException {

        return DatabaseSingleton
                .getInstance()
                .getConnection();
    }

    public static void close() {

        DatabaseSingleton
                .getInstance()
                .closeConnection();
    }
}