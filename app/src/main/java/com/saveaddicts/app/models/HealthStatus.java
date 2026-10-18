package com.saveaddicts.app.models;

/**
 * Clinician-evaluated health and recovery status indicators.
 */
public enum HealthStatus {
    IMPROVING("Steady Progress / Improving", "#2E7D32"),
    STABLE("Stable in Program", "#0288D1"),
    MONITORING("Close Clinical Monitoring", "#F57C00"),
    CRITICAL("Intensive Intervention Needed", "#D32F2F");

    private final String label;
    private final String colorHex;

    HealthStatus(String label, String colorHex) {
        this.label = label;
        this.colorHex = colorHex;
    }

    public String getLabel() {
        return label;
    }

    public String getColorHex() {
        return colorHex;
    }

    public static HealthStatus fromString(String value) {
        if (value == null) return STABLE;
        for (HealthStatus status : values()) {
            if (status.name().equalsIgnoreCase(value) || status.label.equalsIgnoreCase(value)) {
                return status;
            }
        }
        return STABLE;
    }
}
