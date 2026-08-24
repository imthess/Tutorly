package com.tutorly.service;

import com.tutorly.model.Tutor;
import com.tutorly.patterns.decorator.BasicTutorProfile;
import com.tutorly.patterns.decorator.TopRatedTutorDecorator;
import com.tutorly.patterns.decorator.TutorProfile;
import com.tutorly.patterns.decorator.VerifiedTutorDecorator;

/**
 * Service responsible for applying TutorProfile
 * decorators according to the tutor's current data.
 */
public class TutorProfileDecoratorService {

    /**
     * Builds a decorated TutorProfile dynamically.
     *
     * Current rules:
     *
     * 1. Every tutor starts with BasicTutorProfile.
     * 2. A tutor with qualifications receives
     *    VerifiedTutorDecorator.
     * 3. A tutor with at least 5 years of experience
     *    receives TopRatedTutorDecorator.
     */
    public TutorProfile buildProfile(Tutor tutor) {

        if (tutor == null) {
            throw new IllegalArgumentException(
                    "Tutor cannot be null."
            );
        }

        /*
         * Concrete Component.
         */
        TutorProfile profile =
                new BasicTutorProfile(
                        tutor.getFullName(),
                        tutor.getHourlyRate()
                );

        /*
         * Add Verified decoration when the tutor
         * has provided qualifications.
         */
        if (tutor.getQualifications() != null
                && !tutor.getQualifications().isBlank()) {

            profile =
                    new VerifiedTutorDecorator(profile);
        }

        /*
         * Add Top Rated decoration when the tutor
         * has at least five years of experience.
         */
        if (tutor.getExperience() >= 5) {

            profile =
                    new TopRatedTutorDecorator(profile);
        }

        return profile;
    }
}
