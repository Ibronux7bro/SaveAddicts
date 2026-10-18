package com.saveaddicts.app.models;

/**
 * Age group classifications for educational content filtering.
 */
public enum AgeGroup {
    ALL("All Ages"),
    YOUTH("Youth (15-24)"),
    ADULT("Adult (25-54)"),
    SENIOR("Senior (55+)");

    private final String label;

    AgeGroup(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static AgeGroup fromString(String value) {
        if (value == null) return ALL;
        for (AgeGroup age : values()) {
            if (age.name().equalsIgnoreCase(value) || age.label.equalsIgnoreCase(value)) {
                return age;
            }
        }
        return ALL;
    }
}
