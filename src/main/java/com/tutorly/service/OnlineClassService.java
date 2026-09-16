package com.tutorly.service;

import com.tutorly.model.OnlineClass;
import com.tutorly.repository.OnlineClassRepository;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.UUID;

public class OnlineClassService {

    private final OnlineClassRepository onlineClassRepository;

    public OnlineClassService() {
        onlineClassRepository = new OnlineClassRepository();
    }

    public OnlineClass createClass(int bookingId) throws SQLException {
        OnlineClass existing = findByBookingId(bookingId);
        if (existing != null) {
            return existing;
        }

        OnlineClass onlineClass = new OnlineClass();
        onlineClass.setBookingId(bookingId);
        onlineClass.setMeetingLink(generateMeetingLink());
        onlineClass.setStatus("Scheduled");
        onlineClassRepository.create(onlineClass);
        return onlineClass;
    }

    /**
     * Starts an occurrence immediately, regardless of its scheduled calendar
     * date.  The controller is responsible for selecting the exact accepted
     * occurrence.  Completed classes are immutable and cannot be restarted.
     */
    public OnlineClass startClassWithMeetingLink(
            int bookingId,
            String meetingLink,
            LocalDateTime startedAt
    ) throws SQLException {

        OnlineClass onlineClass = createClass(bookingId);

        if ("Completed".equalsIgnoreCase(onlineClass.getStatus())) {
            throw new IllegalStateException(
                    "This online class has already been completed."
            );
        }

        if (isRunning(onlineClass)) {
            return onlineClass;
        }

        if (!"Scheduled".equalsIgnoreCase(onlineClass.getStatus())) {
            throw new IllegalStateException(
                    "This online class is not available to start."
            );
        }

        if (meetingLink == null || meetingLink.isBlank()) {
            throw new IllegalArgumentException("A classroom link is required.");
        }
        if (startedAt == null) {
            throw new IllegalArgumentException("A class start time is required.");
        }

        onlineClass.setMeetingLink(meetingLink);
        onlineClass.setStartTime(startedAt);
        onlineClass.setEndTime(null);
        onlineClass.setStatus("Scheduled");
        onlineClassRepository.updateClass(onlineClass);
        return onlineClass;
    }

    public boolean startClass(int classId) throws SQLException {
        OnlineClass onlineClass = onlineClassRepository.findById(classId);
        if (onlineClass == null
                || !"Scheduled".equalsIgnoreCase(onlineClass.getStatus())
                || onlineClass.getStartTime() != null) {
            return false;
        }

        onlineClass.setStartTime(LocalDateTime.now());
        return updateClass(onlineClass);
    }

    /**
     * Completing the online class records the actual end time and makes the
     * occurrence permanently non-runnable.
     */
    public boolean endClass(int classId) throws SQLException {
        OnlineClass onlineClass = onlineClassRepository.findById(classId);
        if (onlineClass == null
                || !"Scheduled".equalsIgnoreCase(onlineClass.getStatus())
                || onlineClass.getStartTime() == null
                || onlineClass.getEndTime() != null) {
            return false;
        }

        onlineClass.setEndTime(LocalDateTime.now());
        onlineClass.setStatus("Completed");
        return updateClass(onlineClass);
    }

    public OnlineClass findByBookingId(int bookingId) throws SQLException {
        return onlineClassRepository.findByBookingId(bookingId);
    }

    public OnlineClass findById(int classId) throws SQLException {
        return onlineClassRepository.findById(classId);
    }

    public boolean isRunning(OnlineClass onlineClass) {
        return onlineClass != null
                && "Scheduled".equalsIgnoreCase(onlineClass.getStatus())
                && onlineClass.getStartTime() != null
                && onlineClass.getEndTime() == null;
    }

    private boolean updateClass(OnlineClass onlineClass) throws SQLException {
        return onlineClassRepository.updateClass(onlineClass);
    }

    private String generateMeetingLink() {
        return "https://meet.jit.si/Tutorly-" + UUID.randomUUID();
    }
}
