package com.saveaddicts.app.repositories;

import com.saveaddicts.app.models.AgeGroup;
import com.saveaddicts.app.models.EducationalResource;
import com.saveaddicts.app.models.ResourceType;
import com.saveaddicts.app.models.SubstanceType;

import java.util.List;

/**
 * Repository interface for educational resources (FR6 & FR10).
 */
public interface EducationalRepository {
    void getFilteredResources(SubstanceType substanceFilter,
                              ResourceType typeFilter,
                              AgeGroup ageFilter,
                              Callback<List<EducationalResource>> callback);

    void getAllResources(Callback<List<EducationalResource>> callback);

    void getResourceById(String resourceId, Callback<EducationalResource> callback);

    void addResource(EducationalResource resource, Callback<EducationalResource> callback);

    void updateResource(EducationalResource resource, Callback<Void> callback);

    void deleteResource(String resourceId, Callback<Void> callback);
}
