package com.saveaddicts.app.models;

/**
 * The four operational roles defined in the Save Addicts specification.
 */
public enum UserRole {
    PATIENT("Patient"),
    PSYCHIATRIST("Psychiatrist"),
    PHYSICIAN("Physician"),
    ADMIN("Admin");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static UserRole fromString(String value) {
        if (value == null) return PATIENT;
        for (UserRole role : values()) {
            if (role.name().equalsIgnoreCase(value) || role.displayName.equalsIgnoreCase(value)) {
                return role;
            }
        }
        return PATIENT;
    }
}
