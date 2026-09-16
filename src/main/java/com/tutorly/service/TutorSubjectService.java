package com.tutorly.service;

import com.tutorly.repository.TutorSubjectRepository;

import java.sql.SQLException;
import java.util.List;

public class TutorSubjectService {

    private final TutorSubjectRepository repository;

    public TutorSubjectService() {
        repository = new TutorSubjectRepository();
    }

    public List<TutorSubjectRepository.SubjectOption> getTutorSubjectOptions(
            int tutorId
    ) throws SQLException {
        validateTutorId(tutorId);
        return repository.findSubjectsByTutorId(tutorId);
    }

    public List<String> getTutorSubjects(
            int tutorId
    ) throws SQLException {
        validateTutorId(tutorId);
        return repository.findSubjectsByTutorIdAsNames(tutorId);
    }

    public List<TutorSubjectRepository.SubjectOption> getAllSubjectOptions()
            throws SQLException {
        return repository.findAllSubjectOptions();
    }

    public List<String> getAllSubjects()
            throws SQLException {
        return repository.findAllSubjects();
    }

    public int addSubject(
            int tutorId,
            String subject
    ) throws SQLException {
        validateTutorId(tutorId);
        return repository.addSubject(tutorId, subject);
    }

    public void removeSubject(
            int tutorId,
            int subjectId
    ) throws SQLException {
        validateTutorId(tutorId);
        repository.removeSubject(tutorId, subjectId);
    }

    public boolean tutorTeachesSubject(
            int tutorId,
            int subjectId
    ) throws SQLException {
        validateTutorId(tutorId);
        if (subjectId <= 0) return false;
        return repository.tutorTeachesSubject(tutorId, subjectId);
    }

    private void validateTutorId(int tutorId) {
        if (tutorId <= 0) {
            throw new IllegalArgumentException("Invalid tutor ID.");
        }
    }
}
