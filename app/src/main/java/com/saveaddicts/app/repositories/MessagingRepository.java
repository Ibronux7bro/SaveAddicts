package com.saveaddicts.app.repositories;

import com.saveaddicts.app.models.ChatMessage;
import com.saveaddicts.app.models.Conversation;

import java.util.List;

/**
 * Repository interface for secure messaging between patients and psychiatrists (FR3).
 */
public interface MessagingRepository {
    void getConversationsForUser(String userId, Callback<List<Conversation>> callback);

    void getMessages(String conversationId, Callback<List<ChatMessage>> callback);

    void sendMessage(ChatMessage message, Callback<ChatMessage> callback);

    void getOrCreateConversation(String patientId, String patientPseudonym,
                                 String psychiatristId, String psychiatristName,
                                 Callback<Conversation> callback);
}
