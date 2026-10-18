package com.saveaddicts.app;

import com.saveaddicts.app.models.UserRole;
import com.saveaddicts.app.security.AccessControl;

import org.junit.Test;

import static org.junit.Assert.*;

public class AccessControlTest {

    @Test
    public void testPhysicianRecoveryRecordPermissions() {
        // FR8: Only Physician is permitted to update clinical recovery records and Optimism Graph
        assertTrue("Physician must be allowed to modify recovery records",
                AccessControl.canModifyRecoveryRecord(UserRole.PHYSICIAN));
        assertFalse("Patient must not be allowed to modify recovery records",
                AccessControl.canModifyRecoveryRecord(UserRole.PATIENT));
        assertFalse("Admin must not be allowed to modify recovery records",
                AccessControl.canModifyRecoveryRecord(UserRole.ADMIN));
        assertFalse("Psychiatrist must not be allowed to modify physical recovery records",
                AccessControl.canModifyRecoveryRecord(UserRole.PSYCHIATRIST));
    }

    @Test
    public void testAdminEducationalPermissions() {
        // FR10: Only Admin is permitted to curate educational library content
        assertTrue("Admin must be allowed to manage educational content",
                AccessControl.canManageEducationalContent(UserRole.ADMIN));
        assertFalse("Patient must not manage educational content",
                AccessControl.canManageEducationalContent(UserRole.PATIENT));
        assertFalse("Physician must not curate administrative content",
                AccessControl.canManageEducationalContent(UserRole.PHYSICIAN));
        assertFalse("Psychiatrist must not curate administrative content",
                AccessControl.canManageEducationalContent(UserRole.PSYCHIATRIST));
    }

    @Test
    public void testPsychiatristSessionPermissions() {
        // FR7: Only Psychiatrist can conduct psychiatric consultation sessions
        assertTrue("Psychiatrist must be allowed to manage session queue",
                AccessControl.canConductPsychiatristSessions(UserRole.PSYCHIATRIST));
        assertFalse("Patient cannot manage session queue",
                AccessControl.canConductPsychiatristSessions(UserRole.PATIENT));
        assertFalse("Physician cannot manage psychiatric queue",
                AccessControl.canConductPsychiatristSessions(UserRole.PHYSICIAN));
        assertFalse("Admin cannot manage psychiatric queue",
                AccessControl.canConductPsychiatristSessions(UserRole.ADMIN));
    }

    @Test(expected = SecurityException.class)
    public void testRequirePhysicianThrowsForPatient() {
        AccessControl.requirePhysician(UserRole.PATIENT);
    }

    @Test(expected = SecurityException.class)
    public void testRequireAdminThrowsForPhysician() {
        AccessControl.requireAdmin(UserRole.PHYSICIAN);
    }
}
