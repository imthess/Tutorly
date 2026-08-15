package com.tutorly.service;

import java.sql.SQLException;
import java.util.List;

import com.tutorly.model.Notification;
import com.tutorly.patterns.observer.NotificationManager;
import com.tutorly.patterns.observer.NotificationObserver;
import com.tutorly.repository.NotificationRepository;

public class NotificationService {

    private final NotificationRepository notificationRepository;

    private final NotificationManager notificationManager;

    public NotificationService() {

        this.notificationRepository =
                new NotificationRepository();

        this.notificationManager =
                new NotificationManager();

        /*
         * Register the database notification observer.
         */
        this.notificationManager.addObserver(
                new NotificationObserver()
        );
    }

    public void sendNotification(
            int userId,
            String message,
            String notificationType
    ) {

        validateNotificationType(notificationType);

        notificationManager.notifyObservers(
                userId,
                message,
                notificationType
        );
    }

    public List<Notification> getUserNotifications(
            int userId
    ) throws SQLException {

        return notificationRepository.findByUserId(userId);
    }

    public void markAsRead(
            int notificationId
    ) throws SQLException {

        notificationRepository.markAsRead(
                notificationId
        );
    }

    public void markAllAsRead(
            int userId
    ) throws SQLException {

        notificationRepository.markAllAsRead(
                userId
        );
    }

    public int getUnreadCount(
            int userId
    ) throws SQLException {

        return notificationRepository.countUnread(
                userId
        );
    }

    private void validateNotificationType(
            String notificationType
    ) {

        if (notificationType == null
                || notificationType.isBlank()) {

            throw new IllegalArgumentException(
                    "Notification type is required."
            );
        }

        switch (notificationType) {

            case "Booking":
            case "Payment":
            case "Class":
            case "Result":
                break;

            default:
                throw new IllegalArgumentException(
                        "Invalid notification type: "
                                + notificationType
                );
        }
    }
}