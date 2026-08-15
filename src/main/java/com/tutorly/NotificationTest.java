package com.tutorly;

import com.tutorly.service.NotificationService;

public class NotificationTest {

    public static void main(String[] args) {

        NotificationService notificationService =
                new NotificationService();

        notificationService.sendNotification(
                1,
                "You have received a new booking request.",
                "Booking"
        );

        System.out.println("Notification test completed.");
    }
}