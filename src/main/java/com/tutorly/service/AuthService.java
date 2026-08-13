package com.tutorly.service;

import com.tutorly.model.User;
import com.tutorly.patterns.factory.UserFactory;
import com.tutorly.repository.UserRepository;

import java.sql.SQLException;

public class AuthService {

    private final UserRepository userRepository;

    public AuthService() {
        this.userRepository = new UserRepository();
    }

    public User register(
            String fullName,
            String email,
            String password,
            String phone,
            String role) throws SQLException {

        validateRegistration(
                fullName,
                email,
                password,
                role
        );

        if (userRepository.emailExists(email)) {
            throw new IllegalArgumentException(
                    "An account with this email already exists."
            );
        }

        User user = UserFactory.createUser(
                role,
                fullName,
                email,
                password,
                phone
        );

        userRepository.createUser(user);

        return user;
    }

    public User login(
            String email,
            String password) throws SQLException {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required.");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required.");
        }

        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new IllegalArgumentException(
                    "Invalid email or password."
            );
        }

        if (!user.getPassword().equals(password)) {
            throw new IllegalArgumentException(
                    "Invalid email or password."
            );
        }

        return user;
    }

    private void validateRegistration(
            String fullName,
            String email,
            String password,
            String role) {

        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException(
                    "Full name is required."
            );
        }

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email is required."
            );
        }

        if (password == null || password.length() < 6) {
            throw new IllegalArgumentException(
                    "Password must contain at least 6 characters."
            );
        }

        if (!"student".equalsIgnoreCase(role)
                && !"tutor".equalsIgnoreCase(role)) {

            throw new IllegalArgumentException(
                    "Invalid account type."
            );
        }
    }
}
