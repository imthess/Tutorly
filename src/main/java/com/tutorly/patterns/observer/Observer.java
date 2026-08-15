package com.tutorly.patterns.observer;

public interface Observer {

    void update(
            int userId,
            String message,
            String notificationType
    );
}