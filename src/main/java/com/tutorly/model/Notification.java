package com.tutorly.model;

import java.time.LocalDateTime;

public class Notification {

    private int notificationId;
    private int userId;
    private String message;
    private String notificationType;
    private boolean read;
    private LocalDateTime createdAt;

    public Notification() {
    }

    public Notification(
            int notificationId,
            int userId,
            String message,
            String notificationType,
            boolean read,
            LocalDateTime createdAt
    ) {

        this.notificationId = notificationId;
        this.userId = userId;
        this.message = message;
        this.notificationType = notificationType;
        this.read = read;
        this.createdAt = createdAt;
    }

    public int getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(int notificationId) {
        this.notificationId = notificationId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}