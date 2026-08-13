package com.tutorly.controller;

import com.tutorly.model.User;
import com.tutorly.service.AuthService;
import com.tutorly.util.Navigator;
import com.tutorly.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class LoginController {

    @FXML
    private TextField emailField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label messageLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void handleLogin() {

        String email = emailField.getText().trim();
        String password = passwordField.getText();

        try {

            User user = authService.login(email, password);

            Session.login(user);

            navigateByRole(user.getRole());

        } catch (SQLException | IllegalArgumentException e) {

            showMessage(e.getMessage());
        }
    }

    private void navigateByRole(String role) {

        switch (role.toLowerCase()) {

            case "student" ->
                    Navigator.navigate(
                            "/fxml/student/dashboard.fxml"
                    );

            case "tutor" ->
                    Navigator.navigate(
                            "/fxml/tutor/dashboard.fxml"
                    );

            case "admin" ->
                    Navigator.navigate(
                            "/fxml/admin/dashboard.fxml"
                    );

            default ->
                    showMessage("Unknown user role.");
        }
    }

    @FXML
    private void handleSignup() {
        Navigator.navigate("/fxml/signup.fxml");
    }

    @FXML
    private void handleBack() {
        Navigator.navigate("/fxml/home.fxml");
    }

    private void showMessage(String message) {
        messageLabel.setText(message);
    }
}
