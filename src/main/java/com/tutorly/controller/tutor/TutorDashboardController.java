package com.tutorly.controller.tutor;

import com.tutorly.model.Tutor;
import com.tutorly.service.TutorService;
import com.tutorly.util.Navigator;
import com.tutorly.util.Session;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class TutorDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private TextField qualificationsField;

    @FXML
    private TextField experienceField;

    @FXML
    private TextField hourlyRateField;

    @FXML
    private TextArea bioArea;

    @FXML
    private Label messageLabel;

    private final TutorService tutorService =
            new TutorService();

    private Tutor tutor;

    @FXML
    private void initialize() {

        if (!Session.isLoggedIn()) {
            Navigator.navigate("/fxml/login.fxml");
            return;
        }

        if (!"tutor".equalsIgnoreCase(
                Session.getCurrentUser().getRole())) {

            Navigator.navigate("/fxml/home.fxml");
            return;
        }

        loadProfile();
    }

    private void loadProfile() {

        try {

            int userId =
                    Session.getCurrentUser().getUserId();

            tutor =
                    tutorService.getTutorProfile(userId);

            if (tutor == null) {

                showMessage(
                        "Tutor profile not found."
                );

                return;
            }

            welcomeLabel.setText(
                    "Welcome, " + tutor.getFullName()
            );

            qualificationsField.setText(
                    tutor.getQualifications() == null
                            ? ""
                            : tutor.getQualifications()
            );

            experienceField.setText(
                    String.valueOf(
                            tutor.getExperience()
                    )
            );

            hourlyRateField.setText(
                    String.valueOf(
                            tutor.getHourlyRate()
                    )
            );

            bioArea.setText(
                    tutor.getBio() == null
                            ? ""
                            : tutor.getBio()
            );

        } catch (SQLException e) {

            e.printStackTrace();

            showMessage(
                    "Could not load tutor profile."
            );
        }
    }

    @FXML
    private void handleSaveProfile() {

        System.out.println("SAVE PROFILE BUTTON CLICKED");

        try {

            tutorService.updateTutorProfile(
                    Session.getCurrentUser().getUserId(),
                    qualificationsField.getText(),
                    experienceField.getText(),
                    hourlyRateField.getText(),
                    bioArea.getText()
            );

            showMessage(
                    "Profile updated successfully."
            );

            loadProfile();

        } catch (IllegalArgumentException e) {

            showMessage(e.getMessage());

        } catch (SQLException e) {

            e.printStackTrace();

            showMessage(
                    "Could not update profile."
            );
        }
    }

    @FXML
    private void handleLogout() {

        Session.logout();

        Navigator.navigate("/fxml/home.fxml");
    }

    private void showMessage(String message) {

        messageLabel.setText(message);
    }
}
