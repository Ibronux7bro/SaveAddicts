package com.saveaddicts.app.models;

/**
 * Status of psychiatrist consultation sessions (FR7).
 */
public enum SessionStatus {
    REQUESTED("Requested / In Queue"),
    ACCEPTED("Accepted / Active"),
    COMPLETED("Completed"),
    DECLINED("Declined");

    private final String label;

    SessionStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
