package com.tutorly.controller.tutor;

import com.tutorly.model.Tutor;
import com.tutorly.model.User;
import com.tutorly.patterns.decorator.TutorProfile;
import com.tutorly.service.TutorProfileDecoratorService;
import com.tutorly.service.TutorService;
import com.tutorly.util.Navigator;
import com.tutorly.util.Session;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.SQLException;

public class TutorDashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    private Label profileStatusLabel;

    private final TutorService tutorService =
            new TutorService();

    private final TutorProfileDecoratorService
            decoratorService =
            new TutorProfileDecoratorService();

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

        loadTutorProfile(user.getUserId());
    }

    private void loadTutorProfile(int userId) {

        try {

            Tutor tutor =
                    tutorService.getTutorProfile(userId);

            if (tutor == null) {

                profileStatusLabel.setText(
                        "Tutor profile not found."
                );

                return;
            }

            /*
             * Build the decorated profile through
             * the dedicated service.
             */
            TutorProfile profile =
                    decoratorService.buildProfile(tutor);

            profileStatusLabel.setText(
                    profile.getProfile()
            );

        } catch (SQLException e) {

            e.printStackTrace();

            profileStatusLabel.setText(
                    "Failed to load tutor profile."
            );
        }
    }

    @FXML
    private void handleOnlineClasses() {

        Navigator.navigate(
                "/fxml/tutor/online-class.fxml"
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
