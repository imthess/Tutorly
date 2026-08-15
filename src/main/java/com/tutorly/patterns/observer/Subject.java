package com.tutorly.patterns.observer;

public interface Subject {

    void addObserver(Observer observer);

    void removeObserver(Observer observer);

    void notifyObservers(
            int userId,
            String message,
            String notificationType
    );
}