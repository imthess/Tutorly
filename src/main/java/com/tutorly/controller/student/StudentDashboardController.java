package com.tutorly.controller.student;

import com.tutorly.model.Student;
import com.tutorly.model.User;
import com.tutorly.service.StudentService;
import com.tutorly.util.Navigator;
import com.tutorly.util.Session;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.SQLException;

public class StudentDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label profileSummaryLabel;

    @FXML
    private Label bookingSummaryLabel;

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

        loadProfile(user.getUserId());
    }

    private void loadProfile(int userId) {

        try {

            Student student =
                    studentService.getStudentProfile(
                            userId
                    );

            if (student == null) {

                profileSummaryLabel.setText(
                        "Profile information unavailable."
                );

                return;
            }

            profileSummaryLabel.setText(
                    "Education: "
                            + safe(student.getEducation())
                            + "\nInstitute: "
                            + safe(student.getInstitute())
            );

        } catch (SQLException e) {

            profileSummaryLabel.setText(
                    "Unable to load profile information."
            );
        }
    }

    private String safe(String value) {

        return value == null ||
                value.isBlank()
                ? "Not provided"
                : value;
    }

    @FXML
    private void handleFindTutors() {

        Navigator.navigate(
                "/fxml/student/find-tutors.fxml"
        );
    }

    @FXML
    private void handleProfile() {

        Navigator.navigate(
                "/fxml/student/profile-setup.fxml"
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
