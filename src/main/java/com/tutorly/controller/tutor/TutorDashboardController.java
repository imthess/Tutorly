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
    private Label qualificationsLabel;

    @FXML
    private Label experienceLabel;

    @FXML
    private Label hourlyRateLabel;

    @FXML
    private Label bioLabel;

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
            Navigator.navigate("/fxml/login.fxml");
            return;
        }

        welcomeLabel.setText(
                "Welcome back, " + user.getFullName()
        );

        loadTutorProfile(user.getUserId());
    }

    private void loadTutorProfile(int userId) {

        try {

            Tutor tutor =
                    tutorService.getTutorProfile(userId);

            if (tutor == null) {

                profileStatusLabel.setText(
                        "Profile not found"
                );

                return;
            }

            qualificationsLabel.setText(
                    valueOrDash(tutor.getQualifications())
            );

            experienceLabel.setText(
                    tutor.getExperience() + " years"
            );

            hourlyRateLabel.setText(
                    String.format(
                            "৳%.2f / hour",
                            tutor.getHourlyRate()
                    )
            );

            bioLabel.setText(
                    valueOrDash(tutor.getBio())
            );

            boolean complete =
                    tutorService.isProfileComplete(userId);

            profileStatusLabel.setText(
                    complete
                            ? "Profile complete"
                            : "Profile incomplete"
            );

            /*
             * Keep Decorator pattern actively integrated
             * into the tutor dashboard.
             */
            TutorProfile profile =
                    decoratorService.buildProfile(tutor);

            System.out.println(
                    "Tutor profile: "
                            + profile.getProfile()
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
    private void handleManageSubjects() {

        System.out.println(
                "Manage Subjects selected."
        );

        // Subject management module will be connected here.
    }

    @FXML
    private void handleAvailability() {

        System.out.println(
                "Manage Availability selected."
        );

        // Availability module will be connected here.
    }

    @FXML
    private void handleBookings() {

        System.out.println(
                "Booking Requests selected."
        );

        // Booking module will be connected here.
    }

    @FXML
    private void handleOnlineClasses() {

        Navigator.navigate(
                "/fxml/tutor/online-class.fxml"
        );
    }

    @FXML
    private void handleLearningMaterials() {

        System.out.println(
                "Learning Materials selected."
        );

        // Learning-material module will be connected here.
    }

    @FXML
    private void handleEditProfile() {

        Navigator.navigate(
                "/fxml/tutor/profile-setup.fxml"
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
