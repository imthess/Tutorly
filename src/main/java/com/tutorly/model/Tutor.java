package com.tutorly.model;

public class Tutor extends User {

    private int tutorId;
    private String qualifications;
    private int experience;
    private double hourlyRate;
    private String bio;

    public Tutor() {
        super();
    }

    public Tutor(
            String fullName,
            String email,
            String password,
            String phone) {

        super(
                fullName,
                email,
                password,
                phone,
                "tutor"
        );
    }

    public int getTutorId() {
        return tutorId;
    }

    public void setTutorId(int tutorId) {
        this.tutorId = tutorId;
    }

    public String getQualifications() {
        return qualifications;
    }

    public void setQualifications(String qualifications) {
        this.qualifications = qualifications;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public double getHourlyRate() {
        return hourlyRate;
    }

    public void setHourlyRate(double hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }
}
