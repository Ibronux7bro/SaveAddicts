package com.saveaddicts.app.ui;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.saveaddicts.app.R;
import com.saveaddicts.app.adapters.ChatMessageAdapter;
import com.saveaddicts.app.models.ChatMessage;
import com.saveaddicts.app.models.Conversation;
import com.saveaddicts.app.models.UserProfile;
import com.saveaddicts.app.models.UserRole;
import com.saveaddicts.app.repositories.Callback;
import com.saveaddicts.app.repositories.RepositoryLocator;
import com.saveaddicts.app.security.EncryptionService;

import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private Conversation conversation;
    private UserProfile currentUser;

    private TextView tvChatTitle, tvChatSubtitle, tvAvatarInitials, tvEncryptionNotice;
    private RecyclerView rvMessages;
    private EditText etMessageInput;
    private ImageButton btnSendMessage;

    private ChatMessageAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        conversation = (Conversation) getIntent().getSerializableExtra("conversation");
        currentUser = RepositoryLocator.getAuthRepository().getCurrentUser();

        if (currentUser == null || conversation == null) {
            Toast.makeText(this, "Unable to load conversation details.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        initViews();
        setupHeader();
        setupRecyclerView();
        setupMessaging();
        loadMessages();
    }

    private void initViews() {
        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
        tvChatTitle = findViewById(R.id.tvChatTitle);
        tvChatSubtitle = findViewById(R.id.tvChatSubtitle);
        tvAvatarInitials = findViewById(R.id.tvAvatarInitials);
        tvEncryptionNotice = findViewById(R.id.tvEncryptionNotice);
        rvMessages = findViewById(R.id.rvMessages);
        etMessageInput = findViewById(R.id.etMessageInput);
        btnSendMessage = findViewById(R.id.btnSendMessage);
    }

    private void setupHeader() {
        boolean isCurrentPsychiatrist = currentUser.getRole() == UserRole.PSYCHIATRIST;
        if (isCurrentPsychiatrist) {
            tvChatTitle.setText("Patient: " + conversation.getPatientPseudonym());
            tvChatSubtitle.setText("Confidential Patient Session (FR3)");
            tvAvatarInitials.setText(conversation.getPatientPseudonym().substring(0, Math.min(2, conversation.getPatientPseudonym().length())).toUpperCase());
        } else {
            tvChatTitle.setText(conversation.getPsychiatristName());
            tvChatSubtitle.setText("Licensed Psychiatrist • Confidential Chat (FR3)");
            tvAvatarInitials.setText("DR");
        }

        EncryptionService encService = RepositoryLocator.getEncryptionService();
        tvEncryptionNotice.setText(encService.getSecurityNotice());
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        rvMessages.setLayoutManager(layoutManager);
        adapter = new ChatMessageAdapter(currentUser.getUid());
        rvMessages.setAdapter(adapter);
    }

    private void setupMessaging() {
        btnSendMessage.setOnClickListener(v -> {
            String text = etMessageInput.getText() != null ? etMessageInput.getText().toString().trim() : "";
            if (text.isEmpty()) return;

            // Determine recipient
            String recipientId = currentUser.getUid().equals(conversation.getPatientId())
                    ? conversation.getPsychiatristId()
                    : conversation.getPatientId();

            EncryptionService enc = RepositoryLocator.getEncryptionService();
            String encrypted = enc.encrypt(text, recipientId);

            ChatMessage msg = new ChatMessage(
                    null,
                    conversation.getId(),
                    currentUser.getUid(),
                    currentUser.getPseudonym(),
                    currentUser.getRole(),
                    recipientId,
                    encrypted,
                    text,
                    System.currentTimeMillis()
            );

            etMessageInput.setText("");
            adapter.addMessage(msg);
            rvMessages.smoothScrollToPosition(adapter.getItemCount() - 1);

            RepositoryLocator.getMessagingRepository().sendMessage(msg, new Callback<ChatMessage>() {
                @Override
                public void onSuccess(ChatMessage result) {
                    // Message synchronized
                }

                @Override
                public void onError(Exception exception) {
                    Toast.makeText(ChatActivity.this, "Failed to deliver message: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    private void loadMessages() {
        RepositoryLocator.getMessagingRepository().getMessages(conversation.getId(), new Callback<List<ChatMessage>>() {
            @Override
            public void onSuccess(List<ChatMessage> list) {
                adapter.setItems(list);
                if (adapter.getItemCount() > 0) {
                    rvMessages.scrollToPosition(adapter.getItemCount() - 1);
                }
            }

            @Override
            public void onError(Exception exception) {
                Toast.makeText(ChatActivity.this, "Error loading messages: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
