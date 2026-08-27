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
    private Label educationLabel;

    @FXML
    private Label instituteLabel;

    @FXML
    private Label profileStatusLabel;

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
                "Welcome back, " + user.getFullName()
        );

        loadStudentProfile(user.getUserId());
    }

    private void loadStudentProfile(int userId) {

        try {

            Student student =
                    studentService.getStudentProfile(userId);

            if (student == null) {

                profileStatusLabel.setText(
                        "Profile not found"
                );

                educationLabel.setText("-");
                instituteLabel.setText("-");

                return;
            }

            educationLabel.setText(
                    valueOrDash(student.getEducation())
            );

            instituteLabel.setText(
                    valueOrDash(student.getInstitute())
            );

            boolean complete =
                    studentService.isProfileComplete(userId);

            profileStatusLabel.setText(
                    complete
                            ? "Profile complete"
                            : "Profile incomplete"
            );

        } catch (SQLException e) {

            e.printStackTrace();

            profileStatusLabel.setText(
                    "Unable to load profile"
            );
        }
    }

    private String valueOrDash(String value) {

        return value == null || value.isBlank()
                ? "-"
                : value;
    }

    @FXML
    private void handleFindTutors() {

        System.out.println(
                "Find Tutors selected."
        );

        // Tutor search module will be connected here.
    }

    @FXML
    private void handleSearchSubjects() {

        System.out.println(
                "Search Subjects selected."
        );

        // Subject search module will be connected here.
    }

    @FXML
    private void handleBookings() {

        System.out.println(
                "My Bookings selected."
        );

        // Booking module will be connected here.
    }

    @FXML
    private void handleOnlineClasses() {

        System.out.println(
                "Online Classes selected."
        );

        // Student online-class access will be connected
        // to the booking/class module.
    }

    @FXML
    private void handleEditProfile() {

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
