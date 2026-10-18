package com.saveaddicts.app.models;

/**
 * Status of physical consultations between patients and physicians.
 */
public enum AppointmentStatus {
    PENDING("Pending Confirmation"),
    CONFIRMED("Confirmed"),
    CANCELLED("Cancelled"),
    COMPLETED("Completed");

    private final String displayStatus;

    AppointmentStatus(String displayStatus) {
        this.displayStatus = displayStatus;
    }

    public String getDisplayStatus() {
        return displayStatus;
    }
}
