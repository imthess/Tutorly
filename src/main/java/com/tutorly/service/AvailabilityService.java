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

    public List<AvailabilityRepository.SubjectOption>
    getTutorSubjects(int tutorId) throws SQLException {

        if (tutorId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid tutor ID."
            );
        }

        return repository.findSubjectsByTutorId(tutorId);
    }

    public void addAvailability(
            int tutorId,
            int subjectId,
            String day,
            LocalTime start,
            LocalTime end,
            String description
    ) throws SQLException {

        validate(
                tutorId,
                subjectId,
                day,
                start,
                end,
                description
        );

        if (!repository.tutorTeachesSubject(
                tutorId,
                subjectId
        )) {

            throw new IllegalArgumentException(
                    "You can only select a subject that you teach."
            );
        }

        Availability availability =
                new Availability();

        availability.setTutorId(tutorId);
        availability.setSubjectId(subjectId);
        availability.setDayOfWeek(day);
        availability.setStartTime(start);
        availability.setEndTime(end);
        availability.setDescription(
                description == null
                        ? ""
                        : description.trim()
        );
        availability.setStatus("Available");

        repository.create(availability);
    }

    public void updateAvailability(
            int availabilityId,
            int tutorId,
            int subjectId,
            String day,
            LocalTime start,
            LocalTime end,
            String description
    ) throws SQLException {

        if (availabilityId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid availability ID."
            );
        }

        validate(
                tutorId,
                subjectId,
                day,
                start,
                end,
                description
        );

        if (!repository.tutorTeachesSubject(
                tutorId,
                subjectId
        )) {

            throw new IllegalArgumentException(
                    "You can only select a subject that you teach."
            );
        }

        boolean updated =
                repository.update(
                        availabilityId,
                        tutorId,
                        subjectId,
                        day,
                        start,
                        end,
                        description == null
                                ? ""
                                : description.trim()
                );

        if (!updated) {
            throw new IllegalArgumentException(
                    "Availability could not be updated."
            );
        }
    }

    private void validate(
            int tutorId,
            int subjectId,
            String day,
            LocalTime start,
            LocalTime end,
            String description
    ) {

        if (tutorId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid tutor ID."
            );
        }

        if (subjectId <= 0) {
            throw new IllegalArgumentException(
                    "Please select a subject."
            );
        }

        if (day == null || day.isBlank()) {
            throw new IllegalArgumentException(
                    "Day is required."
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

        if (description != null &&
                description.trim().length() > 500) {

            throw new IllegalArgumentException(
                    "Description must be 500 characters or less."
            );
        }
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

        repository.updateStatus(
                availabilityId,
                nextStatus
        );
    }
}
