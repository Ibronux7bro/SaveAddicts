package com.saveaddicts.app.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Dated recovery record used to populate the Optimism Graph and track patient progress.
 * Enforces clinical integrity: created and updated exclusively by licensed physicians (FR5, FR8).
 */
public class RecoveryRecord implements Serializable {
    private String id;
    private String patientId;
    private String clinicianId;
    private String clinicianName;
    private int optimismScore; // 0 to 100 percentage
    private HealthStatus healthStatus;
    private String clinicalNotes;
    private long timestamp;

    public RecoveryRecord() {
        // Required for Firebase Realtime Database
    }

    public RecoveryRecord(String id, String patientId, String clinicianId, String clinicianName,
                          int optimismScore, HealthStatus healthStatus, String clinicalNotes, long timestamp) {
        this.id = id;
        this.patientId = patientId;
        this.clinicianId = clinicianId;
        this.clinicianName = clinicianName;
        this.optimismScore = Math.max(0, Math.min(100, optimismScore));
        this.healthStatus = healthStatus != null ? healthStatus : HealthStatus.STABLE;
        this.clinicalNotes = clinicalNotes;
        this.timestamp = timestamp;
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

    public String getClinicianId() {
        return clinicianId;
    }

    public void setClinicianId(String clinicianId) {
        this.clinicianId = clinicianId;
    }

    public String getClinicianName() {
        return clinicianName;
    }

    public void setClinicianName(String clinicianName) {
        this.clinicianName = clinicianName;
    }

    public int getOptimismScore() {
        return optimismScore;
    }

    public void setOptimismScore(int optimismScore) {
        this.optimismScore = Math.max(0, Math.min(100, optimismScore));
    }

    public HealthStatus getHealthStatus() {
        return healthStatus != null ? healthStatus : HealthStatus.STABLE;
    }

    public void setHealthStatus(HealthStatus healthStatus) {
        this.healthStatus = healthStatus;
    }

    public String getClinicalNotes() {
        return clinicalNotes;
    }

    public void setClinicalNotes(String clinicalNotes) {
        this.clinicalNotes = clinicalNotes;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    public String getShortDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}
