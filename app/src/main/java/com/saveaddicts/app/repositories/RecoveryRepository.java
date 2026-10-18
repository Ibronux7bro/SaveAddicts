package com.saveaddicts.app.repositories;

import com.saveaddicts.app.models.HealthStatus;
import com.saveaddicts.app.models.RecoveryRecord;

import java.util.List;

/**
 * Repository interface for recovery records and Optimism Graph data (FR5 & FR8).
 * Note: Modification is restricted exclusively to authorized clinical roles.
 */
public interface RecoveryRepository {
    void getRecoveryRecordsForPatient(String patientId, Callback<List<RecoveryRecord>> callback);

    void addRecoveryRecord(RecoveryRecord record, Callback<RecoveryRecord> callback);

    void getLatestHealthStatus(String patientId, Callback<HealthStatus> callback);
}
