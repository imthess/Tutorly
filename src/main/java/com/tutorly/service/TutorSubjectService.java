package com.tutorly.service;

import com.tutorly.repository.TutorSubjectRepository;

import java.sql.SQLException;
import java.util.List;

public class TutorSubjectService {

    private final TutorSubjectRepository repository;

    public TutorSubjectService() {
        repository = new TutorSubjectRepository();
    }

    public List<String> getTutorSubjects(
            int tutorId
    ) throws SQLException {

        return repository.findSubjectsByTutorId(tutorId);
    }

    public List<String> getAllSubjects()
            throws SQLException {

        return repository.findAllSubjects();
    }

    public void addSubject(
            int tutorId,
            String subject
    ) throws SQLException {

        if (subject == null || subject.isBlank()) {
            throw new IllegalArgumentException(
                    "Subject is required."
            );
        }

        repository.addSubject(
                tutorId,
                subject
        );
    }

    public void removeSubject(
            int tutorId,
            String subject
    ) throws SQLException {

        repository.removeSubject(
                tutorId,
                subject
        );
    }
}
