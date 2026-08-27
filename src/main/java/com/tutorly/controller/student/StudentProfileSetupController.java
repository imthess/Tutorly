package com.tutorly.controller.student;

import com.tutorly.model.Student;
import com.tutorly.model.User;
import com.tutorly.service.StudentService;
import com.tutorly.util.Navigator;
import com.tutorly.util.Session;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.sql.SQLException;

public class StudentProfileSetupController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label titleLabel;

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

        User user =
                Session.getCurrentUser();

        if (user == null ||
                !"student".equalsIgnoreCase(
                        user.getRole()
                )) {

            Navigator.navigate("/fxml/login.fxml");
            return;
        }

        welcomeLabel.setText(
                "Welcome, " + user.getFullName()
        );

        loadExistingProfile(
                user.getUserId()
        );
    }

    private void loadExistingProfile(
            int userId
    ) {

        try {

            Student student =
                    studentService.getStudentProfile(
                            userId
                    );

            if (student == null) {
                return;
            }

            educationField.setText(
                    safe(student.getEducation())
            );

            instituteField.setText(
                    safe(student.getInstitute())
            );

            titleLabel.setText(
                    "Edit Student Profile"
            );

        } catch (SQLException e) {

            messageLabel.setText(
                    "Unable to load your profile."
            );
        }
    }

    private String safe(String value) {

        return value == null
                ? ""
                : value;
    }

    @FXML
    private void handleSaveProfile() {

        User user =
                Session.getCurrentUser();

        if (user == null) {
            Navigator.navigate("/fxml/login.fxml");
            return;
        }

        try {

            studentService.updateStudentProfile(
                    user.getUserId(),
                    educationField.getText().trim(),
                    instituteField.getText().trim()
            );

            messageLabel.setText(
                    "Profile saved successfully."
            );

        } catch (SQLException |
                 IllegalArgumentException e) {

            messageLabel.setText(
                    e.getMessage()
            );
        }
    }

    @FXML
    private void handleBack() {

        Navigator.navigate(
                "/fxml/student/dashboard.fxml"
        );
    }

    @FXML
    private void handleLogout() {

        Session.logout();

        Navigator.navigate(
                "/fxml/home.fxml"
        );
    }
}
