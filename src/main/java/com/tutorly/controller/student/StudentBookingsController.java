package com.tutorly.controller.student;

import com.tutorly.model.BookingDetails;
import com.tutorly.model.Student;
import com.tutorly.service.BookingService;
import com.tutorly.service.StudentService;
import com.tutorly.util.Navigator;
import com.tutorly.util.Session;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.SQLException;

public class StudentBookingsController {

    @FXML
    private TableView<BookingDetails> bookingTable;

    @FXML
    private TableColumn<BookingDetails, String> tutorColumn;

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

    private final StudentService studentService =
            new StudentService();

    @FXML
    private void initialize() {

        if (Session.getCurrentUser() == null ||
                !"student".equalsIgnoreCase(
                        Session.getCurrentUser().getRole()
                )) {

            Navigator.navigate(
                    "/fxml/login.fxml"
            );

            return;
        }

        setupTable();
        loadBookings();
    }

    private void setupTable() {

        tutorColumn.setCellValueFactory(
                new PropertyValueFactory<>("tutorName")
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

    private void loadBookings() {

        try {

            Student student =
                    studentService.getStudentProfile(
                            Session.getCurrentUser()
                                    .getUserId()
                    );

            if (student == null) {

                messageLabel.setText(
                        "Student profile not found."
                );

                return;
            }

            bookingTable.setItems(
                    FXCollections.observableArrayList(
                            bookingService
                                    .getStudentBookingDetails(
                                            student.getStudentId()
                                    )
                    )
            );

            if (bookingTable.getItems().isEmpty()) {

                messageLabel.setText(
                        "You have no bookings yet."
                );

            } else {

                messageLabel.setText(
                        "Your bookings and enrollment status."
                );
            }

        } catch (SQLException e) {

            e.printStackTrace();

            messageLabel.setText(
                    "Unable to load bookings."
            );
        }
    }

    @FXML
    private void handleBack() {

        Navigator.navigate(
                "/fxml/student/dashboard.fxml"
        );
    }
}