package com.example.streetinkbookingsystem.models;

import java.time.LocalDate;
import java.time.LocalTime;

public class Booking {
    private int id;
    private LocalTime startTimeSlot;
    private LocalTime endTimeSlot;
    private LocalDate date;
    private int clientId;
    private String username;
    private String projectTitle;
    private String projectDesc;
    private String personalNote;
    private boolean isDepositPayed;


    public Booking() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalTime getStartTimeSlot() {
        return startTimeSlot;
    }

    public void setStartTimeSlot(LocalTime startTimeSlot) {
        this.startTimeSlot = startTimeSlot;
    }

    public LocalTime getEndTimeSlot() {
        return endTimeSlot;
    }

    public void setEndTimeSlot(LocalTime endTimeSlot) {
        this.endTimeSlot = endTimeSlot;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProjectTitle() {
        return projectTitle;
    }

    public void setProjectTitle(String projectTitle) {
        this.projectTitle = projectTitle;
    }

    public String getProjectDesc() {
        return projectDesc;
    }

    public void setProjectDesc(String projectDesc) {
        this.projectDesc = projectDesc;
    }

    public String getPersonalNote() {
        return personalNote;
    }

    public void setPersonalNote(String personalNote) {
        this.personalNote = personalNote;
    }

    public boolean isDepositPayed() {
        return isDepositPayed;
    }

    public void setDepositPayed(boolean depositPayed) {
        isDepositPayed = depositPayed;
    }
}
