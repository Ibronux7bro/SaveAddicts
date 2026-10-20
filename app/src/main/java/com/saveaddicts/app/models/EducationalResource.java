package com.saveaddicts.app.models;

import java.io.Serializable;

/**
 * Educational resource model for videos, books, and articles (FR6 & FR10).
 * Clearly tags sample vs verified content.
 */
public class EducationalResource implements Serializable {
    private String id;
    private String title;
    private String description;
    private ResourceType resourceType;
    private SubstanceType substanceType;
    private AgeGroup ageGroup;
    private String culturalContext;
    private String authorOrSource;
    private String contentBody;
    private boolean isSampleContent;
    private long createdAt;

    public EducationalResource() {
        // Required for Firebase Realtime Database
    }

    public EducationalResource(String id, String title, String description,
                               ResourceType resourceType, SubstanceType substanceType,
                               AgeGroup ageGroup, String culturalContext,
                               String authorOrSource, String contentBody,
                               boolean isSampleContent, long createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.resourceType = resourceType != null ? resourceType : ResourceType.ARTICLE;
        this.substanceType = substanceType != null ? substanceType : SubstanceType.ALL;
        this.ageGroup = ageGroup != null ? ageGroup : AgeGroup.ALL;
        this.culturalContext = culturalContext;
        this.authorOrSource = authorOrSource;
        this.contentBody = contentBody;
        this.isSampleContent = isSampleContent;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ResourceType getResourceType() {
        return resourceType != null ? resourceType : ResourceType.ARTICLE;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public SubstanceType getSubstanceType() {
        return substanceType != null ? substanceType : SubstanceType.ALL;
    }

    public void setSubstanceType(SubstanceType substanceType) {
        this.substanceType = substanceType;
    }

    public AgeGroup getAgeGroup() {
        return ageGroup != null ? ageGroup : AgeGroup.ALL;
    }

    public void setAgeGroup(AgeGroup ageGroup) {
        this.ageGroup = ageGroup;
    }

    public String getCulturalContext() {
        return culturalContext != null ? culturalContext : "Universal";
    }

    public void setCulturalContext(String culturalContext) {
        this.culturalContext = culturalContext;
    }

    public String getAuthorOrSource() {
        return authorOrSource != null ? authorOrSource : "Educational Contributor";
    }

    public void setAuthorOrSource(String authorOrSource) {
        this.authorOrSource = authorOrSource;
    }

    public String getContentBody() {
        return contentBody != null ? contentBody : "";
    }

    public void setContentBody(String contentBody) {
        this.contentBody = contentBody;
    }

    public boolean isSampleContent() {
        return isSampleContent;
    }

    public void setSampleContent(boolean sampleContent) {
        isSampleContent = sampleContent;
    }

    public void setIsSampleContent(boolean sampleContent) {
        this.isSampleContent = sampleContent;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
