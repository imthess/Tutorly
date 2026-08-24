package com.tutorly.patterns.decorator.demo;

import com.tutorly.patterns.decorator.BasicTutorProfile;
import com.tutorly.patterns.decorator.TopRatedTutorDecorator;
import com.tutorly.patterns.decorator.TutorProfile;
import com.tutorly.patterns.decorator.VerifiedTutorDecorator;

/**
 * Demonstrates the Decorator design pattern.
 */
public final class DecoratorDemo {

    private DecoratorDemo() {
    }

    public static void main(String[] args) {

        TutorProfile profile =
                new BasicTutorProfile(
                        "Max Verstappen",
                        100000
                );

        System.out.println(
                "Basic profile: "
                        + profile.getProfile()
        );

        profile =
                new VerifiedTutorDecorator(profile);

        System.out.println(
                "After verification: "
                        + profile.getProfile()
        );

        profile =
                new TopRatedTutorDecorator(profile);

        System.out.println(
                "After Top Rated decoration: "
                        + profile.getProfile()
        );

        System.out.println(
                "Hourly rate: "
                        + profile.getHourlyRate()
        );
    }
}
