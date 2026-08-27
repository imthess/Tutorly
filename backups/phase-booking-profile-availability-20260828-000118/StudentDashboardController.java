package com.tutorly.controller.student;

import com.tutorly.model.User;
import com.tutorly.util.Navigator;
import com.tutorly.util.Session;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class StudentDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private void initialize() {

        User user =
                Session.getCurrentUser();

        if (user == null) {

            Navigator.navigate(
                    "/fxml/login.fxml"
            );

            return;
        }

        welcomeLabel.setText(
                "Welcome, " + user.getFullName()
        );
    }

    @FXML
    private void handleFindTutors() {

        Navigator.navigate(
                "/fxml/student/find-tutors.fxml"
        );
    }

    @FXML
    private void handleBookings() {

        Navigator.navigate(
                "/fxml/student/bookings.fxml"
        );
    }

    @FXML
    private void handleOnlineClasses() {

        Navigator.navigate(
                "/fxml/tutor/online-class.fxml"
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
