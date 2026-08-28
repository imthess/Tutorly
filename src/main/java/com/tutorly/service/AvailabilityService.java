package com.tutorly.service;

import com.tutorly.model.Availability;
import com.tutorly.repository.AvailabilityRepository;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.List;

public class AvailabilityService {

    private final AvailabilityRepository repository;

    public AvailabilityService() {
        repository = new AvailabilityRepository();
    }

    public List<Availability> getTutorAvailability(
            int tutorId
    ) throws SQLException {

        if (tutorId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid tutor ID."
            );
        }

        return repository.findByTutorId(tutorId);
    }

    public void addAvailability(
            int tutorId,
            String day,
            LocalTime start,
            LocalTime end,
            String description
    ) throws SQLException {

        if (tutorId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid tutor ID."
            );
        }

        if (day == null || day.isBlank()) {
            throw new IllegalArgumentException(
                    "Day is required."
            );
        }

        if (description == null) {
            description = "";
        }

        description = description.trim();

        if (description.length() > 500) {
            throw new IllegalArgumentException(
                    "Description must be 500 characters or less."
            );
        }

        if (start == null || end == null) {
            throw new IllegalArgumentException(
                    "Start and end time are required."
            );
        }

        if (!start.isBefore(end)) {
            throw new IllegalArgumentException(
                    "End time must be after start time."
            );
        }

        Availability availability =
                new Availability();

        availability.setTutorId(tutorId);
        availability.setDayOfWeek(day);
        availability.setStartTime(start);
        availability.setEndTime(end);
        availability.setDescription(description);
        availability.setStatus("Available");

        repository.create(availability);
    }

    public void deleteAvailability(
            int availabilityId
    ) throws SQLException {

        if (availabilityId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid availability ID."
            );
        }

        repository.delete(availabilityId);
    }

    public void toggleAvailability(
            int availabilityId,
            String currentStatus
    ) throws SQLException {

        String nextStatus =
                "Available".equalsIgnoreCase(currentStatus)
                        ? "Unavailable"
                        : "Available";

        repository.updateStatus(availabilityId, nextStatus);
    }
}
