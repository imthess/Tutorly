package com.tutorly.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import com.tutorly.model.Booking;
import com.tutorly.repository.BookingRepository;

public class BookingService {

    private final BookingRepository bookingRepository;

    private final NotificationService notificationService;

    public BookingService() {

        this.bookingRepository =
                new BookingRepository();

        this.notificationService =
                new NotificationService();
    }

    public Booking createBooking(
            int studentId,
            int tutorId,
            int subjectId,
            LocalDate bookingDate,
            LocalTime bookingTime,
            int duration
    ) throws SQLException {

        validateBooking(
                studentId,
                tutorId,
                subjectId,
                bookingDate,
                bookingTime,
                duration
        );

        Booking booking = new Booking(
                studentId,
                tutorId,
                subjectId,
                bookingDate,
                bookingTime,
                duration
        );

        bookingRepository.createBooking(booking);

        /*
         * Observer Pattern:
         *
         * After the booking is successfully created,
         * notify the tutor.
         */
        notificationService.sendNotification(
                tutorId,
                "You have received a new booking request.",
                "Booking"
        );

        return booking;
    }

    public Booking getBookingById(
            int bookingId
    ) throws SQLException {

        if (bookingId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid booking ID."
            );
        }

        return bookingRepository.findById(bookingId);
    }

    public List<Booking> getStudentBookings(
            int studentId
    ) throws SQLException {

        if (studentId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid student ID."
            );
        }

        return bookingRepository.findByStudentId(
                studentId
        );
    }

    public List<Booking> getTutorBookings(
            int tutorId
    ) throws SQLException {

        if (tutorId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid tutor ID."
            );
        }

        return bookingRepository.findByTutorId(
                tutorId
        );
    }

    public void acceptBooking(
            int bookingId
    ) throws SQLException {

        Booking booking =
                getExistingBooking(bookingId);

        bookingRepository.updateStatus(
                bookingId,
                "Accepted"
        );

        /*
         * Notify the student that the tutor
         * accepted the booking.
         */
        notificationService.sendNotification(
                booking.getStudentId(),
                "Your booking request has been accepted.",
                "Booking"
        );
    }

    public void rejectBooking(
            int bookingId
    ) throws SQLException {

        Booking booking =
                getExistingBooking(bookingId);

        bookingRepository.updateStatus(
                bookingId,
                "Rejected"
        );

        /*
         * Notify the student that the tutor
         * rejected the booking.
         */
        notificationService.sendNotification(
                booking.getStudentId(),
                "Your booking request has been rejected.",
                "Booking"
        );
    }

    public void completeBooking(
            int bookingId
    ) throws SQLException {

        Booking booking =
                getExistingBooking(bookingId);

        bookingRepository.updateStatus(
                bookingId,
                "Completed"
        );

        notificationService.sendNotification(
                booking.getStudentId(),
                "Your booking has been completed.",
                "Booking"
        );
    }

    public void cancelBooking(
            int bookingId
    ) throws SQLException {

        Booking booking =
                getExistingBooking(bookingId);

        bookingRepository.updateStatus(
                bookingId,
                "Cancelled"
        );

        notificationService.sendNotification(
                booking.getStudentId(),
                "Your booking has been cancelled.",
                "Booking"
        );
    }

    public void deleteBooking(
            int bookingId
    ) throws SQLException {

        getExistingBooking(bookingId);

        bookingRepository.deleteBooking(
                bookingId
        );
    }

    private Booking getExistingBooking(
            int bookingId
    ) throws SQLException {

        if (bookingId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid booking ID."
            );
        }

        Booking booking =
                bookingRepository.findById(bookingId);

        if (booking == null) {
            throw new IllegalArgumentException(
                    "Booking not found."
            );
        }

        return booking;
    }

    private void validateBooking(
            int studentId,
            int tutorId,
            int subjectId,
            LocalDate bookingDate,
            LocalTime bookingTime,
            int duration
    ) {

        if (studentId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid student ID."
            );
        }

        if (tutorId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid tutor ID."
            );
        }

        if (subjectId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid subject ID."
            );
        }

        if (bookingDate == null) {
            throw new IllegalArgumentException(
                    "Booking date is required."
            );
        }

        if (bookingTime == null) {
            throw new IllegalArgumentException(
                    "Booking time is required."
            );
        }

        if (duration <= 0) {
            throw new IllegalArgumentException(
                    "Duration must be greater than zero."
            );
        }

        if (bookingDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "Booking date cannot be in the past."
            );
        }
    }
}