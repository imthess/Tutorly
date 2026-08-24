package com.tutorly.patterns.decorator;

/**
 * Base Decorator for TutorProfile.
 *
 * It implements the same interface as the component
 * and delegates the original behavior to the wrapped
 * TutorProfile object.
 */
public abstract class ProfileDecorator
        implements TutorProfile {

    protected final TutorProfile tutorProfile;

    protected ProfileDecorator(
            TutorProfile tutorProfile) {

        if (tutorProfile == null) {
            throw new IllegalArgumentException(
                    "Tutor profile cannot be null."
            );
        }

        this.tutorProfile = tutorProfile;
    }

    @Override
    public String getProfile() {
        return tutorProfile.getProfile();
    }

    @Override
    public double getHourlyRate() {
        return tutorProfile.getHourlyRate();
    }
}
