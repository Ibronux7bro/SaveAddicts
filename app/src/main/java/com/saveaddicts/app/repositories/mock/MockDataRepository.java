package com.saveaddicts.app.repositories.mock;

import android.os.Handler;
import android.os.Looper;

import com.saveaddicts.app.models.*;
import com.saveaddicts.app.repositories.*;
import com.saveaddicts.app.security.AccessControl;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * In-memory development repository implementing all application interfaces.
 * Pre-seeded with realistic data to evaluate FR1 through FR10 without requiring a live Firebase connection.
 */
public class MockDataRepository implements
        AuthRepository,
        AppointmentRepository,
        RecoveryRepository,
        MessagingRepository,
        EducationalRepository,
        UserRepository,
        SessionRepository {

    private static volatile MockDataRepository INSTANCE;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    // In-memory data structures
    private final Map<String, UserProfile> usersByEmail = new ConcurrentHashMap<>();
    private final Map<String, UserProfile> usersByUid = new ConcurrentHashMap<>();
    private final Map<String, String> passwordsByEmail = new ConcurrentHashMap<>();

    private final List<RecoveryRecord> recoveryRecords = new CopyOnWriteArrayList<>();
    private final List<Appointment> appointments = new CopyOnWriteArrayList<>();
    private final List<ChatMessage> messages = new CopyOnWriteArrayList<>();
    private final List<Conversation> conversations = new CopyOnWriteArrayList<>();
    private final List<EducationalResource> educationalResources = new CopyOnWriteArrayList<>();
    private final List<ConsultationSession> consultationSessions = new CopyOnWriteArrayList<>();

    private UserProfile currentSessionUser;

    public static synchronized MockDataRepository getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MockDataRepository();
        }
        return INSTANCE;
    }

    private MockDataRepository() {
        seedInitialData();
    }

    private void seedInitialData() {
        long now = System.currentTimeMillis();
        long dayMs = 24L * 60 * 60 * 1000;

        // 1. Seed Users (All 4 roles)
        UserProfile patient1 = new UserProfile("pat_1", "HopeSeeker42", "patient@saveaddicts.org",
                UserRole.PATIENT, SubstanceType.OPIOIDS, now - 35 * dayMs);
        usersByEmail.put(patient1.getEmail().toLowerCase(), patient1);
        usersByUid.put(patient1.getUid(), patient1);
        passwordsByEmail.put(patient1.getEmail().toLowerCase(), "password123");

        UserProfile patient2 = new UserProfile("pat_2", "NewDawn", "patient2@saveaddicts.org",
                UserRole.PATIENT, SubstanceType.ALCOHOL, now - 10 * dayMs);
        usersByEmail.put(patient2.getEmail().toLowerCase(), patient2);
        usersByUid.put(patient2.getUid(), patient2);
        passwordsByEmail.put(patient2.getEmail().toLowerCase(), "password123");

        UserProfile psychiatrist = new UserProfile("psych_1", "Dr. Sarah Al-Sabah", "psychiatrist@saveaddicts.org",
                UserRole.PSYCHIATRIST, SubstanceType.ALL, now - 180 * dayMs);
        usersByEmail.put(psychiatrist.getEmail().toLowerCase(), psychiatrist);
        usersByUid.put(psychiatrist.getUid(), psychiatrist);
        passwordsByEmail.put(psychiatrist.getEmail().toLowerCase(), "password123");

        UserProfile physician = new UserProfile("phys_1", "Dr. Ahmad Mansour", "physician@saveaddicts.org",
                UserRole.PHYSICIAN, SubstanceType.ALL, now - 200 * dayMs);
        usersByEmail.put(physician.getEmail().toLowerCase(), physician);
        usersByUid.put(physician.getUid(), physician);
        passwordsByEmail.put(physician.getEmail().toLowerCase(), "password123");

        UserProfile admin = new UserProfile("admin_1", "System Administrator", "admin@saveaddicts.org",
                UserRole.ADMIN, SubstanceType.ALL, now - 365 * dayMs);
        usersByEmail.put(admin.getEmail().toLowerCase(), admin);
        usersByUid.put(admin.getUid(), admin);
        passwordsByEmail.put(admin.getEmail().toLowerCase(), "password123");

        // Default login starts as patient1 for immediate user evaluation
        this.currentSessionUser = patient1;

        // 2. Seed Dated Recovery Records (Optimism Graph for pat_1)
        recoveryRecords.add(new RecoveryRecord("rec_1", "pat_1", "phys_1", "Dr. Ahmad Mansour",
                30, HealthStatus.CRITICAL, "Intake clinical evaluation. Patient initiated stabilization protocol.", now - 28 * dayMs));
        recoveryRecords.add(new RecoveryRecord("rec_2", "pat_1", "phys_1", "Dr. Ahmad Mansour",
                48, HealthStatus.MONITORING, "Withdrawal managed safely. Patient adhering to medical schedule.", now - 21 * dayMs));
        recoveryRecords.add(new RecoveryRecord("rec_3", "pat_1", "phys_1", "Dr. Ahmad Mansour",
                65, HealthStatus.STABLE, "Vital signs regularized. Positive participation in psychiatric counseling.", now - 14 * dayMs));
        recoveryRecords.add(new RecoveryRecord("rec_4", "pat_1", "phys_1", "Dr. Ahmad Mansour",
                82, HealthStatus.IMPROVING, "Outstanding progress. Patient demonstrates psychological resilience and zero lapse.", now - 7 * dayMs));

        // 3. Seed Appointments (FR4 & FR9)
        appointments.add(new Appointment("apt_1", "pat_1", "HopeSeeker42", "phys_1", "Dr. Ahmad Mansour",
                "2026-10-25", "10:00 AM - 10:30 AM", AppointmentStatus.CONFIRMED,
                "Routine in-person clinical checkup & toxicology screening.", now - 2 * dayMs));
        appointments.add(new Appointment("apt_2", "pat_1", "HopeSeeker42", "phys_1", "Dr. Ahmad Mansour",
                "2026-11-02", "02:00 PM - 02:30 PM", AppointmentStatus.PENDING,
                "Nutritional recovery and physical wellness evaluation.", now - 1 * dayMs));
        appointments.add(new Appointment("apt_3", "pat_2", "NewDawn", "phys_1", "Dr. Ahmad Mansour",
                "2026-10-28", "11:30 AM - 12:00 PM", AppointmentStatus.PENDING,
                "Initial consultation for alcohol dependency management.", now));

        // 4. Seed Messages & Conversations (FR3)
        String convId = "conv_pat1_psych1";
        conversations.add(new Conversation(convId, "pat_1", "HopeSeeker42",
                "psych_1", "Dr. Sarah Al-Sabah",
                "Yes, the breathing exercises really helped reduce the panic urge. Thank you doctor.",
                now - 2 * 3600 * 1000, 0));

        messages.add(new ChatMessage("msg_1", convId, "pat_1", "HopeSeeker42", UserRole.PATIENT,
                "psych_1", "ENC:DEMO:SGVsbG8gRG9jdG9yLCBJIHdhcyBmZWVsaW5nIGFuIGludGVuc2UgdXJnZSB0b2RheSBkdWUgdG8gd29yayBzdHJlc3Mu",
                "Hello Doctor, I was feeling an intense urge today due to work stress.", now - 4 * 3600 * 1000));
        messages.add(new ChatMessage("msg_2", convId, "psych_1", "Dr. Sarah Al-Sabah", UserRole.PSYCHIATRIST,
                "pat_1", "ENC:DEMO:SGVsbG8gSG9wZVNlZWtlci4gVGhhdCBpcyBjb21wbGV0ZWx5IG5vcm1hbC4gUGxlYXNlIHJlY2FsbCBvdXIgY29nbml0aXZlIHJlZnJhbWluZyB0ZWNobmlxdWUu",
                "Hello HopeSeeker. That is completely normal. Please recall our cognitive reframing technique and take a 10-minute break.", now - 3 * 3600 * 1000));
        messages.add(new ChatMessage("msg_3", convId, "pat_1", "HopeSeeker42", UserRole.PATIENT,
                "psych_1", "ENC:DEMO:WWVzLCB0aGUgYnJlYXRoaW5nIGV4ZXJjaXNlcyByZWFsbHkgaGVscGVkIHJlZHVjZSB0aGUgcGFuaWMgdXJnZS4gVGhhbmsgeW91IGRvY3Rvci4=",
                "Yes, the breathing exercises really helped reduce the panic urge. Thank you doctor.", now - 2 * 3600 * 1000));

        // 5. Seed Sessions Queue (FR7)
        consultationSessions.add(new ConsultationSession("sess_1", "pat_1", "HopeSeeker42", "psych_1", "Dr. Sarah Al-Sabah",
                SubstanceType.OPIOIDS, SessionStatus.ACCEPTED, "Ongoing psychiatric guidance.", now - 5 * dayMs));
        consultationSessions.add(new ConsultationSession("sess_2", "pat_2", "NewDawn", "psych_1", "Dr. Sarah Al-Sabah",
                SubstanceType.ALCOHOL, SessionStatus.REQUESTED, "Urgent request for initial psychological consultation.", now - 1 * dayMs));

        // 6. Seed Educational Resources (FR6 & FR10)
        educationalResources.add(new EducationalResource(
                "res_1",
                "Understanding Opioid Neurochemistry & Healing",
                "A scientific overview explaining how opioid receptors reset during recovery and methods to cope with cravings.",
                ResourceType.VIDEO,
                SubstanceType.OPIOIDS,
                AgeGroup.ADULT,
                "GCC & International Clinical Context",
                "Addiction Recovery Institute",
                "In this comprehensive video lecture, clinical neuroscientists discuss how endorphin production stabilizes over months of abstinence, validating the patient's emotional journey.",
                true,
                now - 20 * dayMs
        ));

        educationalResources.add(new EducationalResource(
                "res_2",
                "Walking Away from Alcohol: A Stigma-Free Guide",
                "Practical handbook for overcoming alcohol dependency while maintaining privacy in social and familial environments.",
                ResourceType.BOOK,
                SubstanceType.ALCOHOL,
                AgeGroup.ALL,
                "Arabic & English Community Context",
                "Community Health & Wellness Foundation",
                "Chapter 1 outlines the social triggers common in family gatherings and offers scripted boundaries for declining drinks without disclosing private medical recovery.",
                true,
                now - 15 * dayMs
        ));

        educationalResources.add(new EducationalResource(
                "res_3",
                "Cannabinoid Receptors and Youth Cognitive Recovery",
                "Empirical study on how the teenage and young adult brain repairs executive functioning after cannabis cessation.",
                ResourceType.ARTICLE,
                SubstanceType.CANNABIS,
                AgeGroup.YOUTH,
                "Youth Mental Health Initiative",
                "Dr. Leila Haddad",
                "Research demonstrates that sleep quality, working memory, and motivation metrics show marked improvement within 21 days of cessation.",
                true,
                now - 12 * dayMs
        ));

        educationalResources.add(new EducationalResource(
                "res_4",
                "Confidential Family Support & Breaking the Stigma",
                "How families can provide an empathetic, non-punitive support system for loved ones undergoing addiction rehabilitation.",
                ResourceType.ARTICLE,
                SubstanceType.ALL,
                AgeGroup.ALL,
                "GCC Cultural Values & Family Care",
                "Dr. L. Shalaby & Educators",
                "Stigma remains the greatest barrier to recovery. By fostering privacy, patience, and professional medical partnership, recovery rates significantly improve.",
                true,
                now - 10 * dayMs
        ));

        educationalResources.add(new EducationalResource(
                "res_5",
                "Mindfulness, Breathwork, and Relapse Prevention",
                "Guided audio-visual relaxation module to reduce cortisol and manage acute impulse moments.",
                ResourceType.VIDEO,
                SubstanceType.ALL,
                AgeGroup.ADULT,
                "Universal",
                "Holistic Mental Health Center",
                "Step-by-step 4-7-8 breathing and sensory grounding exercises designed to halt neurological flight-or-fight distress.",
                true,
                now - 8 * dayMs
        ));

        educationalResources.add(new EducationalResource(
                "res_6",
                "Seniors & Prescription Medication Safety",
                "Understanding the subtle emergence of opioid dependence in chronic pain management among senior citizens.",
                ResourceType.BOOK,
                SubstanceType.OPIOIDS,
                AgeGroup.SENIOR,
                "Elderly Wellness Care",
                "Geriatric Care Council",
                "Addresses physical dependency versus addiction, dosage tapering strategies, and non-pharmacological pain relief methods.",
                true,
                now - 5 * dayMs
        ));
    }

    // ==================== AuthRepository ====================

    @Override
    public void login(String email, String password, Callback<UserProfile> callback) {
        postAsync(() -> {
            if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                callback.onError(new IllegalArgumentException("Please enter your email and password."));
                return;
            }
            String cleanEmail = email.trim().toLowerCase();
            UserProfile user = usersByEmail.get(cleanEmail);
            String storedPassword = passwordsByEmail.get(cleanEmail);

            if (user != null && storedPassword != null && storedPassword.equals(password)) {
                this.currentSessionUser = user;
                callback.onSuccess(user);
            } else {
                callback.onError(new IllegalStateException("Invalid email or password. Please verify your credentials."));
            }
        });
    }

    @Override
    public void register(String pseudonym, String email, String password, UserRole role, Callback<UserProfile> callback) {
        postAsync(() -> {
            if (pseudonym == null || pseudonym.trim().length() < 3) {
                callback.onError(new IllegalArgumentException("Pseudonym must be at least 3 characters long."));
                return;
            }
            if (email == null || !email.contains("@")) {
                callback.onError(new IllegalArgumentException("Please enter a valid email address."));
                return;
            }
            if (password == null || password.length() < 6) {
                callback.onError(new IllegalArgumentException("Password must be at least 6 characters long."));
                return;
            }

            String cleanEmail = email.trim().toLowerCase();
            if (usersByEmail.containsKey(cleanEmail)) {
                callback.onError(new IllegalStateException("An account with this email already exists."));
                return;
            }

            String newUid = "usr_" + UUID.randomUUID().toString().substring(0, 8);
            UserProfile newUser = new UserProfile(newUid, pseudonym.trim(), cleanEmail,
                    role != null ? role : UserRole.PATIENT, SubstanceType.ALL, System.currentTimeMillis());

            usersByEmail.put(cleanEmail, newUser);
            usersByUid.put(newUid, newUser);
            passwordsByEmail.put(cleanEmail, password);

            this.currentSessionUser = newUser;
            callback.onSuccess(newUser);
        });
    }

    @Override
    public void logout() {
        this.currentSessionUser = null;
    }

    @Override
    public UserProfile getCurrentUser() {
        return currentSessionUser;
    }

    @Override
    public boolean isAuthenticated() {
        return currentSessionUser != null;
    }

    @Override
    public void updateSubstancePreference(SubstanceType substanceType, Callback<Void> callback) {
        postAsync(() -> {
            if (currentSessionUser != null) {
                currentSessionUser.setSubstancePreference(substanceType != null ? substanceType : SubstanceType.ALL);
                callback.onSuccess(null);
            } else {
                callback.onError(new IllegalStateException("No active user session."));
            }
        });
    }

    // ==================== RecoveryRepository (FR5 & FR8) ====================

    @Override
    public void getRecoveryRecordsForPatient(String patientId, Callback<List<RecoveryRecord>> callback) {
        postAsync(() -> {
            List<RecoveryRecord> result = new ArrayList<>();
            for (RecoveryRecord r : recoveryRecords) {
                if (r.getPatientId().equals(patientId)) {
                    result.add(r);
                }
            }
            Collections.sort(result, Comparator.comparingLong(RecoveryRecord::getTimestamp));
            callback.onSuccess(result);
        });
    }

    @Override
    public void addRecoveryRecord(RecoveryRecord record, Callback<RecoveryRecord> callback) {
        postAsync(() -> {
            if (currentSessionUser == null || !AccessControl.canModifyRecoveryRecord(currentSessionUser.getRole())) {
                callback.onError(new SecurityException("Only licensed Physicians may update clinical recovery records."));
                return;
            }
            if (record.getId() == null || record.getId().isEmpty()) {
                record.setId("rec_" + UUID.randomUUID().toString().substring(0, 8));
            }
            if (record.getTimestamp() <= 0) {
                record.setTimestamp(System.currentTimeMillis());
            }
            record.setClinicianId(currentSessionUser.getUid());
            record.setClinicianName(currentSessionUser.getPseudonym());

            recoveryRecords.add(record);
            callback.onSuccess(record);
        });
    }

    @Override
    public void getLatestHealthStatus(String patientId, Callback<HealthStatus> callback) {
        postAsync(() -> {
            RecoveryRecord latest = null;
            for (RecoveryRecord r : recoveryRecords) {
                if (r.getPatientId().equals(patientId)) {
                    if (latest == null || r.getTimestamp() > latest.getTimestamp()) {
                        latest = r;
                    }
                }
            }
            callback.onSuccess(latest != null ? latest.getHealthStatus() : HealthStatus.STABLE);
        });
    }

    // ==================== AppointmentRepository (FR4 & FR9) ====================

    @Override
    public void getAppointmentsForPatient(String patientId, Callback<List<Appointment>> callback) {
        postAsync(() -> {
            List<Appointment> result = new ArrayList<>();
            for (Appointment a : appointments) {
                if (a.getPatientId().equals(patientId)) {
                    result.add(a);
                }
            }
            Collections.sort(result, (a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()));
            callback.onSuccess(result);
        });
    }

    @Override
    public void getAppointmentsForPhysician(String physicianId, Callback<List<Appointment>> callback) {
        postAsync(() -> {
            List<Appointment> result = new ArrayList<>();
            for (Appointment a : appointments) {
                if (a.getPhysicianId().equals(physicianId)) {
                    result.add(a);
                }
            }
            Collections.sort(result, (a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()));
            callback.onSuccess(result);
        });
    }

    @Override
    public void getAppointmentById(String appointmentId, Callback<Appointment> callback) {
        postAsync(() -> {
            for (Appointment a : appointments) {
                if (a.getId().equals(appointmentId)) {
                    callback.onSuccess(a);
                    return;
                }
            }
            callback.onError(new NoSuchElementException("Appointment not found."));
        });
    }

    @Override
    public void createAppointment(Appointment appointment, Callback<Appointment> callback) {
        postAsync(() -> {
            // Check for booking conflict on same physician + date + timeSlot
            for (Appointment existing : appointments) {
                if (existing.getPhysicianId().equals(appointment.getPhysicianId())
                        && existing.getDate().equals(appointment.getDate())
                        && existing.getTimeSlot().equalsIgnoreCase(appointment.getTimeSlot())
                        && existing.getStatus() != AppointmentStatus.CANCELLED) {
                    callback.onError(new IllegalStateException("This time slot is already reserved. Please select another slot."));
                    return;
                }
            }

            if (appointment.getId() == null || appointment.getId().isEmpty()) {
                appointment.setId("apt_" + UUID.randomUUID().toString().substring(0, 8));
            }
            if (appointment.getCreatedAt() <= 0) {
                appointment.setCreatedAt(System.currentTimeMillis());
            }
            appointment.setStatus(AppointmentStatus.PENDING);
            appointments.add(appointment);
            callback.onSuccess(appointment);
        });
    }

    @Override
    public void updateAppointmentStatus(String appointmentId, AppointmentStatus status, Callback<Void> callback) {
        postAsync(() -> {
            for (Appointment a : appointments) {
                if (a.getId().equals(appointmentId)) {
                    a.setStatus(status);
                    callback.onSuccess(null);
                    return;
                }
            }
            callback.onError(new NoSuchElementException("Appointment not found."));
        });
    }

    @Override
    public void cancelAppointment(String appointmentId, String reason, Callback<Void> callback) {
        postAsync(() -> {
            for (Appointment a : appointments) {
                if (a.getId().equals(appointmentId)) {
                    a.setStatus(AppointmentStatus.CANCELLED);
                    a.setCancellationReason(reason);
                    callback.onSuccess(null);
                    return;
                }
            }
            callback.onError(new NoSuchElementException("Appointment not found."));
        });
    }

    @Override
    public void getAvailableTimeSlots(String physicianId, String date, Callback<List<String>> callback) {
        postAsync(() -> {
            List<String> standardSlots = Arrays.asList(
                    "09:00 AM - 09:30 AM",
                    "10:00 AM - 10:30 AM",
                    "11:00 AM - 11:30 AM",
                    "01:00 PM - 01:30 PM",
                    "02:00 PM - 02:30 PM",
                    "03:00 PM - 03:30 PM"
            );
            Set<String> takenSlots = new HashSet<>();
            for (Appointment a : appointments) {
                if (a.getPhysicianId().equals(physicianId) && a.getDate().equals(date) && a.getStatus() != AppointmentStatus.CANCELLED) {
                    takenSlots.add(a.getTimeSlot());
                }
            }
            List<String> available = new ArrayList<>();
            for (String slot : standardSlots) {
                if (!takenSlots.contains(slot)) {
                    available.add(slot);
                }
            }
            callback.onSuccess(available);
        });
    }

    // ==================== MessagingRepository (FR3) ====================

    @Override
    public void getConversationsForUser(String userId, Callback<List<Conversation>> callback) {
        postAsync(() -> {
            List<Conversation> result = new ArrayList<>();
            for (Conversation c : conversations) {
                if (c.getPatientId().equals(userId) || c.getPsychiatristId().equals(userId)) {
                    result.add(c);
                }
            }
            Collections.sort(result, (a, b) -> Long.compare(b.getLastMessageTimestamp(), a.getLastMessageTimestamp()));
            callback.onSuccess(result);
        });
    }

    @Override
    public void getMessages(String conversationId, Callback<List<ChatMessage>> callback) {
        postAsync(() -> {
            List<ChatMessage> result = new ArrayList<>();
            for (ChatMessage m : messages) {
                if (m.getConversationId().equals(conversationId)) {
                    result.add(m);
                }
            }
            Collections.sort(result, Comparator.comparingLong(ChatMessage::getTimestamp));
            callback.onSuccess(result);
        });
    }

    @Override
    public void sendMessage(ChatMessage message, Callback<ChatMessage> callback) {
        postAsync(() -> {
            if (message.getId() == null || message.getId().isEmpty()) {
                message.setId("msg_" + UUID.randomUUID().toString().substring(0, 8));
            }
            if (message.getTimestamp() <= 0) {
                message.setTimestamp(System.currentTimeMillis());
            }
            messages.add(message);

            // Update conversation snippet
            for (Conversation c : conversations) {
                if (c.getId().equals(message.getConversationId())) {
                    c.setLastMessageText(message.getMessageText());
                    c.setLastMessageTimestamp(message.getTimestamp());
                    break;
                }
            }
            callback.onSuccess(message);
        });
    }

    @Override
    public void getOrCreateConversation(String patientId, String patientPseudonym,
                                        String psychiatristId, String psychiatristName,
                                        Callback<Conversation> callback) {
        postAsync(() -> {
            for (Conversation c : conversations) {
                if (c.getPatientId().equals(patientId) && c.getPsychiatristId().equals(psychiatristId)) {
                    callback.onSuccess(c);
                    return;
                }
            }
            String newId = "conv_" + UUID.randomUUID().toString().substring(0, 8);
            Conversation newConv = new Conversation(newId, patientId, patientPseudonym,
                    psychiatristId, psychiatristName, "Session initialized", System.currentTimeMillis(), 0);
            conversations.add(newConv);
            callback.onSuccess(newConv);
        });
    }

    // ==================== EducationalRepository (FR6 & FR10) ====================

    @Override
    public void getFilteredResources(SubstanceType substanceFilter, ResourceType typeFilter, AgeGroup ageFilter, Callback<List<EducationalResource>> callback) {
        postAsync(() -> {
            List<EducationalResource> result = new ArrayList<>();
            for (EducationalResource r : educationalResources) {
                boolean matchSubstance = (substanceFilter == null || substanceFilter == SubstanceType.ALL
                        || r.getSubstanceType() == SubstanceType.ALL || r.getSubstanceType() == substanceFilter);
                boolean matchType = (typeFilter == null || r.getResourceType() == typeFilter);
                boolean matchAge = (ageFilter == null || ageFilter == AgeGroup.ALL
                        || r.getAgeGroup() == AgeGroup.ALL || r.getAgeGroup() == ageFilter);

                if (matchSubstance && matchType && matchAge) {
                    result.add(r);
                }
            }
            callback.onSuccess(result);
        });
    }

    @Override
    public void getAllResources(Callback<List<EducationalResource>> callback) {
        postAsync(() -> callback.onSuccess(new ArrayList<>(educationalResources)));
    }

    @Override
    public void getResourceById(String resourceId, Callback<EducationalResource> callback) {
        postAsync(() -> {
            for (EducationalResource r : educationalResources) {
                if (r.getId().equals(resourceId)) {
                    callback.onSuccess(r);
                    return;
                }
            }
            callback.onError(new NoSuchElementException("Resource not found."));
        });
    }

    @Override
    public void addResource(EducationalResource resource, Callback<EducationalResource> callback) {
        postAsync(() -> {
            if (currentSessionUser == null || !AccessControl.canManageEducationalContent(currentSessionUser.getRole())) {
                callback.onError(new SecurityException("Only Administrators can add educational resources."));
                return;
            }
            if (resource.getId() == null || resource.getId().isEmpty()) {
                resource.setId("res_" + UUID.randomUUID().toString().substring(0, 8));
            }
            resource.setCreatedAt(System.currentTimeMillis());
            resource.setSampleContent(true); // Sample until officially medically reviewed
            educationalResources.add(resource);
            callback.onSuccess(resource);
        });
    }

    @Override
    public void updateResource(EducationalResource resource, Callback<Void> callback) {
        postAsync(() -> {
            if (currentSessionUser == null || !AccessControl.canManageEducationalContent(currentSessionUser.getRole())) {
                callback.onError(new SecurityException("Only Administrators can update educational resources."));
                return;
            }
            for (int i = 0; i < educationalResources.size(); i++) {
                if (educationalResources.get(i).getId().equals(resource.getId())) {
                    educationalResources.set(i, resource);
                    callback.onSuccess(null);
                    return;
                }
            }
            callback.onError(new NoSuchElementException("Resource not found."));
        });
    }

    @Override
    public void deleteResource(String resourceId, Callback<Void> callback) {
        postAsync(() -> {
            if (currentSessionUser == null || !AccessControl.canManageEducationalContent(currentSessionUser.getRole())) {
                callback.onError(new SecurityException("Only Administrators can delete educational resources."));
                return;
            }
            educationalResources.removeIf(r -> r.getId().equals(resourceId));
            callback.onSuccess(null);
        });
    }

    // ==================== UserRepository ====================

    @Override
    public void getUsersByRole(UserRole role, Callback<List<UserProfile>> callback) {
        postAsync(() -> {
            List<UserProfile> result = new ArrayList<>();
            for (UserProfile u : usersByUid.values()) {
                if (u.getRole() == role) {
                    result.add(u);
                }
            }
            callback.onSuccess(result);
        });
    }

    @Override
    public void getUserById(String uid, Callback<UserProfile> callback) {
        postAsync(() -> {
            UserProfile u = usersByUid.get(uid);
            if (u != null) {
                callback.onSuccess(u);
            } else {
                callback.onError(new NoSuchElementException("User not found: " + uid));
            }
        });
    }

    // ==================== SessionRepository (FR7) ====================

    @Override
    public void getSessionsForPsychiatrist(String psychiatristId, Callback<List<ConsultationSession>> callback) {
        postAsync(() -> {
            List<ConsultationSession> result = new ArrayList<>();
            for (ConsultationSession s : consultationSessions) {
                if (s.getPsychiatristId() != null && s.getPsychiatristId().equals(psychiatristId)) {
                    result.add(s);
                }
            }
            callback.onSuccess(result);
        });
    }

    @Override
    public void getPendingQueue(Callback<List<ConsultationSession>> callback) {
        postAsync(() -> {
            List<ConsultationSession> result = new ArrayList<>();
            for (ConsultationSession s : consultationSessions) {
                if (s.getStatus() == SessionStatus.REQUESTED) {
                    result.add(s);
                }
            }
            callback.onSuccess(result);
        });
    }

    @Override
    public void requestSession(ConsultationSession session, Callback<ConsultationSession> callback) {
        postAsync(() -> {
            if (session.getId() == null || session.getId().isEmpty()) {
                session.setId("sess_" + UUID.randomUUID().toString().substring(0, 8));
            }
            session.setRequestedAt(System.currentTimeMillis());
            session.setStatus(SessionStatus.REQUESTED);
            consultationSessions.add(session);
            callback.onSuccess(session);
        });
    }

    @Override
    public void updateSessionStatus(String sessionId, SessionStatus status, Callback<Void> callback) {
        postAsync(() -> {
            for (ConsultationSession s : consultationSessions) {
                if (s.getId().equals(sessionId)) {
                    s.setStatus(status);
                    callback.onSuccess(null);
                    return;
                }
            }
            callback.onError(new NoSuchElementException("Session not found."));
        });
    }

    private void postAsync(Runnable task) {
        new Thread(() -> {
            try {
                Thread.sleep(120); // Simulate realistic light async latency
            } catch (InterruptedException ignored) {}
            mainHandler.post(task);
        }).start();
    }
}
