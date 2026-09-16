package com.tutorly.controller.tutor;

import com.tutorly.model.BookingDetails;
import com.tutorly.model.Tutor;
import com.tutorly.service.BookingService;
import com.tutorly.service.TutorService;
import com.tutorly.util.Navigator;
import com.tutorly.util.Session;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BookingRequestsController {

    @FXML private TableView<BookingDetails> bookingTable;
    @FXML private TableColumn<BookingDetails, String> studentColumn;
    @FXML private TableColumn<BookingDetails, String> subjectColumn;
    @FXML private TableColumn<BookingDetails, String> dateColumn;
    @FXML private TableColumn<BookingDetails, String> timeColumn;
    @FXML private TableColumn<BookingDetails, Integer> durationColumn;
    @FXML private TableColumn<BookingDetails, String> statusColumn;
    @FXML private ComboBox<String> statusFilter;
    @FXML private Label pendingLabel;
    @FXML private Label messageLabel;

    private final BookingService bookingService = new BookingService();
    private final TutorService tutorService = new TutorService();
    private final ObservableList<BookingDetails> allBookings = FXCollections.observableArrayList();
    private int tutorId;

    @FXML
    private void initialize() {
        if (!userGuard()) return;
        setupTable();
        statusFilter.setItems(FXCollections.observableArrayList("Pending", "Accepted", "Rejected", "Completed", "Cancelled", "All"));
        statusFilter.getSelectionModel().select("Pending");
        loadRequests();
    }

    private boolean userGuard() {
        if (Session.getCurrentUser() == null || !"tutor".equalsIgnoreCase(Session.getCurrentUser().getRole())) {
            Navigator.navigate("/fxml/login.fxml");
            return false;
        }
        return true;
    }

    private void setupTable() {
        studentColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        subjectColumn.setCellValueFactory(new PropertyValueFactory<>("subjectName"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("bookingDate"));
        timeColumn.setCellValueFactory(new PropertyValueFactory<>("bookingTime"));
        durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        bookingTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
    }

    private void loadRequests() {
        try {
            Tutor tutor = tutorService.getTutorProfile(Session.getCurrentUser().getUserId());
            if (tutor == null) {
                messageLabel.setText("Tutor profile not found.");
                return;
            }
            tutorId = tutor.getTutorId();
            allBookings.setAll(bookingService.getTutorBookingDetails(tutorId));
            applyFilter();
        } catch (SQLException e) {
            e.printStackTrace();
            messageLabel.setText("Unable to load booking requests.");
        }
    }

    @FXML
    private void handleFilterChanged() {
        applyFilter();
    }

    private void applyFilter() {
        String filter = statusFilter.getValue();
        List<BookingDetails> visible = allBookings.stream()
                .filter(b -> "All".equals(filter) || b.getStatus().equalsIgnoreCase(filter))
                .toList();
        bookingTable.setItems(FXCollections.observableArrayList(visible));

        long pending = allBookings.stream().filter(b -> "Pending".equalsIgnoreCase(b.getStatus())).count();
        pendingLabel.setText(pending + " pending request(s)");
        messageLabel.setText(visible.isEmpty()
                ? "No bookings match this status."
                : "Select one or more rows. You can accept or reject multiple pending requests at once.");
    }

    @FXML
    private void handleAccept() {
        manageSelected(true);
    }

    @FXML
    private void handleReject() {
        manageSelected(false);
    }

    private void manageSelected(boolean accept) {
        List<BookingDetails> selected = new ArrayList<>(bookingTable.getSelectionModel().getSelectedItems());
        List<BookingDetails> pending = selected.stream()
                .filter(b -> "Pending".equalsIgnoreCase(b.getStatus()))
                .toList();

        if (pending.isEmpty()) {
            messageLabel.setText("Select at least one pending booking request.");
            return;
        }

        int success = 0;
        List<String> errors = new ArrayList<>();
        for (BookingDetails booking : pending) {
            try {
                if (accept) {
                    bookingService.acceptBooking(booking.getBookingId(), tutorId);
                } else {
                    bookingService.rejectBooking(booking.getBookingId(), tutorId);
                }
                success++;
            } catch (IllegalArgumentException | SQLException e) {
                errors.add("#" + booking.getBookingId() + ": " + e.getMessage());
            }
        }

        loadRequests();
        if (errors.isEmpty()) {
            messageLabel.setText(success + " request(s) " + (accept ? "accepted" : "rejected") + " successfully.");
        } else {
            messageLabel.setText(success + " request(s) updated. " + String.join("; ", errors));
        }
    }

    @FXML
    private void handleBack() {
        Navigator.navigate("/fxml/tutor/dashboard.fxml");
    }
}
