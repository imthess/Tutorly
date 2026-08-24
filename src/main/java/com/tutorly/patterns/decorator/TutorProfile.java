package com.tutorly.patterns.decorator;

/**
 * Component interface for the Decorator pattern.
 *
 * Represents a tutor profile that can be decorated
 * with additional features dynamically.
 */
public interface TutorProfile {

    String getProfile();

    double getHourlyRate();
}
