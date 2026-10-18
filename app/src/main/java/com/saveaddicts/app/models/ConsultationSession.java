package com.saveaddicts.app.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Consultation session model for Psychiatrist workflow (FR7).
 */
public class ConsultationSession implements Serializable {
    private String id;
    private String patientId;
    private String patientPseudonym;
    private String psychiatristId;
    private String psychiatristName;
    private SubstanceType substanceType;
    private SessionStatus status;
    private String initialNotes;
    private long requestedAt;

    public ConsultationSession() {
        // Required for Firebase Realtime Database
    }

    public ConsultationSession(String id, String patientId, String patientPseudonym,
                               String psychiatristId, String psychiatristName,
                               SubstanceType substanceType, SessionStatus status,
                               String initialNotes, long requestedAt) {
        this.id = id;
        this.patientId = patientId;
        this.patientPseudonym = patientPseudonym;
        this.psychiatristId = psychiatristId;
        this.psychiatristName = psychiatristName;
        this.substanceType = substanceType != null ? substanceType : SubstanceType.ALL;
        this.status = status != null ? status : SessionStatus.REQUESTED;
        this.initialNotes = initialNotes;
        this.requestedAt = requestedAt;
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

    public String getPsychiatristId() {
        return psychiatristId;
    }

    public void setPsychiatristId(String psychiatristId) {
        this.psychiatristId = psychiatristId;
    }

    public String getPsychiatristName() {
        return psychiatristName;
    }

    public void setPsychiatristName(String psychiatristName) {
        this.psychiatristName = psychiatristName;
    }

    public SubstanceType getSubstanceType() {
        return substanceType != null ? substanceType : SubstanceType.ALL;
    }

    public void setSubstanceType(SubstanceType substanceType) {
        this.substanceType = substanceType;
    }

    public SessionStatus getStatus() {
        return status != null ? status : SessionStatus.REQUESTED;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public String getInitialNotes() {
        return initialNotes;
    }

    public void setInitialNotes(String initialNotes) {
        this.initialNotes = initialNotes;
    }

    public long getRequestedAt() {
        return requestedAt;
    }

    public void setRequestedAt(long requestedAt) {
        this.requestedAt = requestedAt;
    }

    public String getFormattedDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
        return sdf.format(new Date(requestedAt));
    }
}
