package com.saveaddicts.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saveaddicts.app.R;
import com.saveaddicts.app.models.ChatMessage;

import java.util.ArrayList;
import java.util.List;

public class ChatMessageAdapter extends RecyclerView.Adapter<ChatMessageAdapter.ViewHolder> {

    private final List<ChatMessage> items = new ArrayList<>();
    private final String currentUserId;

    public ChatMessageAdapter(String currentUserId) {
        this.currentUserId = currentUserId != null ? currentUserId : "";
    }

    public void setItems(List<ChatMessage> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessage message) {
        this.items.add(message);
        notifyItemInserted(this.items.size() - 1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_message, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChatMessage message = items.get(position);
        boolean isOutgoing = message.getSenderId().equals(currentUserId);

        if (isOutgoing) {
            holder.layoutOutgoing.setVisibility(View.VISIBLE);
            holder.layoutIncoming.setVisibility(View.GONE);
            holder.tvOutgoingText.setText(message.getMessageText());
            holder.tvOutgoingTime.setText(message.getFormattedTime());
        } else {
            holder.layoutIncoming.setVisibility(View.VISIBLE);
            holder.layoutOutgoing.setVisibility(View.GONE);
            holder.tvIncomingSender.setText(message.getSenderName());
            holder.tvIncomingText.setText(message.getMessageText());
            holder.tvIncomingTime.setText(message.getFormattedTime());
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout layoutIncoming, layoutOutgoing;
        TextView tvIncomingSender, tvIncomingText, tvIncomingTime;
        TextView tvOutgoingText, tvOutgoingTime;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutIncoming = itemView.findViewById(R.id.layoutIncoming);
            layoutOutgoing = itemView.findViewById(R.id.layoutOutgoing);
            tvIncomingSender = itemView.findViewById(R.id.tvIncomingSender);
            tvIncomingText = itemView.findViewById(R.id.tvIncomingText);
            tvIncomingTime = itemView.findViewById(R.id.tvIncomingTime);
            tvOutgoingText = itemView.findViewById(R.id.tvOutgoingText);
            tvOutgoingTime = itemView.findViewById(R.id.tvOutgoingTime);
        }
    }
}
