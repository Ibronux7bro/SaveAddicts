package com.saveaddicts.app;

import com.saveaddicts.app.models.*;

import org.junit.Test;

import static org.junit.Assert.*;

public class ModelValidationTest {

    @Test
    public void testUserProfilePseudonymIntegrity() {
        // FR1: No real names required; pseudonym fallback
        UserProfile profile = new UserProfile("uid_test", null, "test@saveaddicts.org",
                UserRole.PATIENT, SubstanceType.OPIOIDS, 1000L);
        assertEquals("Anonymous", profile.getPseudonym());
        assertEquals("AN", profile.getInitials());

        profile.setPseudonym("HopeSeeker");
        assertEquals("HO", profile.getInitials());
    }

    @Test
    public void testRecoveryRecordScoreClamping() {
        // FR5 & FR8: Optimism score is bounded between 0% and 100%
        RecoveryRecord recordHigh = new RecoveryRecord("id_1", "pat_1", "phys_1", "Dr. Ahmad",
                150, HealthStatus.STABLE, "Excellent", 2000L);
        assertEquals(100, recordHigh.getOptimismScore());

        RecoveryRecord recordLow = new RecoveryRecord("id_2", "pat_1", "phys_1", "Dr. Ahmad",
                -20, HealthStatus.CRITICAL, "Struggling", 2000L);
        assertEquals(0, recordLow.getOptimismScore());
    }

    @Test
    public void testSubstanceTypeParsing() {
        // FR2: Supported substance categories
        assertEquals(SubstanceType.ALCOHOL, SubstanceType.fromString("alcohol"));
        assertEquals(SubstanceType.OPIOIDS, SubstanceType.fromString("OPIOIDS"));
        assertEquals(SubstanceType.CANNABIS, SubstanceType.fromString("Cannabis"));
        assertEquals(SubstanceType.OTHER, SubstanceType.fromString("other"));
        assertEquals(SubstanceType.ALL, SubstanceType.fromString("unknown_val"));
    }

    @Test
    public void testEducationalResourceSampleLabeling() {
        // FR6 & FR10: Explicit sample content labeling
        EducationalResource resource = new EducationalResource("res_1", "Sample Video", "Summary",
                ResourceType.VIDEO, SubstanceType.ALCOHOL, AgeGroup.ADULT,
                "Universal", "Dr. Educator", "Body", true, 3000L);

        assertTrue("Sample content must be clearly flagged", resource.isSampleContent());
        assertEquals(ResourceType.VIDEO, resource.getResourceType());
        assertEquals(SubstanceType.ALCOHOL, resource.getSubstanceType());
    }
}
