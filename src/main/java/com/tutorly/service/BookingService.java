package com.tutorly.service;

import com.tutorly.model.Availability;
import com.tutorly.model.Booking;
import com.tutorly.model.BookingDetails;
import com.tutorly.repository.AvailabilityRepository;
import com.tutorly.repository.BookingRepository;

import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class BookingService {

    private final OnlineClassService onlineClassService;
    private final BookingRepository bookingRepository;
    private final AvailabilityRepository availabilityRepository;
    private final NotificationService notificationService;

    public BookingService() {
        onlineClassService = new OnlineClassService();
        bookingRepository = new BookingRepository();
        availabilityRepository = new AvailabilityRepository();
        notificationService = new NotificationService();
    }

    public Booking createBooking(
            int studentId,
            int tutorId,
            int subjectId,
            LocalDate bookingDate,
            LocalTime bookingTime,
            int duration
    ) throws SQLException {
        validateBooking(studentId, tutorId, subjectId, bookingDate, bookingTime, duration);

        if (!availabilityRepository.isAvailable(tutorId, bookingDate, bookingTime, duration)) {
            throw new IllegalArgumentException("The tutor is not available at the selected date and time.");
        }

        checkForConflict(studentId, tutorId, bookingDate, bookingTime, duration);

        Booking booking = new Booking(
                studentId, tutorId, subjectId, bookingDate, bookingTime, duration
        );
        bookingRepository.createBooking(booking);
        notifyTutorOfNewRequest(tutorId);
        return booking;
    }

    /**
     * Creates the requested number of weekly occurrences for every selected slot.
     * The database keeps normal booking rows, so the existing tutor accept/reject
     * workflow continues to work without a separate recurrence table.
     */
    public int createRecurringBookings(
            int studentId,
            int tutorId,
            List<Availability> selectedSlots,
            LocalDate startDate,
            int classesPerSlot
    ) throws SQLException {
        validateRecurringRequest(studentId, tutorId, selectedSlots, startDate, classesPerSlot);

        List<Booking> bookings = new ArrayList<>();

        for (Availability slot : selectedSlots) {
            LocalDate firstOccurrence = firstOccurrenceOnOrAfter(startDate, slot.getDayOfWeek());

            for (int occurrence = 0; occurrence < classesPerSlot; occurrence++) {
                LocalDate bookingDate = firstOccurrence.plusWeeks(occurrence);
                LocalTime bookingTime = slot.getStartTime();
                int duration = (int) java.time.Duration.between(
                        slot.getStartTime(), slot.getEndTime()
                ).toMinutes();

                if (!availabilityRepository.isAvailable(
                        tutorId, slot.getSubjectId(), bookingDate, bookingTime, duration
                )) {
                    throw new IllegalArgumentException(
                            "The slot " + slot.getDayOfWeek() + " " +
                            slot.getStartTime() + "–" + slot.getEndTime() +
                            " is no longer available. Refresh and try again."
                    );
                }

                if (bookingRepository.hasOverlappingStudentBooking(
                        studentId,
                        Date.valueOf(bookingDate),
                        Time.valueOf(bookingTime),
                        duration
                )) {
                    throw new IllegalArgumentException(
                            "A student booking already overlaps " +
                            bookingDate + " at " + bookingTime + "."
                    );
                }

                if (bookingRepository.hasOverlappingBooking(
                        tutorId,
                        Date.valueOf(bookingDate),
                        Time.valueOf(bookingTime),
                        duration
                )) {
                    throw new IllegalArgumentException(
                            "The tutor already has a booking on " +
                            bookingDate + " at " + bookingTime + "."
                    );
                }

                bookings.add(new Booking(
                        studentId,
                        tutorId,
                        slot.getSubjectId(),
                        bookingDate,
                        bookingTime,
                        duration
                ));
            }
        }

        if (bookings.isEmpty()) {
            throw new IllegalArgumentException("No classes were selected.");
        }

        int created = bookingRepository.createBookings(bookings);
        notifyTutorOfRecurringRequest(tutorId, selectedSlots.size(), classesPerSlot, created);
        return created;
    }

    private void validateRecurringRequest(
            int studentId,
            int tutorId,
            List<Availability> selectedSlots,
            LocalDate startDate,
            int classesPerSlot
    ) {
        if (studentId <= 0) throw new IllegalArgumentException("Invalid student ID.");
        if (tutorId <= 0) throw new IllegalArgumentException("Invalid tutor ID.");
        if (selectedSlots == null || selectedSlots.isEmpty()) {
            throw new IllegalArgumentException("Select at least one weekly slot.");
        }
        if (startDate == null) throw new IllegalArgumentException("Start date is required.");
        if (startDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Start date cannot be in the past.");
        }
        if (classesPerSlot < 1 || classesPerSlot > 52) {
            throw new IllegalArgumentException("Classes per selected slot must be between 1 and 52.");
        }

        for (int i = 0; i < selectedSlots.size(); i++) {
            Availability slot = selectedSlots.get(i);
            if (slot == null || slot.getTutorId() != tutorId) {
                throw new IllegalArgumentException("Invalid availability selection.");
            }
            if (slot.getSubjectId() <= 0 || slot.getStartTime() == null || slot.getEndTime() == null) {
                throw new IllegalArgumentException("One of the selected slots is incomplete.");
            }
            if (!slot.getStartTime().isBefore(slot.getEndTime())) {
                throw new IllegalArgumentException("A selected slot has an invalid time range.");
            }
            if (!"Available".equalsIgnoreCase(slot.getStatus())) {
                throw new IllegalArgumentException("One of the selected slots is no longer available.");
            }

            for (int j = i + 1; j < selectedSlots.size(); j++) {
                Availability other = selectedSlots.get(j);
                if (other != null && slot.getDayOfWeek().equalsIgnoreCase(other.getDayOfWeek())
                        && slot.getStartTime().isBefore(other.getEndTime())
                        && other.getStartTime().isBefore(slot.getEndTime())) {
                    throw new IllegalArgumentException(
                            "Selected weekly slots overlap on " + slot.getDayOfWeek() + "."
                    );
                }
            }
        }
    }

    private LocalDate firstOccurrenceOnOrAfter(LocalDate startDate, String dayName) {
        DayOfWeek target;
        try {
            target = DayOfWeek.valueOf(dayName.toUpperCase());
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid availability day: " + dayName);
        }

        int daysUntil = target.getValue() - startDate.getDayOfWeek().getValue();
        if (daysUntil < 0) daysUntil += 7;
        return startDate.plusDays(daysUntil);
    }

    private void checkForConflict(
            int studentId,
            int tutorId,
            LocalDate date,
            LocalTime time,
            int duration
    ) throws SQLException {
        if (bookingRepository.hasOverlappingBooking(
                tutorId, Date.valueOf(date), Time.valueOf(time), duration
        )) {
            throw new IllegalArgumentException("The selected time overlaps with another tutor booking.");
        }

        if (bookingRepository.hasOverlappingStudentBooking(
                studentId, Date.valueOf(date), Time.valueOf(time), duration
        )) {
            throw new IllegalArgumentException("The selected time overlaps with one of your bookings.");
        }
    }

    private void notifyTutorOfNewRequest(int tutorId) throws SQLException {
        int tutorUserId = bookingRepository.findTutorUserId(tutorId);
        notificationService.sendNotification(
                tutorUserId,
                "You have received a new booking request.",
                "Booking"
        );
    }

    private void notifyTutorOfRecurringRequest(
            int tutorId, int slotCount, int classesPerSlot, int total
    ) throws SQLException {
        int tutorUserId = bookingRepository.findTutorUserId(tutorId);
        notificationService.sendNotification(
                tutorUserId,
                "A student submitted " + total + " recurring class requests across " +
                        slotCount + " weekly slot(s), " + classesPerSlot +
                        " class(es) per selected slot.",
                "Booking"
        );
    }

    public List<BookingDetails> getStudentBookingDetails(int studentId) throws SQLException {
        if (studentId <= 0) throw new IllegalArgumentException("Invalid student ID.");
        return bookingRepository.findStudentBookingDetails(studentId);
    }

    public List<BookingDetails> getTutorBookingDetails(int tutorId) throws SQLException {
        if (tutorId <= 0) throw new IllegalArgumentException("Invalid tutor ID.");
        return bookingRepository.findTutorBookingDetails(tutorId);
    }

    public List<Booking> getStudentBookings(int studentId) throws SQLException {
        return bookingRepository.findByStudentId(studentId);
    }

    public List<Booking> getTutorBookings(int tutorId) throws SQLException {
        return bookingRepository.findByTutorId(tutorId);
    }

    public int getStudentUserIdForBooking(
            int bookingId
    ) throws SQLException {

        Booking booking = getExistingBooking(bookingId);

        return bookingRepository.findStudentUserId(
                booking.getStudentId()
        );
    }

    public Booking getBookingById(int bookingId) throws SQLException {
        return getExistingBooking(bookingId);
    }

    public void acceptBooking(int bookingId) throws SQLException {
        acceptBookingInternal(bookingId, null);
    }

    public void acceptBooking(int bookingId, int tutorId) throws SQLException {
        acceptBookingInternal(bookingId, tutorId);
    }

    private void acceptBookingInternal(int bookingId, Integer actingTutorId) throws SQLException {
        Booking booking = getExistingBooking(bookingId);
        if (actingTutorId != null && booking.getTutorId() != actingTutorId) {
            throw new IllegalArgumentException("You can only manage bookings sent to your tutor account.");
        }
        if (!"Pending".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalArgumentException("Only pending bookings can be accepted.");
        }

        if (!availabilityRepository.isAvailable(
                booking.getTutorId(), booking.getSubjectId(), booking.getBookingDate(),
                booking.getBookingTime(), booking.getDuration())) {
            throw new IllegalArgumentException("The tutor is no longer available for this class time.");
        }

        if (bookingRepository.hasOverlappingBooking(
                booking.getTutorId(), Date.valueOf(booking.getBookingDate()),
                Time.valueOf(booking.getBookingTime()), booking.getDuration(), booking.getBookingId())) {
            throw new IllegalArgumentException("This class overlaps another pending or accepted tutor booking.");
        }

        bookingRepository.updateStatus(bookingId, "Accepted");
        try {
            onlineClassService.createClass(bookingId);
        } catch (SQLException e) {
            bookingRepository.updateStatus(bookingId, "Pending");
            throw e;
        }

        int studentUserId = bookingRepository.findStudentUserId(booking.getStudentId());
        notificationService.sendNotification(
                studentUserId,
                "Your booking request has been accepted.",
                "Booking"
        );
    }

    public void rejectBooking(int bookingId) throws SQLException {
        rejectBookingInternal(bookingId, null);
    }

    public void rejectBooking(int bookingId, int tutorId) throws SQLException {
        rejectBookingInternal(bookingId, tutorId);
    }

    private void rejectBookingInternal(int bookingId, Integer actingTutorId) throws SQLException {
        Booking booking = getExistingBooking(bookingId);
        if (actingTutorId != null && booking.getTutorId() != actingTutorId) {
            throw new IllegalArgumentException("You can only manage bookings sent to your tutor account.");
        }
        if (!"Pending".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalArgumentException("Only pending bookings can be rejected.");
        }

        bookingRepository.updateStatus(bookingId, "Rejected");
        int studentUserId = bookingRepository.findStudentUserId(booking.getStudentId());
        notificationService.sendNotification(
                studentUserId,
                "Your booking request has been rejected.",
                "Booking"
        );
    }

    public void completeBooking(int bookingId) throws SQLException {
        Booking booking = getExistingBooking(bookingId);
        if (!"Accepted".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalArgumentException("Only accepted bookings can be completed.");
        }

        bookingRepository.updateStatus(bookingId, "Completed");
        int studentUserId = bookingRepository.findStudentUserId(booking.getStudentId());
        notificationService.sendNotification(
                studentUserId,
                "Your booking has been completed.",
                "Booking"
        );
    }

    public void cancelBooking(int bookingId) throws SQLException {
        cancelBookingInternal(bookingId, null);
    }

    public void cancelBooking(int bookingId, int studentId) throws SQLException {
        cancelBookingInternal(bookingId, studentId);
    }

    private void cancelBookingInternal(int bookingId, Integer actingStudentId) throws SQLException {
        Booking booking = getExistingBooking(bookingId);
        if (actingStudentId != null && booking.getStudentId() != actingStudentId) {
            throw new IllegalArgumentException("You can only cancel your own bookings.");
        }
        if ("Completed".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalArgumentException("Completed bookings cannot be cancelled.");
        }
        if ("Cancelled".equalsIgnoreCase(booking.getStatus())) {
            throw new IllegalArgumentException("Booking is already cancelled.");
        }

        bookingRepository.updateStatus(bookingId, "Cancelled");
        int tutorUserId = bookingRepository.findTutorUserId(booking.getTutorId());
        notificationService.sendNotification(
                tutorUserId,
                "A booking has been cancelled.",
                "Booking"
        );
    }

    public void deleteBooking(int bookingId) throws SQLException {
        getExistingBooking(bookingId);
        bookingRepository.deleteBooking(bookingId);
    }

    private Booking getExistingBooking(int bookingId) throws SQLException {
        if (bookingId <= 0) throw new IllegalArgumentException("Invalid booking ID.");
        Booking booking = bookingRepository.findById(bookingId);
        if (booking == null) throw new IllegalArgumentException("Booking not found.");
        return booking;
    }

    private void validateBooking(
            int studentId, int tutorId, int subjectId,
            LocalDate bookingDate, LocalTime bookingTime, int duration
    ) {
        if (studentId <= 0) throw new IllegalArgumentException("Invalid student ID.");
        if (tutorId <= 0) throw new IllegalArgumentException("Invalid tutor ID.");
        if (subjectId <= 0) throw new IllegalArgumentException("Invalid subject ID.");
        if (bookingDate == null) throw new IllegalArgumentException("Booking date is required.");
        if (bookingTime == null) throw new IllegalArgumentException("Booking time is required.");
        if (duration <= 0) throw new IllegalArgumentException("Duration must be greater than zero.");
        if (bookingDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Booking date cannot be in the past.");
        }

        LocalTime endTime = bookingTime.plusMinutes(duration);
        if (endTime.isBefore(bookingTime)) {
            throw new IllegalArgumentException("Booking cannot cross midnight.");
        }
    }
}
