package com.tutorly.service;

import com.tutorly.model.OnlineClass;
import com.tutorly.repository.OnlineClassRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public class OnlineClassService {

    private final OnlineClassRepository onlineClassRepository;

    public OnlineClassService() {
        onlineClassRepository =
                new OnlineClassRepository();
    }

    /**
     * Creates a scheduled online class for a booking.
     */
    public OnlineClass createClass(
            int bookingId
    ) throws SQLException {

        OnlineClass onlineClass =
                new OnlineClass();

        onlineClass.setBookingId(bookingId);

        onlineClass.setMeetingLink(
                generateMeetingLink()
        );

        onlineClass.setStatus("Scheduled");

        onlineClassRepository.create(
                onlineClass
        );

        return onlineClass;
    }

    /**
     * Starts an existing online class.
     */
    public boolean startClass(
            int classId
    ) throws SQLException {

        OnlineClass onlineClass =
                onlineClassRepository.findById(classId);

        if (onlineClass == null) {
            return false;
        }

        if (!"Scheduled".equalsIgnoreCase(
                onlineClass.getStatus())) {

            return false;
        }

        onlineClass.setStartTime(
                LocalDateTime.now()
        );

        return updateClass(
                onlineClass
        );
    }

    /**
     * Completes an online class.
     */
    public boolean endClass(
            int classId
    ) throws SQLException {

        OnlineClass onlineClass =
                onlineClassRepository.findById(classId);

        if (onlineClass == null) {
            return false;
        }

        if (!"Scheduled".equalsIgnoreCase(
                onlineClass.getStatus())) {

            return false;
        }

        onlineClass.setEndTime(
                LocalDateTime.now()
        );

        onlineClass.setStatus("Completed");

        return updateClass(
                onlineClass
        );
    }

    /**
     * Finds an online class by booking ID.
     */
    public OnlineClass findByBookingId(
            int bookingId
    ) throws SQLException {

        return onlineClassRepository.findByBookingId(
                bookingId
        );
    }

    /**
     * Finds an online class by class ID.
     */
    public OnlineClass findById(
            int classId
    ) throws SQLException {

        return onlineClassRepository.findById(
                classId
        );
    }

    /**
     * Persists the current online-class state.
     */
    private boolean updateClass(
            OnlineClass onlineClass
    ) throws SQLException {

        return onlineClassRepository.updateClass(
                onlineClass
        );
    }

    /**
     * Generates a unique meeting link.
     */
    private String generateMeetingLink() {

        return "tutorly://class/"
                + UUID.randomUUID();
    }
}
