package com.tutorly.controller.student;

import com.tutorly.model.Student;
import com.tutorly.model.User;
import com.tutorly.service.StudentService;
import com.tutorly.util.Navigator;
import com.tutorly.util.Session;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class StudentProfileSetupController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private TextField educationField;

    @FXML
    private TextField instituteField;

    @FXML
    private Label messageLabel;

    private final StudentService studentService =
            new StudentService();

    @FXML
    private void initialize() {

        User user = Session.getCurrentUser();

        if (user == null) {
            Navigator.navigate("/fxml/login.fxml");
            return;
        }

        welcomeLabel.setText(
                "Welcome, " + user.getFullName()
        );
    }


    @FXML
    private void handleSaveProfile() {

        User user = Session.getCurrentUser();

        if (user == null) {
            Navigator.navigate("/fxml/login.fxml");
            return;
        }

        String education =
                educationField.getText().trim();

        String institute =
                instituteField.getText().trim();

        try {

            studentService.updateStudentProfile(
                    user.getUserId(),
                    education,
                    institute
            );

            Navigator.navigate(
                    "/fxml/student/dashboard.fxml"
            );

        } catch (SQLException |
                 IllegalArgumentException e) {

            messageLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void handleLogout() {

        Session.logout();

        Navigator.navigate("/fxml/home.fxml");
    }
}