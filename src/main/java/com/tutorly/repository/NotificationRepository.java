package com.tutorly.repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.tutorly.database.DatabaseConnection;
import com.tutorly.model.Notification;

public class NotificationRepository {

    public List<Notification> findByUserId(int userId)
            throws SQLException {

        String sql = """
                SELECT
                    notification_id,
                    user_id,
                    message,
                    notification_type,
                    is_read,
                    created_at
                FROM notifications
                WHERE user_id = ?
                ORDER BY created_at DESC
                """;

        List<Notification> notifications = new ArrayList<>();

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                while (resultSet.next()) {

                    Notification notification =
                            new Notification();

                    notification.setNotificationId(
                            resultSet.getInt("notification_id")
                    );

                    notification.setUserId(
                            resultSet.getInt("user_id")
                    );

                    notification.setMessage(
                            resultSet.getString("message")
                    );

                    notification.setNotificationType(
                            resultSet.getString("notification_type")
                    );

                    notification.setRead(
                            resultSet.getBoolean("is_read")
                    );

                    Timestamp timestamp =
                            resultSet.getTimestamp("created_at");

                    if (timestamp != null) {
                        notification.setCreatedAt(
                                timestamp.toLocalDateTime()
                        );
                    }

                    notifications.add(notification);
                }
            }
        }

        return notifications;
    }

    public void markAsRead(int notificationId)
            throws SQLException {

        String sql = """
                UPDATE notifications
                SET is_read = 1
                WHERE notification_id = ?
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, notificationId);

            statement.executeUpdate();
        }
    }

    public void markAllAsRead(int userId)
            throws SQLException {

        String sql = """
                UPDATE notifications
                SET is_read = 1
                WHERE user_id = ?
                AND is_read = 0
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            statement.executeUpdate();
        }
    }

    public int countUnread(int userId)
            throws SQLException {

        String sql = """
                SELECT COUNT(*)
                FROM notifications
                WHERE user_id = ?
                AND is_read = 0
                """;

        try (Connection connection =
                     DatabaseConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, userId);

            try (ResultSet resultSet =
                         statement.executeQuery()) {

                if (resultSet.next()) {
                    return resultSet.getInt(1);
                }
            }
        }

        return 0;
    }
}