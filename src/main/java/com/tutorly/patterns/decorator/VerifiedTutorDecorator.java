package com.tutorly.patterns.decorator;

/**
 * Concrete Decorator that adds verified status
 * to a tutor profile.
 */
public class VerifiedTutorDecorator
        extends ProfileDecorator {

    public VerifiedTutorDecorator(
            TutorProfile tutorProfile) {

        super(tutorProfile);
    }

    @Override
    public String getProfile() {

        return tutorProfile.getProfile()
                + " | Verified Tutor";
    }
}
