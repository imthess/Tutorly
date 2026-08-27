package com.tutorly.service;

import com.tutorly.model.Booking;
import com.tutorly.model.BookingDetails;
import com.tutorly.repository.AvailabilityRepository;
import com.tutorly.repository.BookingRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class BookingService {

    private final OnlineClassService onlineClassService;
    private final BookingRepository bookingRepository;
    private final AvailabilityRepository availabilityRepository;
    private final NotificationService notificationService;

    public BookingService() {

        this.onlineClassService =
                new OnlineClassService();

        this.bookingRepository =
                new BookingRepository();

        this.availabilityRepository =
                new AvailabilityRepository();

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

        if (!availabilityRepository.isAvailable(
                tutorId,
                bookingDate,
                bookingTime,
                duration
        )) {

            throw new IllegalArgumentException(
                    "The tutor is not available at the selected date and time."
            );
        }

        Booking booking =
                new Booking(
                        studentId,
                        tutorId,
                        subjectId,
                        bookingDate,
                        bookingTime,
                        duration
                );

        bookingRepository.createBooking(
                booking
        );

        notificationService.sendNotification(
                tutorId,
                "You have received a new booking request.",
                "Booking"
        );

        return booking;
    }

    public List<BookingDetails> getStudentBookingDetails(
            int studentId
    ) throws SQLException {

        if (studentId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid student ID."
            );
        }

        return bookingRepository.findStudentDetails(
                studentId
        );
    }

    public List<BookingDetails> getTutorBookingDetails(
            int tutorId
    ) throws SQLException {

        if (tutorId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid tutor ID."
            );
        }

        return bookingRepository.findTutorDetails(
                tutorId
        );
    }

    public Booking getBookingById(
            int bookingId
    ) throws SQLException {

        return getExistingBooking(bookingId);
    }

    public List<Booking> getStudentBookings(
            int studentId
    ) throws SQLException {

        return bookingRepository.findByStudentId(
                studentId
        );
    }

    public List<Booking> getTutorBookings(
            int tutorId
    ) throws SQLException {

        return bookingRepository.findByTutorId(
                tutorId
        );
    }

    public void acceptBooking(
            int bookingId
    ) throws SQLException {

        Booking booking =
                getExistingBooking(bookingId);

        if (!"Pending".equalsIgnoreCase(
                booking.getStatus())) {

            throw new IllegalArgumentException(
                    "Only pending bookings can be accepted."
            );
        }

        bookingRepository.updateStatus(
                bookingId,
                "Accepted"
        );

        try {

            onlineClassService.createClass(
                    bookingId
            );

        } catch (SQLException e) {

            bookingRepository.updateStatus(
                    bookingId,
                    "Pending"
            );

            throw e;
        }

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

        if (!"Pending".equalsIgnoreCase(
                booking.getStatus())) {

            throw new IllegalArgumentException(
                    "Only pending bookings can be rejected."
            );
        }

        bookingRepository.updateStatus(
                bookingId,
                "Rejected"
        );

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
                bookingRepository.findById(
                        bookingId
                );

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

        if (studentId <= 0)
            throw new IllegalArgumentException(
                    "Invalid student ID."
            );

        if (tutorId <= 0)
            throw new IllegalArgumentException(
                    "Invalid tutor ID."
            );

        if (subjectId <= 0)
            throw new IllegalArgumentException(
                    "Invalid subject ID."
            );

        if (bookingDate == null)
            throw new IllegalArgumentException(
                    "Booking date is required."
            );

        if (bookingTime == null)
            throw new IllegalArgumentException(
                    "Booking time is required."
            );

        if (duration <= 0)
            throw new IllegalArgumentException(
                    "Duration must be greater than zero."
            );

        if (bookingDate.isBefore(LocalDate.now()))
            throw new IllegalArgumentException(
                    "Booking date cannot be in the past."
            );

        if (bookingTime.plusMinutes(duration)
                .isBefore(bookingTime)) {

            throw new IllegalArgumentException(
                    "Booking cannot cross midnight."
            );
        }
    }
}
