package com.saveaddicts.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saveaddicts.app.R;
import com.saveaddicts.app.models.Conversation;
import com.saveaddicts.app.models.UserRole;

import java.util.ArrayList;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ViewHolder> {

    public interface OnConversationClickListener {
        void onConversationClick(Conversation conversation);
    }

    private final List<Conversation> items = new ArrayList<>();
    private final UserRole currentRole;
    private final OnConversationClickListener listener;

    public ConversationAdapter(UserRole currentRole, OnConversationClickListener listener) {
        this.currentRole = currentRole;
        this.listener = listener;
    }

    public void setItems(List<Conversation> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_conversation, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Conversation item = items.get(position);

        String title;
        if (currentRole == UserRole.PSYCHIATRIST) {
            title = "Patient: " + item.getPatientPseudonym();
        } else {
            title = item.getPsychiatristName();
        }

        holder.tvParticipantName.setText(title);
        holder.tvLastMessage.setText(item.getLastMessageText());
        holder.tvTimestamp.setText(item.getFormattedDate());

        String initials = "DR";
        if (currentRole == UserRole.PSYCHIATRIST && item.getPatientPseudonym() != null && item.getPatientPseudonym().length() >= 2) {
            initials = item.getPatientPseudonym().substring(0, 2).toUpperCase();
        }
        holder.tvAvatarInitials.setText(initials);

        if (item.getUnreadCount() > 0) {
            holder.tvUnreadBadge.setText(String.valueOf(item.getUnreadCount()));
            holder.tvUnreadBadge.setVisibility(View.VISIBLE);
        } else {
            holder.tvUnreadBadge.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onConversationClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatarInitials, tvParticipantName, tvLastMessage, tvTimestamp, tvUnreadBadge;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatarInitials = itemView.findViewById(R.id.tvAvatarInitials);
            tvParticipantName = itemView.findViewById(R.id.tvParticipantName);
            tvLastMessage = itemView.findViewById(R.id.tvLastMessage);
            tvTimestamp = itemView.findViewById(R.id.tvTimestamp);
            tvUnreadBadge = itemView.findViewById(R.id.tvUnreadBadge);
        }
    }
}
