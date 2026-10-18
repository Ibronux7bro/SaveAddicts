package com.saveaddicts.app.repositories;

import com.saveaddicts.app.models.UserProfile;
import com.saveaddicts.app.models.UserRole;

import java.util.List;

/**
 * Repository interface for retrieving clinician and patient directories within authorized bounds.
 */
public interface UserRepository {
    void getUsersByRole(UserRole role, Callback<List<UserProfile>> callback);

    void getUserById(String uid, Callback<UserProfile> callback);
}
