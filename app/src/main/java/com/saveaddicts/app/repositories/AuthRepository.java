package com.saveaddicts.app.repositories;

import com.saveaddicts.app.models.SubstanceType;
import com.saveaddicts.app.models.UserProfile;
import com.saveaddicts.app.models.UserRole;

/**
 * Authentication and session boundary interface (FR1).
 * Supports pseudonymous registration without identity documents.
 */
public interface AuthRepository {
    void login(String email, String password, Callback<UserProfile> callback);

    void register(String pseudonym, String email, String password, UserRole role, Callback<UserProfile> callback);

    void logout();

    UserProfile getCurrentUser();

    boolean isAuthenticated();

    void updateSubstancePreference(SubstanceType substanceType, Callback<Void> callback);
}
