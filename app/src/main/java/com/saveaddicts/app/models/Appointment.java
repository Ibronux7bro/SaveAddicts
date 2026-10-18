package com.saveaddicts.app.models;

import java.io.Serializable;

/**
 * Appointment model for physician consultations (FR4 & FR9).
 */
public class Appointment implements Serializable {
    private String id;
    private String patientId;
    private String patientPseudonym;
    private String physicianId;
    private String physicianName;
    private String date; // YYYY-MM-DD
    private String timeSlot; // e.g. "10:00 AM - 10:30 AM"
    private AppointmentStatus status;
    private String reasonNotes;
    private String cancellationReason;
    private long createdAt;

    public Appointment() {
        // Required for Firebase Realtime Database
    }

    public Appointment(String id, String patientId, String patientPseudonym, String physicianId,
                       String physicianName, String date, String timeSlot, AppointmentStatus status,
                       String reasonNotes, long createdAt) {
        this.id = id;
        this.patientId = patientId;
        this.patientPseudonym = patientPseudonym;
        this.physicianId = physicianId;
        this.physicianName = physicianName;
        this.date = date;
        this.timeSlot = timeSlot;
        this.status = status != null ? status : AppointmentStatus.PENDING;
        this.reasonNotes = reasonNotes;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(String patientId) {
        this.patientId = patientId;
    }

    public String getPatientPseudonym() {
        return patientPseudonym != null ? patientPseudonym : "Anonymous Patient";
    }

    public void setPatientPseudonym(String patientPseudonym) {
        this.patientPseudonym = patientPseudonym;
    }

    public String getPhysicianId() {
        return physicianId;
    }

    public void setPhysicianId(String physicianId) {
        this.physicianId = physicianId;
    }

    public String getPhysicianName() {
        return physicianName != null ? physicianName : "Consulting Physician";
    }

    public void setPhysicianName(String physicianName) {
        this.physicianName = physicianName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }

    public AppointmentStatus getStatus() {
        return status != null ? status : AppointmentStatus.PENDING;
    }

    public void setStatus(AppointmentStatus status) {
        this.status = status;
    }

    public String getReasonNotes() {
        return reasonNotes;
    }

    public void setReasonNotes(String reasonNotes) {
        this.reasonNotes = reasonNotes;
    }

    public String getCancellationReason() {
        return cancellationReason;
    }

    public void setCancellationReason(String cancellationReason) {
        this.cancellationReason = cancellationReason;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
