package com.tutorly.service;

import com.tutorly.database.DatabaseConnection;
import com.tutorly.model.User;
import com.tutorly.patterns.factory.UserFactory;
import com.tutorly.repository.StudentRepository;
import com.tutorly.repository.TutorRepository;
import com.tutorly.repository.UserRepository;

import java.sql.Connection;
import java.sql.SQLException;

public class AuthService {

    private final UserRepository userRepository;
    private final StudentRepository studentRepository;
    private final TutorRepository tutorRepository;

    public AuthService() {
        this.userRepository = new UserRepository();
        this.studentRepository = new StudentRepository();
        this.tutorRepository = new TutorRepository();
    }

    public User register(
            String fullName,
            String email,
            String password,
            String phone,
            String role
    ) throws SQLException {

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

        try (Connection connection =
                     DatabaseConnection.getConnection()) {

            try {
                connection.setAutoCommit(false);

                userRepository.createUser(
                        connection,
                        user
                );

                if ("student".equalsIgnoreCase(role)) {

                    studentRepository.createProfile(
                            connection,
                            user.getUserId()
                    );

                } else if ("tutor".equalsIgnoreCase(role)) {

                    tutorRepository.createProfile(
                            connection,
                            user.getUserId()
                    );
                }

                connection.commit();

                return user;

            } catch (SQLException | RuntimeException e) {

                connection.rollback();
                throw e;

            } finally {
                connection.setAutoCommit(true);
            }
        }
    }

    public User login(
            String email,
            String password
    ) throws SQLException {

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException(
                    "Email is required."
            );
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException(
                    "Password is required."
            );
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
            String role
    ) {

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
