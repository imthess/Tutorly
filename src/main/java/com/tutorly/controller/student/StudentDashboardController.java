package com.tutorly.controller.student;

import com.tutorly.model.Student;
import com.tutorly.model.User;
import com.tutorly.service.BookingService;
import com.tutorly.service.NotificationService;
import com.tutorly.service.StudentService;
import com.tutorly.util.Navigator;
import com.tutorly.util.Session;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

import java.sql.SQLException;

public class StudentDashboardController {

    @FXML private Label welcomeLabel;
    @FXML private Label profileSummaryLabel;
    @FXML private Label bookingSummaryLabel;
    @FXML private Label notificationSummaryLabel;

    private final StudentService studentService = new StudentService();
    private final BookingService bookingService = new BookingService();
    private final NotificationService notificationService = new NotificationService();

    @FXML
    private void initialize() {
        User user = Session.getCurrentUser();
        if (user == null || !"student".equalsIgnoreCase(user.getRole())) {
            Navigator.navigate("/fxml/login.fxml");
            return;
        }

        welcomeLabel.setText("Welcome, " + user.getFullName());
        loadProfile(user.getUserId());
        loadBookingSummary(user.getUserId());
        loadNotificationSummary(user.getUserId());
    }

    private void loadProfile(int userId) {
        try {
            Student student = studentService.getStudentProfile(userId);
            if (student == null) {
                profileSummaryLabel.setText("Profile information unavailable.");
                return;
            }
            profileSummaryLabel.setText(
                    "Education: " + safe(student.getEducation()) +
                    "\nInstitute: " + safe(student.getInstitute())
            );
        } catch (SQLException e) {
            profileSummaryLabel.setText("Unable to load profile information.");
        }
    }

    private void loadBookingSummary(int userId) {
        try {
            Student student = studentService.getStudentProfile(userId);
            if (student == null) {
                bookingSummaryLabel.setText("Booking information unavailable.");
                return;
            }
            var bookings = bookingService.getStudentBookingDetails(student.getStudentId());
            long pending = bookings.stream().filter(b -> "Pending".equalsIgnoreCase(b.getStatus())).count();
            long accepted = bookings.stream().filter(b -> "Accepted".equalsIgnoreCase(b.getStatus())).count();
            long completed = bookings.stream().filter(b -> "Completed".equalsIgnoreCase(b.getStatus())).count();
            bookingSummaryLabel.setText(
                    "Total: " + bookings.size() +
                    "  •  Pending: " + pending +
                    "  •  Accepted: " + accepted +
                    "  •  Completed: " + completed
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

    private String safe(String value) {
        return value == null || value.isBlank() ? "Not provided" : value;
    }

    @FXML private void handleFindTutors() { Navigator.navigate("/fxml/student/find-tutors.fxml"); }
    @FXML private void handleBookings() { Navigator.navigate("/fxml/student/bookings.fxml"); }
    @FXML private void handleProfile() { Navigator.navigate("/fxml/student/profile-setup.fxml"); }
    @FXML private void handleNotifications() { Navigator.navigate("/fxml/notifications.fxml"); }
    @FXML private void handleLogout() { Session.logout(); Navigator.navigate("/fxml/home.fxml"); }
}
