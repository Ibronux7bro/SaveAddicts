package com.saveaddicts.app.models;

import java.io.Serializable;

/**
 * User account profile.
 * Conforms to FR1: pseudonym-based registration without requiring personal identity documents.
 */
public class UserProfile implements Serializable {
    private String uid;
    private String pseudonym;
    private String email;
    private UserRole role;
    private SubstanceType substancePreference;
    private long createdAt;

    public UserProfile() {
        // Required for Firebase Realtime Database serialization
    }

    public UserProfile(String uid, String pseudonym, String email, UserRole role, SubstanceType substancePreference, long createdAt) {
        this.uid = uid;
        this.pseudonym = pseudonym;
        this.email = email;
        this.role = role != null ? role : UserRole.PATIENT;
        this.substancePreference = substancePreference != null ? substancePreference : SubstanceType.ALL;
        this.createdAt = createdAt;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPseudonym() {
        return pseudonym != null ? pseudonym : "Anonymous";
    }

    public void setPseudonym(String pseudonym) {
        this.pseudonym = pseudonym;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public UserRole getRole() {
        return role != null ? role : UserRole.PATIENT;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public SubstanceType getSubstancePreference() {
        return substancePreference != null ? substancePreference : SubstanceType.ALL;
    }

    public void setSubstancePreference(SubstanceType substancePreference) {
        this.substancePreference = substancePreference;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public String getInitials() {
        if (pseudonym == null || pseudonym.trim().isEmpty()) {
            return "AN";
        }
        String clean = pseudonym.trim();
        if (clean.length() == 1) return clean.toUpperCase();
        return clean.substring(0, Math.min(clean.length(), 2)).toUpperCase();
    }
}
