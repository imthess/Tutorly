package com.tutorly.model;

public class BookingDetails {
    private int bookingId;
    private int studentId;
    private int tutorId;
    private int subjectId;
    private String studentName;
    private String tutorName;
    private String subjectName;
    private String bookingDate;
    private String bookingTime;
    private int duration;
    private String status;

    // Additional detail fields
    private String email;
    private String phone;
    private String education;
    private String institute;
    private String qualifications;
    private int experience;
    private double hourlyRate;
    private String bio;

    public BookingDetails() {}

    // Getters and Setters
    public int getBookingId() { return bookingId; }
    public void setBookingId(int bookingId) { this.bookingId = bookingId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getTutorId() { return tutorId; }
    public void setTutorId(int tutorId) { this.tutorId = tutorId; }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getTutorName() { return tutorName; }
    public void setTutorName(String tutorName) { this.tutorName = tutorName; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getBookingDate() { return bookingDate; }
    public void setBookingDate(String bookingDate) { this.bookingDate = bookingDate; }

    public String getBookingTime() { return bookingTime; }
    public void setBookingTime(String bookingTime) { this.bookingTime = bookingTime; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEducation() { return education; }
    public void setEducation(String education) { this.education = education; }

    public String getInstitute() { return institute; }
    public void setInstitute(String institute) { this.institute = institute; }

    public String getQualifications() { return qualifications; }
    public void setQualifications(String qualifications) { this.qualifications = qualifications; }

    public int getExperience() { return experience; }
    public void setExperience(int experience) { this.experience = experience; }

    public double getHourlyRate() { return hourlyRate; }
    public void setHourlyRate(double hourlyRate) { this.hourlyRate = hourlyRate; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }
}