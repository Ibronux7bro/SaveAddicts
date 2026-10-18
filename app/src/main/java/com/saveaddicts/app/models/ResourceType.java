package com.saveaddicts.app.models;

/**
 * Educational resource media types defined in the specification.
 */
public enum ResourceType {
    ARTICLE("Article"),
    BOOK("Book / Guide"),
    VIDEO("Video");

    private final String label;

    ResourceType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public static ResourceType fromString(String value) {
        if (value == null) return ARTICLE;
        for (ResourceType type : values()) {
            if (type.name().equalsIgnoreCase(value) || type.label.equalsIgnoreCase(value)) {
                return type;
            }
        }
        return ARTICLE;
    }
}
