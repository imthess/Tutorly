package com.tutorly.controller;

import com.tutorly.model.User;
import com.tutorly.service.AuthService;
import com.tutorly.util.Navigator;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class SignupController {

    @FXML
    private TextField fullNameField;

    @FXML
    private TextField emailField;

    @FXML
    private TextField phoneField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private PasswordField confirmPasswordField;

    @FXML
    private ComboBox<String> roleComboBox;

    @FXML
    private Label messageLabel;

    private final AuthService authService = new AuthService();

    @FXML
    private void initialize() {
        roleComboBox.getItems().addAll(
                "Student",
                "Tutor"
        );
    }

    @FXML
    private void handleSignup() {

        String fullName = fullNameField.getText().trim();
        String email = emailField.getText().trim();
        String phone = phoneField.getText().trim();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        String selectedRole = roleComboBox.getValue();

        if (!password.equals(confirmPassword)) {
            showMessage("Passwords do not match.");
            return;
        }

        if (selectedRole == null) {
            showMessage("Please select an account type.");
            return;
        }

        String role = selectedRole.toLowerCase();

        try {

            User user = authService.register(
                    fullName,
                    email,
                    password,
                    phone,
                    role
            );

            showMessage(
                    "Account created successfully. Please log in."
            );

            clearFields();

        } catch (SQLException | IllegalArgumentException e) {

            showMessage(e.getMessage());
        }
    }

    @FXML
    private void handleLogin() {
        Navigator.navigate("/fxml/login.fxml");
    }

    @FXML
    private void handleBack() {
        Navigator.navigate("/fxml/home.fxml");
    }

    private void showMessage(String message) {
        messageLabel.setText(message);
    }

    private void clearFields() {
        fullNameField.clear();
        emailField.clear();
        phoneField.clear();
        passwordField.clear();
        confirmPasswordField.clear();
        roleComboBox.setValue(null);
    }
}
