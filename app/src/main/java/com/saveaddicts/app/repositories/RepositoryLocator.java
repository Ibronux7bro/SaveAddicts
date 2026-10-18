package com.saveaddicts.app.repositories;

import com.saveaddicts.app.repositories.firebase.FirebaseAuthRepository;
import com.saveaddicts.app.repositories.firebase.FirebaseRealtimeRepository;
import com.saveaddicts.app.repositories.mock.MockDataRepository;
import com.saveaddicts.app.security.DemoEncryptionService;
import com.saveaddicts.app.security.EncryptionService;

/**
 * Service Locator maintaining the application's active repository implementations.
 * Supports seamless switching between local Mock evaluation repositories and live Firebase services.
 */
public final class RepositoryLocator {

    private static AuthRepository authRepository;
    private static AppointmentRepository appointmentRepository;
    private static RecoveryRepository recoveryRepository;
    private static MessagingRepository messagingRepository;
    private static EducationalRepository educationalRepository;
    private static UserRepository userRepository;
    private static SessionRepository sessionRepository;
    private static EncryptionService encryptionService;

    private static boolean isFirebaseConnected = false;

    private RepositoryLocator() {
        // Utility
    }

    public static synchronized void enableFirebaseBackend() {
        isFirebaseConnected = true;
        authRepository = new FirebaseAuthRepository();
        FirebaseRealtimeRepository realtime = new FirebaseRealtimeRepository();
        appointmentRepository = realtime;
        recoveryRepository = realtime;
        messagingRepository = realtime;
        educationalRepository = realtime;
        userRepository = realtime;
        sessionRepository = realtime;
    }

    public static synchronized void enableMockBackend() {
        isFirebaseConnected = false;
        authRepository = MockDataRepository.getInstance();
        appointmentRepository = MockDataRepository.getInstance();
        recoveryRepository = MockDataRepository.getInstance();
        messagingRepository = MockDataRepository.getInstance();
        educationalRepository = MockDataRepository.getInstance();
        userRepository = MockDataRepository.getInstance();
        sessionRepository = MockDataRepository.getInstance();
    }

    public static synchronized AuthRepository getAuthRepository() {
        if (authRepository == null) {
            authRepository = isFirebaseConnected ? new FirebaseAuthRepository() : MockDataRepository.getInstance();
        }
        return authRepository;
    }

    public static synchronized AppointmentRepository getAppointmentRepository() {
        if (appointmentRepository == null) {
            appointmentRepository = isFirebaseConnected ? new FirebaseRealtimeRepository() : MockDataRepository.getInstance();
        }
        return appointmentRepository;
    }

    public static synchronized RecoveryRepository getRecoveryRepository() {
        if (recoveryRepository == null) {
            recoveryRepository = isFirebaseConnected ? new FirebaseRealtimeRepository() : MockDataRepository.getInstance();
        }
        return recoveryRepository;
    }

    public static synchronized MessagingRepository getMessagingRepository() {
        if (messagingRepository == null) {
            messagingRepository = isFirebaseConnected ? new FirebaseRealtimeRepository() : MockDataRepository.getInstance();
        }
        return messagingRepository;
    }

    public static synchronized EducationalRepository getEducationalRepository() {
        if (educationalRepository == null) {
            educationalRepository = isFirebaseConnected ? new FirebaseRealtimeRepository() : MockDataRepository.getInstance();
        }
        return educationalRepository;
    }

    public static synchronized UserRepository getUserRepository() {
        if (userRepository == null) {
            userRepository = isFirebaseConnected ? new FirebaseRealtimeRepository() : MockDataRepository.getInstance();
        }
        return userRepository;
    }

    public static synchronized SessionRepository getSessionRepository() {
        if (sessionRepository == null) {
            sessionRepository = isFirebaseConnected ? new FirebaseRealtimeRepository() : MockDataRepository.getInstance();
        }
        return sessionRepository;
    }

    public static synchronized EncryptionService getEncryptionService() {
        if (encryptionService == null) {
            encryptionService = new DemoEncryptionService();
        }
        return encryptionService;
    }

    public static boolean isFirebaseConnected() {
        return isFirebaseConnected;
    }

    public static void setFirebaseConnected(boolean connected) {
        if (connected) {
            enableFirebaseBackend();
        } else {
            enableMockBackend();
        }
    }
}
