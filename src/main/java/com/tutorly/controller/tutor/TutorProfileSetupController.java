package com.tutorly.controller.tutor;

import com.tutorly.service.TutorService;
import com.tutorly.util.Navigator;
import com.tutorly.util.Session;
import com.tutorly.model.User;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.SQLException;

public class TutorProfileSetupController {

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

    @FXML
    private void initialize() {

        User user = Session.getCurrentUser();

        if (user == null) {
            Navigator.navigate("/fxml/login.fxml");
        }
    }

    @FXML
    private void handleSaveProfile() {

        User user = Session.getCurrentUser();

        if (user == null) {
            Navigator.navigate("/fxml/login.fxml");
            return;
        }

        String qualifications =
                qualificationsField.getText().trim();

        String experienceText =
                experienceField.getText().trim();

        String hourlyRateText =
                hourlyRateField.getText().trim();

        String bio =
                bioArea.getText().trim();

        int experience;

        double hourlyRate;


        try {
            experience = Integer.parseInt(experienceText);
        } catch (NumberFormatException e) {
            messageLabel.setText(
                    "Experience must be a valid number."
            );
            return;
        }

        try {
            hourlyRate = Double.parseDouble(hourlyRateText);
        } catch (NumberFormatException e) {
            messageLabel.setText(
                    "Hourly rate must be a valid number."
            );
            return;
        }


        try {

            tutorService.updateTutorProfile(
                    user.getUserId(),
                    qualifications,
                    experience,
                    hourlyRate,
                    bio
            );

            Navigator.navigate(
                    "/fxml/tutor/dashboard.fxml"
            );

        } catch (NumberFormatException e) {

            messageLabel.setText(
                    "Experience and hourly rate must be valid numbers."
            );

        } catch (SQLException |
                 IllegalArgumentException e) {

            messageLabel.setText(
                    e.getMessage()
            );
        }
    }

    @FXML
    private void handleLogout() {

        Session.logout();

        Navigator.navigate(
                "/fxml/home.fxml"
        );
    }
}