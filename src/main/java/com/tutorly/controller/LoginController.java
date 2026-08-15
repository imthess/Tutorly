package com.tutorly.controller;

import com.tutorly.model.User;
import com.tutorly.service.AuthService;
import com.tutorly.service.StudentService;
import com.tutorly.service.TutorService;
import com.tutorly.util.Navigator;
import com.tutorly.util.Session;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class LoginController {

    private final AuthService authService =
            new AuthService();

    private final StudentService studentService =
            new StudentService();

    private final TutorService tutorService =
            new TutorService();

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    @FXML
    private void handleLogin() {

        System.out.println("=================================");
        System.out.println("LOGIN BUTTON CLICKED");

        String email = emailField.getText().trim();
        String password = passwordField.getText();

        System.out.println("Email: " + email);

        try {

            User user = authService.login(
                    email,
                    password
            );

            System.out.println(
                    "Authentication successful"
            );

            System.out.println(
                    "User ID: " + user.getUserId()
            );

            System.out.println(
                    "User role: " + user.getRole()
            );

            Session.login(user);

            System.out.println(
                    "Session created"
            );

            navigateByRole(user);

        } catch (SQLException e) {

            System.err.println(
                    "SQL ERROR DURING LOGIN/PROFILE CHECK"
            );

            e.printStackTrace();

            showMessage(
                    "Database error: " + e.getMessage()
            );

        } catch (IllegalArgumentException e) {

            System.err.println(
                    "VALIDATION ERROR"
            );

            e.printStackTrace();

            showMessage(
                    e.getMessage()
            );

        } catch (RuntimeException e) {

            System.err.println(
                    "RUNTIME ERROR DURING LOGIN"
            );

            e.printStackTrace();

            showMessage(
                    "Login error: " + e.getMessage()
            );
        }
    }

    private void navigateByRole(User user)
            throws SQLException {

        System.out.println(
                "Checking role..."
        );

        String role = user.getRole();

        if (role == null || role.isBlank()) {

            System.err.println(
                    "ERROR: User role is null/blank"
            );

            showMessage(
                    "User role is missing."
            );

            return;
        }

        System.out.println(
                "Role = " + role
        );

        switch (role.toLowerCase()) {

            case "student" -> {

                System.out.println(
                        "STUDENT LOGIN DETECTED"
                );

                boolean complete =
                        studentService.isProfileComplete(
                                user.getUserId()
                        );

                System.out.println(
                        "Student profile complete = "
                                + complete
                );

                if (complete) {

                    System.out.println(
                            "Navigating to STUDENT DASHBOARD"
                    );

                    Navigator.navigate(
                            "/fxml/student/dashboard.fxml"
                    );

                } else {

                    System.out.println(
                            "Navigating to STUDENT PROFILE SETUP"
                    );

                    Navigator.navigate(
                            "/fxml/student/profile-setup.fxml"
                    );
                }
            }

            case "tutor" -> {

                System.out.println(
                        "TUTOR LOGIN DETECTED"
                );

                boolean complete =
                        tutorService.isProfileComplete(
                                user.getUserId()
                        );

                System.out.println(
                        "Tutor profile complete = "
                                + complete
                );

                if (complete) {

                    System.out.println(
                            "Navigating to TUTOR DASHBOARD"
                    );

                    Navigator.navigate(
                            "/fxml/tutor/dashboard.fxml"
                    );

                } else {

                    System.out.println(
                            "Navigating to TUTOR PROFILE SETUP"
                    );

                    Navigator.navigate(
                            "/fxml/tutor/profile-setup.fxml"
                    );
                }
            }

            case "admin" -> {

                System.out.println(
                        "Navigating to ADMIN DASHBOARD"
                );

                Navigator.navigate(
                        "/fxml/admin/dashboard.fxml"
                );
            }

            default -> {

                System.err.println(
                        "Unknown role: " + role
                );

                showMessage(
                        "Unknown user role: " + role
                );
            }
        }
    }

    @FXML
    private void handleSignup() {

        Navigator.navigate(
                "/fxml/signup.fxml"
        );
    }

    @FXML
    private void handleBack() {

        Navigator.navigate(
                "/fxml/home.fxml"
        );
    }

    private void showMessage(String message) {

        if (message == null || message.isBlank()) {

            messageLabel.setText(
                    "Login failed."
            );

        } else {

            messageLabel.setText(message);
        }
    }
}
