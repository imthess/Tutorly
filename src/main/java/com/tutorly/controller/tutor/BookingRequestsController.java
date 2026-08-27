package com.tutorly.controller.tutor;

import com.tutorly.model.BookingDetails;
import com.tutorly.model.Tutor;
import com.tutorly.service.BookingService;
import com.tutorly.service.TutorService;
import com.tutorly.util.Navigator;
import com.tutorly.util.Session;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

public class BookingRequestsController {

    @FXML
    private TableView<BookingDetails> bookingTable;

    @FXML
    private TableColumn<BookingDetails, String> studentColumn;

    @FXML
    private TableColumn<BookingDetails, String> subjectColumn;

    @FXML
    private TableColumn<BookingDetails, String> dateColumn;

    @FXML
    private TableColumn<BookingDetails, String> timeColumn;

    @FXML
    private TableColumn<BookingDetails, Integer> durationColumn;

    @FXML
    private TableColumn<BookingDetails, String> statusColumn;

    @FXML
    private Label messageLabel;

    private final BookingService bookingService =
            new BookingService();

    private final TutorService tutorService =
            new TutorService();

    @FXML
    private void initialize() {

        if (Session.getCurrentUser() == null ||
                !"tutor".equalsIgnoreCase(
                        Session.getCurrentUser().getRole()
                )) {

            Navigator.navigate(
                    "/fxml/login.fxml"
            );

            return;
        }

        setupTable();
        loadRequests();
    }

    private void setupTable() {

        studentColumn.setCellValueFactory(
                new PropertyValueFactory<>("studentName")
        );

        subjectColumn.setCellValueFactory(
                new PropertyValueFactory<>("subjectName")
        );

        dateColumn.setCellValueFactory(
                new PropertyValueFactory<>("bookingDate")
        );

        timeColumn.setCellValueFactory(
                new PropertyValueFactory<>("bookingTime")
        );

        durationColumn.setCellValueFactory(
                new PropertyValueFactory<>("duration")
        );

        statusColumn.setCellValueFactory(
                new PropertyValueFactory<>("status")
        );
    }

    private void loadRequests() {

        try {

            Tutor tutor =
                    tutorService.getTutorProfile(
                            Session.getCurrentUser()
                                    .getUserId()
                    );

            if (tutor == null) {

                messageLabel.setText(
                        "Tutor profile not found."
                );

                return;
            }

            bookingTable.setItems(
                    FXCollections.observableArrayList(
                            bookingService
                                    .getTutorBookingDetails(
                                            tutor.getTutorId()
                                    )
                    )
            );

            if (bookingTable.getItems().isEmpty()) {

                messageLabel.setText(
                        "No booking requests yet."
                );

            } else {

                messageLabel.setText(
                        "Review your booking requests below."
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();

            messageLabel.setText(
                    "Unable to load booking requests."
            );
        }
    }

    @FXML
    private void handleAccept() {

        BookingDetails selected =
                bookingTable
                        .getSelectionModel()
                        .getSelectedItem();

        if (selected == null) {

            messageLabel.setText(
                    "Select a booking request first."
            );

            return;
        }

        try {

            bookingService.acceptBooking(
                    selected.getBookingId()
            );

            messageLabel.setText(
                    "Booking accepted. "
                            + selected.getStudentName()
                            + " is now enrolled for "
                            + selected.getSubjectName()
                            + "."
            );

            loadRequests();

        } catch (IllegalArgumentException e) {

            messageLabel.setText(
                    e.getMessage()
            );

        } catch (SQLException e) {

            e.printStackTrace();

            messageLabel.setText(
                    "Could not accept booking."
            );
        }
    }

    @FXML
    private void handleReject() {

        BookingDetails selected =
                bookingTable
                        .getSelectionModel()
                        .getSelectedItem();

        if (selected == null) {

            messageLabel.setText(
                    "Select a booking request first."
            );

            return;
        }

        try {

            bookingService.rejectBooking(
                    selected.getBookingId()
            );

            messageLabel.setText(
                    "Booking request rejected."
            );

            loadRequests();

        } catch (IllegalArgumentException e) {

            messageLabel.setText(
                    e.getMessage()
            );

        } catch (SQLException e) {

            e.printStackTrace();

            messageLabel.setText(
                    "Could not reject booking."
            );
        }
    }

    @FXML
    private void handleBack() {

        Navigator.navigate(
                "/fxml/tutor/dashboard.fxml"
        );
    }
}