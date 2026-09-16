package com.tutorly.service;

import com.tutorly.model.Availability;
import com.tutorly.repository.AvailabilityRepository;
import com.tutorly.repository.TutorSubjectRepository;

import java.sql.SQLException;
import java.time.LocalTime;
import java.util.List;

public class AvailabilityService {

    private final AvailabilityRepository repository;
    private final TutorSubjectRepository tutorSubjectRepository;

    public AvailabilityService() {
        repository = new AvailabilityRepository();
        tutorSubjectRepository = new com.tutorly.repository.TutorSubjectRepository();
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

        if (repository.overlapsExistingAvailability(
                tutorId, day, start, end, -1)) {
            throw new IllegalArgumentException(
                    "This time overlaps another available slot on " + day + "."
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

        if (repository.overlapsExistingAvailability(
                tutorId, day, start, end, availabilityId)) {
            throw new IllegalArgumentException(
                    "This time overlaps another available slot on " + day + "."
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

    public int addTutorSubject(int tutorId, String subjectName) throws SQLException {
        if (tutorId <= 0) {
            throw new IllegalArgumentException("Invalid tutor ID.");
        }
        return tutorSubjectRepository.addSubject(tutorId, subjectName);
    }

    public void removeTutorSubject(int tutorId, int subjectId) throws SQLException {
        if (tutorId <= 0 || subjectId <= 0) {
            throw new IllegalArgumentException("Invalid tutor or subject.");
        }
        tutorSubjectRepository.removeSubject(tutorId, subjectId);
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
