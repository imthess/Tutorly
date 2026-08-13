package com.tutorly.controller.tutor;

import com.tutorly.util.Navigator;
import com.tutorly.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class TutorDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private void initialize() {

        if (Session.getCurrentUser() != null) {
            welcomeLabel.setText(
                    "Welcome, " +
                    Session.getCurrentUser().getFullName()
            );
        }
    }

    @FXML
    private void handleLogout() {

        Session.logout();

        Navigator.navigate("/fxml/home.fxml");
    }
}
