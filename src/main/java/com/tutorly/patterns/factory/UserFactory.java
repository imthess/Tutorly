package com.tutorly.patterns.factory;

import com.tutorly.model.Student;
import com.tutorly.model.Tutor;
import com.tutorly.model.User;

public final class UserFactory {

    private UserFactory() {
    }

    public static User createUser(
            String role,
            String fullName,
            String email,
            String password,
            String phone) {

        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null.");
        }

        return switch (role.toLowerCase()) {

            case "student" ->
                    new Student(fullName, email, password, phone);

            case "tutor" ->
                    new Tutor(fullName, email, password, phone);

            case "admin" ->
                    createAdmin(fullName, email, password, phone);

            default ->
                    throw new IllegalArgumentException(
                            "Unsupported user role: " + role
                    );
        };
    }

    private static User createAdmin(
            String fullName,
            String email,
            String password,
            String phone) {

        User admin = new User(
                fullName,
                email,
                password,
                phone,
                "admin"
        );

        return admin;
    }
}
