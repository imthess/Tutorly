package com.tutorly.service;

import com.tutorly.model.Tutor;
import com.tutorly.repository.TutorRepository;

import java.sql.SQLException;

public class TutorService {

    private final TutorRepository tutorRepository;

    public TutorService() {
        this.tutorRepository = new TutorRepository();
    }

    public Tutor getTutorProfile(int userId)
            throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid user ID."
            );
        }

        return tutorRepository.findByUserId(userId);
    }

    public boolean isProfileComplete(int userId)
            throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid user ID."
            );
        }

        return tutorRepository.isProfileComplete(userId);
    }

    public void updateTutorProfile(
            int userId,
            String qualifications,
            int experience,
            double hourlyRate,
            String bio
    ) throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid user ID."
            );
        }

        if (qualifications == null ||
                qualifications.isBlank()) {

            throw new IllegalArgumentException(
                    "Qualifications are required."
            );
        }

        if (experience < 0) {

            throw new IllegalArgumentException(
                    "Experience cannot be negative."
            );
        }

        if (hourlyRate <= 0) {

            throw new IllegalArgumentException(
                    "Hourly rate must be greater than 0."
            );
        }

        if (bio == null || bio.isBlank()) {

            throw new IllegalArgumentException(
                    "Bio is required."
            );
        }

        tutorRepository.updateProfile(
                userId,
                qualifications,
                experience,
                hourlyRate,
                bio
        );
    }
}
