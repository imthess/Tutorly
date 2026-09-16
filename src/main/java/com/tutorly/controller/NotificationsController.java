package com.tutorly.controller;

import com.tutorly.model.BookingDetails;
import com.tutorly.model.Notification;
import com.tutorly.model.OnlineClass;
import com.tutorly.model.Student;
import com.tutorly.model.User;
import com.tutorly.patterns.facade.LiveClassFacade;
import com.tutorly.service.BookingService;
import com.tutorly.service.OnlineClassService;
import com.tutorly.service.NotificationService;
import com.tutorly.service.StudentService;
import com.tutorly.util.Navigator;
import com.tutorly.util.Session;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class NotificationsController {

    @FXML private TableView<Notification> notificationTable;
    @FXML private TableColumn<Notification, String> typeColumn;
    @FXML private TableColumn<Notification, String> messageColumn;
    @FXML private TableColumn<Notification, LocalDateTime> dateColumn;
    @FXML private TableColumn<Notification, Boolean> readColumn;
    @FXML private Label unreadLabel;
    @FXML private Label messageLabel;

    private final NotificationService notificationService = new NotificationService();
    private final BookingService bookingService = new BookingService();
    private final OnlineClassService onlineClassService = new OnlineClassService();
    private final StudentService studentService = new StudentService();
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, h:mm a");

    @FXML
    private void initialize() {
        User user = Session.getCurrentUser();
        if (user == null || user.getUserId() <= 0) {
            Navigator.navigate("/fxml/login.fxml");
            return;
        }

        typeColumn.setCellValueFactory(new PropertyValueFactory<>("notificationType"));
        messageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("createdAt"));
        readColumn.setCellValueFactory(new PropertyValueFactory<>("read"));

        dateColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDateTime value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? "" : value.format(DATE_FORMAT));
            }
        });

        readColumn.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Boolean value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty ? "" : Boolean.TRUE.equals(value) ? "Read" : "New");
            }
        });

        notificationTable.setRowFactory(table -> {
            TableRow<Notification> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    openNotification(row.getItem());
                }
            });
            return row;
        });

        loadNotifications();
    }

    private void loadNotifications() {
        try {
            int userId = Session.getCurrentUser().getUserId();
            notificationTable.setItems(FXCollections.observableArrayList(
                    notificationService.getUserNotifications(userId)
            ));
            unreadLabel.setText(notificationService.getUnreadCount(userId) + " unread notification(s)");
            messageLabel.setText(notificationTable.getItems().isEmpty()
                    ? "You have no notifications yet."
                    : "Click a notification to open it. A started online-class notification can be joined directly.");
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Unable to load notifications.");
        }
    }

    private void markSelectedAsRead(Notification notification) {
        if (notification == null || notification.isRead()) return;
        try {
            notificationService.markAsRead(notification.getNotificationId());
            loadNotifications();
        } catch (SQLException e) {
            messageLabel.setText("Unable to update notification status.");
        }
    }


    private void openNotification(Notification notification) {
        if (notification == null) return;

        if ("Class".equalsIgnoreCase(notification.getNotificationType())) {
            showClassNotification(notification);
            return;
        }

        markSelectedAsRead(notification);
    }

    private void showClassNotification(Notification notification) {
        try {
            User user = Session.getCurrentUser();
            if (user == null || !"student".equalsIgnoreCase(user.getRole())) {
                markSelectedAsRead(notification);
                return;
            }

            OnlineClassMatch match = findRunningClassForNotification(user.getUserId(), notification.getMessage());

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Tutorly Live Class");
            alert.setHeaderText("Online Class Started");
            alert.setContentText(notification.getMessage());

            ButtonType joinButton = new ButtonType("Join Online Class", ButtonBar.ButtonData.OK_DONE);
            ButtonType closeButton = new ButtonType("Close", ButtonBar.ButtonData.CANCEL_CLOSE);
            alert.getButtonTypes().setAll(joinButton, closeButton);

            if (match == null) {
                alert.setContentText(notification.getMessage() + "\n\nThe live classroom is not currently available. Please refresh and try again if the tutor is still teaching.");
            }

            ButtonType result = alert.showAndWait().orElse(closeButton);
            markSelectedAsRead(notification);

            if (result == joinButton && match != null) {
                joinLiveClass(match.onlineClass.getMeetingLink());
            }
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to open the online-class notification.");
        }
    }

    private OnlineClassMatch findRunningClassForNotification(int userId, String message) throws SQLException {
        Student student = studentService.getStudentProfile(userId);
        if (student == null) return null;
        java.util.List<BookingDetails> bookings = bookingService.getStudentBookingDetails(student.getStudentId());
        OnlineClassMatch fallback = null;

        for (BookingDetails booking : bookings) {
            if (!"Accepted".equalsIgnoreCase(booking.getStatus())) continue;

            OnlineClass onlineClass = onlineClassService.findByBookingId(booking.getBookingId());
            if (!isRunning(onlineClass)) continue;

            OnlineClassMatch candidate = new OnlineClassMatch(booking, onlineClass);
            if (fallback == null) fallback = candidate;

            String subject = booking.getSubjectName();
            String date = booking.getBookingDate();
            String time = booking.getBookingTime();
            if (containsIgnoreCase(message, subject)
                    && containsDateOrTime(message, date)
                    && containsDateOrTime(message, time)) {
                return candidate;
            }
        }

        return fallback;
    }

    private boolean isRunning(OnlineClass onlineClass) {
        return onlineClass != null
                && "Scheduled".equalsIgnoreCase(onlineClass.getStatus())
                && onlineClass.getStartTime() != null
                && onlineClass.getEndTime() == null
                && onlineClass.getMeetingLink() != null
                && !onlineClass.getMeetingLink().isBlank();
    }

    private boolean containsIgnoreCase(String text, String value) {
        return text != null && value != null && !value.isBlank()
                && text.toLowerCase().contains(value.toLowerCase());
    }

    private boolean containsDateOrTime(String message, String value) {
        if (value == null || value.isBlank()) return true;
        if (containsIgnoreCase(message, value)) return true;

        // BookingDetails may contain formatted values such as 08:00:00 while
        // the class notification uses 08:00. Compare the first five characters.
        if (value.length() >= 5 && containsIgnoreCase(message, value.substring(0, 5))) return true;

        try {
            java.time.LocalDate date = java.time.LocalDate.parse(value);
            return message != null && message.contains(date.toString());
        } catch (Exception ignored) {
            return false;
        }
    }

    private void joinLiveClass(String meetingLink) {
        try {
            if (meetingLink == null || meetingLink.isBlank()) {
                messageLabel.setText("The online classroom address is unavailable.");
                return;
            }

            LiveClassFacade facade = new LiveClassFacade(Session.getCurrentUser(), meetingLink);
            facade.joinClass();
            messageLabel.setText("Joining the live classroom...");
        } catch (Exception e) {
            e.printStackTrace();
            messageLabel.setText("Unable to join the live classroom: " + e.getMessage());
        }
    }

    private static final class OnlineClassMatch {
        private final BookingDetails booking;
        private final OnlineClass onlineClass;

        private OnlineClassMatch(BookingDetails booking, OnlineClass onlineClass) {
            this.booking = booking;
            this.onlineClass = onlineClass;
        }
    }

    @FXML
    private void handleMarkSelectedRead() {
        Notification selected = notificationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            messageLabel.setText("Select a notification first.");
            return;
        }
        markSelectedAsRead(selected);
    }

    @FXML
    private void handleMarkAllRead() {
        try {
            notificationService.markAllAsRead(Session.getCurrentUser().getUserId());
            loadNotifications();
            messageLabel.setText("All notifications marked as read.");
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Unable to mark notifications as read.");
        }
    }

    @FXML
    private void handleRefresh() {
        loadNotifications();
    }

    @FXML
    private void handleBack() {
        User user = Session.getCurrentUser();
        if (user != null && "tutor".equalsIgnoreCase(user.getRole())) {
            Navigator.navigate("/fxml/tutor/dashboard.fxml");
        } else {
            Navigator.navigate("/fxml/student/dashboard.fxml");
        }
    }
}
