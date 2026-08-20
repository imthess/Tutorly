package com.tutorly.model;

import java.time.LocalDate;
import java.time.LocalTime;

public class Booking {

    private int bookingId;
    private int studentId;
    private int tutorId;
    private int subjectId;
    private LocalDate bookingDate;
    private LocalTime bookingTime;
    private int duration;
    private String status;

    public Booking() {
        this.status = "Pending";
    }

    public Booking(
            int studentId,
            int tutorId,
            int subjectId,
            LocalDate bookingDate,
            LocalTime bookingTime,
            int duration
    ) {
        this.studentId = studentId;
        this.tutorId = tutorId;
        this.subjectId = subjectId;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.duration = duration;
        this.status = "Pending";
    }

    public Booking(
            int bookingId,
            int studentId,
            int tutorId,
            int subjectId,
            LocalDate bookingDate,
            LocalTime bookingTime,
            int duration,
            String status
    ) {
        this.bookingId = bookingId;
        this.studentId = studentId;
        this.tutorId = tutorId;
        this.subjectId = subjectId;
        this.bookingDate = bookingDate;
        this.bookingTime = bookingTime;
        this.duration = duration;
        this.status = status;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getTutorId() {
        return tutorId;
    }

    public void setTutorId(int tutorId) {
        this.tutorId = tutorId;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDate bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalTime getBookingTime() {
        return bookingTime;
    }

    public void setBookingTime(LocalTime bookingTime) {
        this.bookingTime = bookingTime;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}