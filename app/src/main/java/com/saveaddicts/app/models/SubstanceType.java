package com.saveaddicts.app.models;

/**
 * Substance categories supported by the Save Addicts project specification.
 */
public enum SubstanceType {
    ALL("All Categories"),
    ALCOHOL("Alcohol"),
    OPIOIDS("Opioids"),
    CANNABIS("Cannabis"),
    OTHER("Other / Polysubstance");

    private final String displayName;

    SubstanceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static SubstanceType fromString(String value) {
        if (value == null) return ALL;
        for (SubstanceType type : values()) {
            if (type.name().equalsIgnoreCase(value) || type.displayName.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return ALL;
    }
}
