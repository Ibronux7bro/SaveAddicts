package com.saveaddicts.app.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Message model for psychiatrist-patient consultations (FR3).
 * Supports cryptographic payload architecture.
 */
public class ChatMessage implements Serializable {
    private String id;
    private String conversationId;
    private String senderId;
    private String senderName;
    private UserRole senderRole;
    private String recipientId;
    private String encryptedPayload; // Ciphertext in Base64
    private String messageText;      // Decrypted / display text
    private long timestamp;
    private boolean isRead;

    public ChatMessage() {
        // Required for Firebase Realtime Database
    }

    public ChatMessage(String id, String conversationId, String senderId, String senderName,
                       UserRole senderRole, String recipientId, String encryptedPayload,
                       String messageText, long timestamp) {
        this.id = id;
        this.conversationId = conversationId;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderRole = senderRole;
        this.recipientId = recipientId;
        this.encryptedPayload = encryptedPayload;
        this.messageText = messageText;
        this.timestamp = timestamp;
        this.isRead = false;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName != null ? senderName : "Anonymous";
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public UserRole getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(UserRole senderRole) {
        this.senderRole = senderRole;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getEncryptedPayload() {
        return encryptedPayload;
    }

    public void setEncryptedPayload(String encryptedPayload) {
        this.encryptedPayload = encryptedPayload;
    }

    public String getMessageText() {
        return messageText != null ? messageText : "";
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public String getFormattedTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}
