package com.tutorly.service;

import com.tutorly.model.Student;
import com.tutorly.repository.StudentRepository;

import java.sql.SQLException;

public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService() {
        this.studentRepository =
                new StudentRepository();
    }

    public Student getStudentProfile(
            int userId
    ) throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid user ID."
            );
        }

        return studentRepository.findByUserId(userId);
    }

    public boolean isProfileComplete(int userId)
            throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid user ID."
            );
        }

        return studentRepository.isProfileComplete(userId);
    }

    public void updateStudentProfile(
            int userId,
            String education,
            String institute
    ) throws SQLException {

        if (userId <= 0) {
            throw new IllegalArgumentException(
                    "Invalid user ID."
            );
        }

        if (education == null || education.isBlank()) {
            throw new IllegalArgumentException(
                    "Education is required."
            );
        }

        if (institute == null || institute.isBlank()) {
            throw new IllegalArgumentException(
                    "Institute is required."
            );
        }

        studentRepository.updateProfile(
                userId,
                education,
                institute
        );
    }
}