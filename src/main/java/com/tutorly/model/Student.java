package com.tutorly.model;

public class Student extends User {

    private int studentId;
    private String education;
    private String institute;

    public Student() {
        setRole("student");
    }

    public Student(String fullName, String email, String password,
                   String phone) {
        super(fullName, email, password, phone, "student");
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getInstitute() {
        return institute;
    }

    public void setInstitute(String institute) {
        this.institute = institute;
    }
}
