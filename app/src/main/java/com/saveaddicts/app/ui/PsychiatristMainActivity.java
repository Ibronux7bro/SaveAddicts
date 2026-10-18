package com.saveaddicts.app.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saveaddicts.app.R;
import com.saveaddicts.app.adapters.ConsultationSessionAdapter;
import com.saveaddicts.app.adapters.ConversationAdapter;
import com.saveaddicts.app.models.ConsultationSession;
import com.saveaddicts.app.models.Conversation;
import com.saveaddicts.app.models.SessionStatus;
import com.saveaddicts.app.models.UserProfile;
import com.saveaddicts.app.models.UserRole;
import com.saveaddicts.app.repositories.Callback;
import com.saveaddicts.app.repositories.RepositoryLocator;

import java.util.List;

public class PsychiatristMainActivity extends AppCompatActivity {

    private UserProfile currentPsychiatrist;

    private TextView tvDoctorName, tvAvatarInitials, tvQueueCount;
    private TextView tvEmptyQueue, tvEmptyConversations;
    private RecyclerView rvSessionQueue, rvPsychiatristConversations;

    private ConsultationSessionAdapter sessionAdapter;
    private ConversationAdapter conversationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psychiatrist_main);

        currentPsychiatrist = RepositoryLocator.getAuthRepository().getCurrentUser();
        if (currentPsychiatrist == null || currentPsychiatrist.getRole() != UserRole.PSYCHIATRIST) {
            Toast.makeText(this, "Access restricted to licensed Psychiatrists.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        initViews();
        setupQueueRecyclerView();
        setupConversationsRecyclerView();
        loadAllData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadAllData();
    }

    private void initViews() {
        tvDoctorName = findViewById(R.id.tvDoctorName);
        tvAvatarInitials = findViewById(R.id.tvAvatarInitials);
        tvQueueCount = findViewById(R.id.tvQueueCount);
        tvEmptyQueue = findViewById(R.id.tvEmptyQueue);
        tvEmptyConversations = findViewById(R.id.tvEmptyConversations);
        rvSessionQueue = findViewById(R.id.rvSessionQueue);
        rvPsychiatristConversations = findViewById(R.id.rvPsychiatristConversations);

        tvDoctorName.setText(currentPsychiatrist.getPseudonym());
        tvAvatarInitials.setText("DR");

        findViewById(R.id.btnLogout).setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Sign Out")
                    .setMessage("Are you sure you want to end your psychiatric clinical session?")
                    .setPositiveButton("Sign Out", (dialog, which) -> {
                        RepositoryLocator.getAuthRepository().logout();
                        startActivity(new Intent(this, LoginActivity.class));
                        finishAffinity();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });
    }

    private void setupQueueRecyclerView() {
        rvSessionQueue.setLayoutManager(new LinearLayoutManager(this));
        sessionAdapter = new ConsultationSessionAdapter(new ConsultationSessionAdapter.OnSessionActionListener() {
            @Override
            public void onAccept(ConsultationSession session) {
                RepositoryLocator.getSessionRepository().updateSessionStatus(session.getId(), SessionStatus.ACCEPTED, new Callback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        Toast.makeText(PsychiatristMainActivity.this, "Consultation session accepted.", Toast.LENGTH_SHORT).show();
                        // Initialize conversation if not already created
                        RepositoryLocator.getMessagingRepository().getOrCreateConversation(
                                session.getPatientId(),
                                session.getPatientPseudonym(),
                                currentPsychiatrist.getUid(),
                                currentPsychiatrist.getPseudonym(),
                                new Callback<Conversation>() {
                                    @Override
                                    public void onSuccess(Conversation conv) {
                                        loadAllData();
                                    }

                                    @Override
                                    public void onError(Exception exception) {
                                        loadAllData();
                                    }
                                }
                        );
                    }

                    @Override
                    public void onError(Exception exception) {
                        Toast.makeText(PsychiatristMainActivity.this, "Failed to update session: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onOpenChat(ConsultationSession session) {
                RepositoryLocator.getMessagingRepository().getOrCreateConversation(
                        session.getPatientId(),
                        session.getPatientPseudonym(),
                        currentPsychiatrist.getUid(),
                        currentPsychiatrist.getPseudonym(),
                        new Callback<Conversation>() {
                            @Override
                            public void onSuccess(Conversation conv) {
                                Intent intent = new Intent(PsychiatristMainActivity.this, ChatActivity.class);
                                intent.putExtra("conversation", conv);
                                startActivity(intent);
                            }

                            @Override
                            public void onError(Exception exception) {
                                Toast.makeText(PsychiatristMainActivity.this, "Could not open chat: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        });
        rvSessionQueue.setAdapter(sessionAdapter);
    }

    private void setupConversationsRecyclerView() {
        rvPsychiatristConversations.setLayoutManager(new LinearLayoutManager(this));
        conversationAdapter = new ConversationAdapter(UserRole.PSYCHIATRIST, conversation -> {
            Intent intent = new Intent(PsychiatristMainActivity.this, ChatActivity.class);
            intent.putExtra("conversation", conversation);
            startActivity(intent);
        });
        rvPsychiatristConversations.setAdapter(conversationAdapter);
    }

    private void loadAllData() {
        // Load pending session queue
        RepositoryLocator.getSessionRepository().getPendingQueue(new Callback<List<ConsultationSession>>() {
            @Override
            public void onSuccess(List<ConsultationSession> list) {
                sessionAdapter.setItems(list);
                int count = list != null ? list.size() : 0;
                tvQueueCount.setText(count + " In Queue");
                tvEmptyQueue.setVisibility(count == 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(PsychiatristMainActivity.this, "Error loading queue: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // Load active conversations
        RepositoryLocator.getMessagingRepository().getConversationsForUser(currentPsychiatrist.getUid(), new Callback<List<Conversation>>() {
            @Override
            public void onSuccess(List<Conversation> list) {
                conversationAdapter.setItems(list);
                tvEmptyConversations.setVisibility(list == null || list.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(PsychiatristMainActivity.this, "Error loading chats: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
