package com.tutorly.patterns.decorator;

/**
 * Concrete Decorator that adds Top Rated status
 * to a tutor profile.
 */
public class TopRatedTutorDecorator
        extends ProfileDecorator {

    public TopRatedTutorDecorator(
            TutorProfile tutorProfile) {

        super(tutorProfile);
    }

    @Override
    public String getProfile() {

        return tutorProfile.getProfile()
                + " | Top Rated";
    }
}
