package com.tutorly.controller.student;

import com.tutorly.model.Student;
import com.tutorly.model.User;
import com.tutorly.service.BookingService;
import com.tutorly.service.StudentService;
import com.tutorly.util.Navigator;
import com.tutorly.util.Session;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;
import java.time.LocalTime;

public class CreateBookingController {

    @FXML
    private TextField tutorIdField;

    @FXML
    private TextField subjectIdField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<String> timeComboBox;

    @FXML
    private ComboBox<Integer> durationComboBox;

    @FXML
    private Label messageLabel;

    private final BookingService bookingService =
            new BookingService();

    private final StudentService studentService =
            new StudentService();

    @FXML
    private void initialize() {

        User user =
                Session.getCurrentUser();

        if (user == null ||
                !"student".equalsIgnoreCase(
                        user.getRole()
                )) {

            Navigator.navigate(
                    "/fxml/login.fxml"
            );

            return;
        }

        timeComboBox.setItems(
                FXCollections.observableArrayList(
                        "08:00",
                        "09:00",
                        "10:00",
                        "11:00",
                        "12:00",
                        "13:00",
                        "14:00",
                        "15:00",
                        "16:00",
                        "17:00",
                        "18:00",
                        "19:00",
                        "20:00",
                        "21:00"
                )
        );

        durationComboBox.setItems(
                FXCollections.observableArrayList(
                        30,
                        60,
                        90,
                        120
                )
        );

        datePicker.setValue(
                LocalDate.now().plusDays(1)
        );
    }

    @FXML
    private void handleBook() {

        try {

            User user =
                    Session.getCurrentUser();

            if (user == null) {

                Navigator.navigate(
                        "/fxml/login.fxml"
                );

                return;
            }

            int tutorId =
                    Integer.parseInt(
                            tutorIdField
                                    .getText()
                                    .trim()
                    );

            int subjectId =
                    Integer.parseInt(
                            subjectIdField
                                    .getText()
                                    .trim()
                    );

            LocalDate date =
                    datePicker.getValue();

            String time =
                    timeComboBox.getValue();

            Integer duration =
                    durationComboBox.getValue();

            if (date == null ||
                    time == null ||
                    duration == null) {

                messageLabel.setText(
                        "Complete all booking fields."
                );

                return;
            }

            Student student =
                    studentService.getStudentProfile(
                            user.getUserId()
                    );

            if (student == null) {

                messageLabel.setText(
                        "Student profile not found."
                );

                return;
            }

            bookingService.createBooking(
                    student.getStudentId(),
                    tutorId,
                    subjectId,
                    date,
                    LocalTime.parse(time),
                    duration
            );

            messageLabel.setText(
                    "Booking request sent successfully."
            );

            clearForm();

        } catch (NumberFormatException e) {

            messageLabel.setText(
                    "Tutor ID and Subject ID must be numbers."
            );

        } catch (IllegalArgumentException e) {

            messageLabel.setText(
                    e.getMessage()
            );

        } catch (Exception e) {

            e.printStackTrace();

            messageLabel.setText(
                    "Unable to create booking."
            );
        }
    }

    private void clearForm() {

        tutorIdField.clear();
        subjectIdField.clear();

        timeComboBox.getSelectionModel()
                .clearSelection();

        durationComboBox.getSelectionModel()
                .clearSelection();

        datePicker.setValue(
                LocalDate.now().plusDays(1)
        );
    }

    @FXML
    private void handleBack() {

        Navigator.navigate(
                "/fxml/student/find-tutors.fxml"
        );
    }
}