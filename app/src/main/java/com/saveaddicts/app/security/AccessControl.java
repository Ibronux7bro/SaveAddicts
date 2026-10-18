package com.saveaddicts.app.security;

import com.saveaddicts.app.models.UserRole;

/**
 * Enforces role-based access control (RBAC) across the application.
 * Restricts clinical records modification, administrative content curation,
 * and confidential consultation handling to authorized roles.
 */
public final class AccessControl {

    private AccessControl() {
        // Utility class
    }

    /**
     * FR8: Only licensed Physicians can update clinical recovery assessments and Optimism Graph records.
     */
    public static boolean canModifyRecoveryRecord(UserRole role) {
        return role == UserRole.PHYSICIAN;
    }

    /**
     * FR7: Only licensed Psychiatrists can access consultation queues and conduct psychiatric sessions.
     */
    public static boolean canConductPsychiatristSessions(UserRole role) {
        return role == UserRole.PSYCHIATRIST;
    }

    /**
     * FR9: Physicians can confirm and manage physical consultations. Patients can create or cancel their own.
     */
    public static boolean canManagePhysicianAppointments(UserRole role) {
        return role == UserRole.PHYSICIAN;
    }

    /**
     * FR10: Only designated Administrators can create, edit, or delete educational library resources.
     */
    public static boolean canManageEducationalContent(UserRole role) {
        return role == UserRole.ADMIN;
    }

    /**
     * Throws SecurityException if the role lacks authorization for clinical recovery updates.
     */
    public static void requirePhysician(UserRole role) {
        if (!canModifyRecoveryRecord(role)) {
            throw new SecurityException("Unauthorized: Clinical recovery records can only be updated by a Physician.");
        }
    }

    /**
     * Throws SecurityException if the role lacks authorization for content curation.
     */
    public static void requireAdmin(UserRole role) {
        if (!canManageEducationalContent(role)) {
            throw new SecurityException("Unauthorized: Educational library content can only be modified by an Administrator.");
        }
    }

    /**
     * Throws SecurityException if the role lacks authorization for psychiatric queue management.
     */
    public static void requirePsychiatrist(UserRole role) {
        if (!canConductPsychiatristSessions(role)) {
            throw new SecurityException("Unauthorized: Consultation sessions are restricted to licensed Psychiatrists.");
        }
    }
}
