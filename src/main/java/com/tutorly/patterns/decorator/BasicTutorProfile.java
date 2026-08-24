package com.tutorly.patterns.decorator;

/**
 * Concrete Component of the Decorator pattern.

 * Represents a normal tutor profile before
 * additional features are applied.
 */
public class BasicTutorProfile
        implements TutorProfile {

    private final String tutorName;
    private final double hourlyRate;

    public BasicTutorProfile(
            String tutorName,
            double hourlyRate) {

        if (tutorName == null ||
                tutorName.isBlank()) {

            throw new IllegalArgumentException(
                    "Tutor name is required."
            );
        }

        if (hourlyRate <= 0) {

            throw new IllegalArgumentException(
                    "Hourly rate must be greater than 0."
            );
        }

        this.tutorName = tutorName;
        this.hourlyRate = hourlyRate;
    }

    @Override
    public String getProfile() {
        return tutorName;
    }

    @Override
    public double getHourlyRate() {
        return hourlyRate;
    }
}
