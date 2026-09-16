package com.tutorly.controller.tutor;

import com.tutorly.model.Tutor;
import com.tutorly.model.User;
import com.tutorly.patterns.decorator.TutorProfile;
import com.tutorly.service.BookingService;
import com.tutorly.service.NotificationService;
import com.tutorly.service.TutorProfileDecoratorService;
import com.tutorly.service.TutorService;
import com.tutorly.util.Navigator;
import com.tutorly.util.Session;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.SQLException;

public class TutorDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label profileStatusLabel;
    @FXML private Label bookingSummaryLabel;
    @FXML private Label notificationSummaryLabel;

    private final TutorService tutorService = new TutorService();
    private final BookingService bookingService = new BookingService();
    private final NotificationService notificationService = new NotificationService();
    private final TutorProfileDecoratorService decoratorService = new TutorProfileDecoratorService();

    @FXML
    private void initialize() {
        User user = Session.getCurrentUser();
        if (user == null || !"tutor".equalsIgnoreCase(user.getRole())) {
            Navigator.navigate("/fxml/login.fxml");
            return;
        }

        welcomeLabel.setText("Welcome, " + user.getFullName());
        loadTutorProfile(user.getUserId());
    }

    private void loadTutorProfile(int userId) {
        try {
            Tutor tutor = tutorService.getTutorProfile(userId);
            if (tutor == null) {
                profileStatusLabel.setText("Tutor profile not found.");
                return;
            }

            TutorProfile profile = decoratorService.buildProfile(tutor);
            profileStatusLabel.setText(profile.getProfile());
            loadBookingSummary(tutor.getTutorId());
            loadNotificationSummary(userId);
        } catch (SQLException e) {
            e.printStackTrace();
            profileStatusLabel.setText("Unable to load tutor profile.");
        }
    }

    private void loadBookingSummary(int tutorId) {
        try {
            var bookings = bookingService.getTutorBookingDetails(tutorId);
            long pending = bookings.stream().filter(b -> "Pending".equalsIgnoreCase(b.getStatus())).count();
            long accepted = bookings.stream().filter(b -> "Accepted".equalsIgnoreCase(b.getStatus())).count();
            bookingSummaryLabel.setText(
                    "Total requests: " + bookings.size() +
                    "  •  Pending: " + pending +
                    "  •  Accepted: " + accepted
            );
        } catch (SQLException e) {
            bookingSummaryLabel.setText("Unable to load booking summary.");
        }
    }

    private void loadNotificationSummary(int userId) {
        try {
            int unread = notificationService.getUnreadCount(userId);
            notificationSummaryLabel.setText(
                    unread == 0 ? "You're all caught up." : unread + " unread notification(s)."
            );
        } catch (SQLException e) {
            notificationSummaryLabel.setText("Notification status unavailable.");
        }
    }

    @FXML
    private void handleAvailability() {
        Navigator.navigate("/fxml/tutor/availability.fxml");
    }

    @FXML
    private void handleBookings() {
        Navigator.navigate("/fxml/tutor/booking-requests.fxml");
    }

    @FXML
    private void handleOnlineClasses() {
        Navigator.navigate("/fxml/tutor/online-class.fxml");
    }

    @FXML
    private void handleProfile() {
        Navigator.navigate("/fxml/tutor/profile-setup.fxml");
    }

    @FXML
    private void handleNotifications() {
        Navigator.navigate("/fxml/notifications.fxml");
    }

    @FXML
    private void handleLogout() {
        Session.logout();
        Navigator.navigate("/fxml/home.fxml");
    }
}
