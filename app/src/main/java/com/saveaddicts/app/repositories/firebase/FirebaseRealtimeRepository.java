package com.saveaddicts.app.repositories.firebase;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.saveaddicts.app.models.*;
import com.saveaddicts.app.repositories.*;

import java.util.*;

/**
 * Concrete data access repository powered by Firebase Realtime Database.
 * Connects User profiles, Recovery records, Appointments, Messaging, and Library content.
 */
public class FirebaseRealtimeRepository implements
        AppointmentRepository,
        RecoveryRepository,
        MessagingRepository,
        EducationalRepository,
        UserRepository,
        SessionRepository {

    private final DatabaseReference rootRef;
    private final DatabaseReference usersRef;
    private final DatabaseReference recoveryRef;
    private final DatabaseReference appointmentsRef;
    private final DatabaseReference conversationsRef;
    private final DatabaseReference messagesRef;
    private final DatabaseReference educationalRef;
    private final DatabaseReference sessionsRef;

    public FirebaseRealtimeRepository() {
        FirebaseDatabase db = FirebaseDatabase.getInstance(FirebaseAuthRepository.DATABASE_URL);
        this.rootRef = db.getReference();
        this.usersRef = rootRef.child("users");
        this.recoveryRef = rootRef.child("recovery_records");
        this.appointmentsRef = rootRef.child("appointments");
        this.conversationsRef = rootRef.child("conversations");
        this.messagesRef = rootRef.child("messages");
        this.educationalRef = rootRef.child("educational_resources");
        this.sessionsRef = rootRef.child("consultation_sessions");
    }

    // ==================== AppointmentRepository ====================

    @Override
    public void getAppointmentsForPatient(String patientId, Callback<List<Appointment>> callback) {
        appointmentsRef.orderByChild("patientId").equalTo(patientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Appointment> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Appointment apt = child.getValue(Appointment.class);
                    if (apt != null) list.add(apt);
                }
                Collections.sort(list, (a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()));
                callback.onSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }

    @Override
    public void getAppointmentsForPhysician(String physicianId, Callback<List<Appointment>> callback) {
        appointmentsRef.orderByChild("physicianId").equalTo(physicianId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Appointment> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    Appointment apt = child.getValue(Appointment.class);
                    if (apt != null) list.add(apt);
                }
                Collections.sort(list, (a, b) -> Long.compare(b.getCreatedAt(), a.getCreatedAt()));
                callback.onSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }

    @Override
    public void getAppointmentById(String appointmentId, Callback<Appointment> callback) {
        appointmentsRef.child(appointmentId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Appointment apt = snapshot.getValue(Appointment.class);
                if (apt != null) {
                    callback.onSuccess(apt);
                } else {
                    callback.onError(new NoSuchElementException("Appointment not found."));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }

    @Override
    public void createAppointment(Appointment appointment, Callback<Appointment> callback) {
        // Check for booking conflict on same physician + date + timeSlot
        appointmentsRef.orderByChild("physicianId").equalTo(appointment.getPhysicianId())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Appointment existing = child.getValue(Appointment.class);
                            if (existing != null && existing.getDate().equals(appointment.getDate())
                                    && existing.getTimeSlot().equalsIgnoreCase(appointment.getTimeSlot())
                                    && existing.getStatus() != AppointmentStatus.CANCELLED) {
                                callback.onError(new IllegalStateException("This time slot is already reserved."));
                                return;
                            }
                        }

                        String id = appointment.getId();
                        if (id == null || id.isEmpty()) {
                            DatabaseReference newChild = appointmentsRef.push();
                            id = newChild.getKey();
                            appointment.setId(id);
                        }
                        if (appointment.getCreatedAt() <= 0) {
                            appointment.setCreatedAt(System.currentTimeMillis());
                        }
                        appointment.setStatus(AppointmentStatus.PENDING);

                        appointmentsRef.child(id).setValue(appointment)
                                .addOnSuccessListener(aVoid -> callback.onSuccess(appointment))
                                .addOnFailureListener(callback::onError);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.toException());
                    }
                });
    }

    @Override
    public void updateAppointmentStatus(String appointmentId, AppointmentStatus status, Callback<Void> callback) {
        appointmentsRef.child(appointmentId).child("status").setValue(status.name())
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }

    @Override
    public void cancelAppointment(String appointmentId, String reason, Callback<Void> callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("status", AppointmentStatus.CANCELLED.name());
        updates.put("cancellationReason", reason);

        appointmentsRef.child(appointmentId).updateChildren(updates)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }

    @Override
    public void getAvailableTimeSlots(String physicianId, String date, Callback<List<String>> callback) {
        List<String> standardSlots = Arrays.asList(
                "09:00 AM - 09:30 AM",
                "10:00 AM - 10:30 AM",
                "11:00 AM - 11:30 AM",
                "01:00 PM - 01:30 PM",
                "02:00 PM - 02:30 PM",
                "03:00 PM - 03:30 PM"
        );

        appointmentsRef.orderByChild("physicianId").equalTo(physicianId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Set<String> takenSlots = new HashSet<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            Appointment a = child.getValue(Appointment.class);
                            if (a != null && a.getDate().equals(date) && a.getStatus() != AppointmentStatus.CANCELLED) {
                                takenSlots.add(a.getTimeSlot());
                            }
                        }
                        List<String> available = new ArrayList<>();
                        for (String slot : standardSlots) {
                            if (!takenSlots.contains(slot)) available.add(slot);
                        }
                        callback.onSuccess(available);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.toException());
                    }
                });
    }

    // ==================== RecoveryRepository ====================

    @Override
    public void getRecoveryRecordsForPatient(String patientId, Callback<List<RecoveryRecord>> callback) {
        recoveryRef.child(patientId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<RecoveryRecord> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    RecoveryRecord r = child.getValue(RecoveryRecord.class);
                    if (r != null) list.add(r);
                }
                Collections.sort(list, Comparator.comparingLong(RecoveryRecord::getTimestamp));
                callback.onSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }

    @Override
    public void addRecoveryRecord(RecoveryRecord record, Callback<RecoveryRecord> callback) {
        String patientId = record.getPatientId();
        if (patientId == null || patientId.isEmpty()) {
            callback.onError(new IllegalArgumentException("Patient ID is required."));
            return;
        }

        String recordId = record.getId();
        if (recordId == null || recordId.isEmpty()) {
            DatabaseReference pushed = recoveryRef.child(patientId).push();
            recordId = pushed.getKey();
            record.setId(recordId);
        }
        if (record.getTimestamp() <= 0) {
            record.setTimestamp(System.currentTimeMillis());
        }

        recoveryRef.child(patientId).child(recordId).setValue(record)
                .addOnSuccessListener(aVoid -> callback.onSuccess(record))
                .addOnFailureListener(callback::onError);
    }

    @Override
    public void getLatestHealthStatus(String patientId, Callback<HealthStatus> callback) {
        recoveryRef.child(patientId).orderByChild("timestamp").limitToLast(1)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        HealthStatus status = HealthStatus.STABLE;
                        for (DataSnapshot child : snapshot.getChildren()) {
                            RecoveryRecord r = child.getValue(RecoveryRecord.class);
                            if (r != null) status = r.getHealthStatus();
                        }
                        callback.onSuccess(status);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.toException());
                    }
                });
    }

    // ==================== MessagingRepository ====================

    @Override
    public void getConversationsForUser(String userId, Callback<List<Conversation>> callback) {
        if (userId == null || userId.trim().isEmpty()) {
            callback.onError(new IllegalArgumentException("User ID cannot be null or empty."));
            return;
        }

        Map<String, Conversation> convMap = new LinkedHashMap<>();

        // Query conversations where user is the patient (complying with Security Rules)
        conversationsRef.orderByChild("patientId").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot patientSnap) {
                        for (DataSnapshot child : patientSnap.getChildren()) {
                            Conversation c = child.getValue(Conversation.class);
                            if (c != null && c.getId() != null) {
                                convMap.put(c.getId(), c);
                            }
                        }

                        // Query conversations where user is the psychiatrist (complying with Security Rules)
                        conversationsRef.orderByChild("psychiatristId").equalTo(userId)
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot psychSnap) {
                                        for (DataSnapshot child : psychSnap.getChildren()) {
                                            Conversation c = child.getValue(Conversation.class);
                                            if (c != null && c.getId() != null) {
                                                convMap.put(c.getId(), c);
                                            }
                                        }

                                        List<Conversation> list = new ArrayList<>(convMap.values());
                                        Collections.sort(list, (a, b) -> Long.compare(b.getLastMessageTimestamp(), a.getLastMessageTimestamp()));
                                        callback.onSuccess(list);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        callback.onError(error.toException());
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.toException());
                    }
                });
    }

    @Override
    public void getMessages(String conversationId, Callback<List<ChatMessage>> callback) {
        messagesRef.child(conversationId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<ChatMessage> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    ChatMessage msg = child.getValue(ChatMessage.class);
                    if (msg != null) list.add(msg);
                }
                Collections.sort(list, Comparator.comparingLong(ChatMessage::getTimestamp));
                callback.onSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }

    @Override
    public void sendMessage(ChatMessage message, Callback<ChatMessage> callback) {
        String convId = message.getConversationId();
        DatabaseReference msgPush = messagesRef.child(convId).push();
        String msgId = msgPush.getKey();
        message.setId(msgId);
        if (message.getTimestamp() <= 0) {
            message.setTimestamp(System.currentTimeMillis());
        }

        msgPush.setValue(message)
                .addOnSuccessListener(aVoid -> {
                    // Update last message snippet on conversation
                    Map<String, Object> convUpdates = new HashMap<>();
                    convUpdates.put("lastMessageText", message.getMessageText());
                    convUpdates.put("lastMessageTimestamp", message.getTimestamp());
                    conversationsRef.child(convId).updateChildren(convUpdates);
                    callback.onSuccess(message);
                })
                .addOnFailureListener(callback::onError);
    }

    @Override
    public void getOrCreateConversation(String patientId, String patientPseudonym,
                                        String psychiatristId, String psychiatristName,
                                        Callback<Conversation> callback) {
        if (patientId == null || psychiatristId == null) {
            callback.onError(new IllegalArgumentException("Patient ID and Psychiatrist ID are required."));
            return;
        }

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String currentUid = currentUser != null ? currentUser.getUid() : null;

        // Choose query indexed field matching the authenticated caller's identity
        // to strictly comply with Realtime Database Security Rules
        Query query;
        if (currentUid != null && currentUid.equals(psychiatristId)) {
            query = conversationsRef.orderByChild("psychiatristId").equalTo(psychiatristId);
        } else {
            query = conversationsRef.orderByChild("patientId").equalTo(patientId);
        }

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    Conversation c = child.getValue(Conversation.class);
                    if (c != null && patientId.equals(c.getPatientId()) && psychiatristId.equals(c.getPsychiatristId())) {
                        callback.onSuccess(c);
                        return;
                    }
                }

                DatabaseReference pushRef = conversationsRef.push();
                String newId = pushRef.getKey();
                Conversation newConv = new Conversation(
                        newId,
                        patientId,
                        patientPseudonym,
                        psychiatristId,
                        psychiatristName,
                        "Session initialized",
                        System.currentTimeMillis(),
                        0
                );
                pushRef.setValue(newConv)
                        .addOnSuccessListener(aVoid -> callback.onSuccess(newConv))
                        .addOnFailureListener(callback::onError);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }

    // ==================== EducationalRepository ====================

    @Override
    public void getFilteredResources(SubstanceType substanceFilter, ResourceType typeFilter,
                                     AgeGroup ageFilter, Callback<List<EducationalResource>> callback) {
        educationalRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<EducationalResource> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    EducationalResource r = child.getValue(EducationalResource.class);
                    if (r != null) {
                        boolean matchSubstance = (substanceFilter == null || substanceFilter == SubstanceType.ALL
                                || r.getSubstanceType() == SubstanceType.ALL || r.getSubstanceType() == substanceFilter);
                        boolean matchType = (typeFilter == null || r.getResourceType() == typeFilter);
                        boolean matchAge = (ageFilter == null || ageFilter == AgeGroup.ALL
                                || r.getAgeGroup() == AgeGroup.ALL || r.getAgeGroup() == ageFilter);

                        if (matchSubstance && matchType && matchAge) {
                            list.add(r);
                        }
                    }
                }
                callback.onSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }

    @Override
    public void getAllResources(Callback<List<EducationalResource>> callback) {
        educationalRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<EducationalResource> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    EducationalResource r = child.getValue(EducationalResource.class);
                    if (r != null) list.add(r);
                }
                callback.onSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }

    @Override
    public void getResourceById(String resourceId, Callback<EducationalResource> callback) {
        educationalRef.child(resourceId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                EducationalResource r = snapshot.getValue(EducationalResource.class);
                if (r != null) {
                    callback.onSuccess(r);
                } else {
                    callback.onError(new NoSuchElementException("Resource not found."));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }

    @Override
    public void addResource(EducationalResource resource, Callback<EducationalResource> callback) {
        DatabaseReference pushRef = educationalRef.push();
        String id = pushRef.getKey();
        resource.setId(id);
        resource.setCreatedAt(System.currentTimeMillis());

        pushRef.setValue(resource)
                .addOnSuccessListener(aVoid -> callback.onSuccess(resource))
                .addOnFailureListener(callback::onError);
    }

    @Override
    public void updateResource(EducationalResource resource, Callback<Void> callback) {
        educationalRef.child(resource.getId()).setValue(resource)
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }

    @Override
    public void deleteResource(String resourceId, Callback<Void> callback) {
        educationalRef.child(resourceId).removeValue()
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }

    // ==================== UserRepository ====================

    @Override
    public void getUsersByRole(UserRole role, Callback<List<UserProfile>> callback) {
        usersRef.orderByChild("role").equalTo(role.name()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<UserProfile> list = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {
                    UserProfile u = child.getValue(UserProfile.class);
                    if (u != null) list.add(u);
                }
                callback.onSuccess(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }

    @Override
    public void getUserById(String uid, Callback<UserProfile> callback) {
        usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserProfile u = snapshot.getValue(UserProfile.class);
                if (u != null) {
                    callback.onSuccess(u);
                } else {
                    callback.onError(new NoSuchElementException("User not found: " + uid));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }

    // ==================== SessionRepository ====================

    @Override
    public void getSessionsForPsychiatrist(String psychiatristId, Callback<List<ConsultationSession>> callback) {
        sessionsRef.orderByChild("psychiatristId").equalTo(psychiatristId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<ConsultationSession> list = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            ConsultationSession s = child.getValue(ConsultationSession.class);
                            if (s != null) list.add(s);
                        }
                        callback.onSuccess(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.toException());
                    }
                });
    }

    @Override
    public void getPendingQueue(Callback<List<ConsultationSession>> callback) {
        sessionsRef.orderByChild("status").equalTo(SessionStatus.REQUESTED.name())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<ConsultationSession> list = new ArrayList<>();
                        for (DataSnapshot child : snapshot.getChildren()) {
                            ConsultationSession s = child.getValue(ConsultationSession.class);
                            if (s != null) list.add(s);
                        }
                        callback.onSuccess(list);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onError(error.toException());
                    }
                });
    }

    @Override
    public void requestSession(ConsultationSession session, Callback<ConsultationSession> callback) {
        DatabaseReference pushRef = sessionsRef.push();
        String id = pushRef.getKey();
        session.setId(id);
        session.setRequestedAt(System.currentTimeMillis());
        session.setStatus(SessionStatus.REQUESTED);

        pushRef.setValue(session)
                .addOnSuccessListener(aVoid -> callback.onSuccess(session))
                .addOnFailureListener(callback::onError);
    }

    @Override
    public void updateSessionStatus(String sessionId, SessionStatus status, Callback<Void> callback) {
        sessionsRef.child(sessionId).child("status").setValue(status.name())
                .addOnSuccessListener(aVoid -> callback.onSuccess(null))
                .addOnFailureListener(callback::onError);
    }
}
