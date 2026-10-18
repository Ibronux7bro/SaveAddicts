package com.saveaddicts.app.repositories;

import com.saveaddicts.app.models.ConsultationSession;
import com.saveaddicts.app.models.SessionStatus;

import java.util.List;

/**
 * Repository interface for psychiatrist consultation queue and sessions (FR7).
 */
public interface SessionRepository {
    void getSessionsForPsychiatrist(String psychiatristId, Callback<List<ConsultationSession>> callback);

    void getPendingQueue(Callback<List<ConsultationSession>> callback);

    void requestSession(ConsultationSession session, Callback<ConsultationSession> callback);

    void updateSessionStatus(String sessionId, SessionStatus status, Callback<Void> callback);
}
