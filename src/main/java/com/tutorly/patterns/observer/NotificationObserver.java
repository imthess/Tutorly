package com.tutorly.patterns.observer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.tutorly.database.DatabaseConnection;

public class NotificationObserver implements Observer {

    @Override
    public void update(
            int userId,
            String message,
            String notificationType
    ) {

        String sql = """
                INSERT INTO notifications
                (
                    user_id,
                    message,
                    notification_type,
                    is_read
                )
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, userId);
            statement.setString(2, message);
            statement.setString(3, notificationType);
            statement.setBoolean(4, false);

            statement.executeUpdate();

            System.out.println(
                    "Notification created for user "
                            + userId
                            + ": "
                            + message
            );

        } catch (SQLException e) {

            System.err.println(
                    "Failed to create notification: "
                            + e.getMessage()
            );
        }
    }
}