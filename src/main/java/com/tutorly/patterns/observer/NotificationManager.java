package com.tutorly.patterns.observer;

import java.util.ArrayList;
import java.util.List;

public class NotificationManager implements Subject {

    private final List<Observer> observers = new ArrayList<>();

    @Override
    public void addObserver(Observer observer) {

        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    @Override
    public void removeObserver(Observer observer) {

        observers.remove(observer);
    }

    @Override
    public void notifyObservers(
            int userId,
            String message,
            String notificationType
    ) {

        for (Observer observer : observers) {

            observer.update(
                    userId,
                    message,
                    notificationType
            );
        }
    }
}