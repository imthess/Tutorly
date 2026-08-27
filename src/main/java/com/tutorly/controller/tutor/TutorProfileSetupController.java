package com.tutorly.controller.tutor;

import com.tutorly.model.Tutor;
import com.tutorly.model.User;
import com.tutorly.service.TutorService;
import com.tutorly.util.Navigator;
import com.tutorly.util.Session;

import javafx.fxml.FXML;
import javafx.scene.control.*;

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

    @FXML
    private Label titleLabel;

    private final TutorService tutorService =
            new TutorService();

    @FXML
    private void initialize() {

        User user =
                Session.getCurrentUser();

        if (user == null ||
                !"tutor".equalsIgnoreCase(
                        user.getRole()
                )) {

            Navigator.navigate("/fxml/login.fxml");
            return;
        }

        loadExistingProfile(
                user.getUserId()
        );
    }

    private void loadExistingProfile(
            int userId
    ) {

        try {

            Tutor tutor =
                    tutorService.getTutorProfile(
                            userId
                    );

            if (tutor == null) {
                return;
            }

            qualificationsField.setText(
                    safe(tutor.getQualifications())
            );

            experienceField.setText(
                    tutor.getExperience() > 0
                            ? String.valueOf(
                                    tutor.getExperience()
                            )
                            : ""
            );

            hourlyRateField.setText(
                    tutor.getHourlyRate() > 0
                            ? String.valueOf(
                                    tutor.getHourlyRate()
                            )
                            : ""
            );

            bioArea.setText(
                    safe(tutor.getBio())
            );

            titleLabel.setText(
                    "Edit Tutor Profile"
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

            String qualifications =
                    qualificationsField.getText().trim();

            int experience =
                    Integer.parseInt(
                            experienceField.getText().trim()
                    );

            double hourlyRate =
                    Double.parseDouble(
                            hourlyRateField.getText().trim()
                    );

            String bio =
                    bioArea.getText().trim();

            tutorService.updateTutorProfile(
                    user.getUserId(),
                    qualifications,
                    experience,
                    hourlyRate,
                    bio
            );

            messageLabel.setText(
                    "Profile saved successfully."
            );

        } catch (NumberFormatException e) {

            messageLabel.setText(
                    "Experience and hourly rate must be valid numbers."
            );

        } catch (IllegalArgumentException |
                 SQLException e) {

            messageLabel.setText(
                    e.getMessage()
            );
        }
    }

    @FXML
    private void handleBack() {

        Navigator.navigate(
                "/fxml/tutor/dashboard.fxml"
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
