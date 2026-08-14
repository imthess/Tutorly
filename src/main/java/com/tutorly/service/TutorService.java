package com.tutorly.service;

import com.tutorly.model.Tutor;
import com.tutorly.repository.TutorRepository;

import java.sql.SQLException;

public class TutorService {

    private final TutorRepository tutorRepository;

    public TutorService() {
        tutorRepository = new TutorRepository();
    }

    public Tutor getTutorProfile(int userId)
            throws SQLException {

        return tutorRepository.findByUserId(userId);
    }

    public void updateTutorProfile(
            int userId,
            String qualifications,
            String experienceText,
            String hourlyRateText,
            String bio
    ) throws SQLException {

        if (experienceText == null || experienceText.isBlank()) {
            throw new IllegalArgumentException(
                    "Experience is required."
            );
        }

        if (hourlyRateText == null || hourlyRateText.isBlank()) {
            throw new IllegalArgumentException(
                    "Hourly rate is required."
            );
        }

        int experience;

        double hourlyRate;

        try {
            experience = Integer.parseInt(
                    experienceText.trim()
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Experience must be a whole number."
            );
        }

        try {
            hourlyRate = Double.parseDouble(
                    hourlyRateText.trim()
            );
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(
                    "Hourly rate must be a number."
            );
        }

        if (experience < 0) {
            throw new IllegalArgumentException(
                    "Experience cannot be negative."
            );
        }

        if (hourlyRate < 0) {
            throw new IllegalArgumentException(
                    "Hourly rate cannot be negative."
            );
        }

        System.out.println(
                "Updating tutor profile for user ID: " + userId
        );

        tutorRepository.updateProfile(
                userId,
                qualifications == null
                        ? ""
                        : qualifications.trim(),
                experience,
                hourlyRate,
                bio == null
                        ? ""
                        : bio.trim()
        );

        System.out.println(
                "Tutor profile update completed."
        );
    }
}
