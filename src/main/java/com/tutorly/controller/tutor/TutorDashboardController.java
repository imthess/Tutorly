package com.tutorly.controller.tutor;

import com.tutorly.model.User;
import com.tutorly.util.Navigator;
import com.tutorly.util.Session;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TutorDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private void initialize() {

        User user = Session.getCurrentUser();

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
    private void handleLogout() {

        Session.logout();

        Navigator.navigate(
                "/fxml/home.fxml"
        );
    }
}