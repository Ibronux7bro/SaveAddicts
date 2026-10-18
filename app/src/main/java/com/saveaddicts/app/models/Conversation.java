package com.saveaddicts.app.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Conversation thread metadata between a Patient and a Psychiatrist.
 */
public class Conversation implements Serializable {
    private String id;
    private String patientId;
    private String patientPseudonym;
    private String psychiatristId;
    private String psychiatristName;
    private String lastMessageText;
    private long lastMessageTimestamp;
    private int unreadCount;

    public Conversation() {
        // Required for Firebase Realtime Database
    }

    public Conversation(String id, String patientId, String patientPseudonym,
                        String psychiatristId, String psychiatristName,
                        String lastMessageText, long lastMessageTimestamp, int unreadCount) {
        this.id = id;
        this.patientId = patientId;
        this.patientPseudonym = patientPseudonym;
        this.psychiatristId = psychiatristId;
        this.psychiatristName = psychiatristName;
        this.lastMessageText = lastMessageText;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.unreadCount = unreadCount;
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
        return psychiatristName != null ? psychiatristName : "Licensed Psychiatrist";
    }

    public void setPsychiatristName(String psychiatristName) {
        this.psychiatristName = psychiatristName;
    }

    public String getLastMessageText() {
        return lastMessageText != null ? lastMessageText : "No messages yet";
    }

    public void setLastMessageText(String lastMessageText) {
        this.lastMessageText = lastMessageText;
    }

    public long getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public void setLastMessageTimestamp(long lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public String getFormattedDate() {
        if (lastMessageTimestamp <= 0) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault());
        return sdf.format(new Date(lastMessageTimestamp));
    }
}
