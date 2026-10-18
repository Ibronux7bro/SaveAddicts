package com.saveaddicts.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saveaddicts.app.R;
import com.saveaddicts.app.models.ConsultationSession;
import com.saveaddicts.app.models.SessionStatus;

import java.util.ArrayList;
import java.util.List;

public class ConsultationSessionAdapter extends RecyclerView.Adapter<ConsultationSessionAdapter.ViewHolder> {

    public interface OnSessionActionListener {
        void onAccept(ConsultationSession session);
        void onOpenChat(ConsultationSession session);
    }

    private final List<ConsultationSession> items = new ArrayList<>();
    private final OnSessionActionListener listener;

    public ConsultationSessionAdapter(OnSessionActionListener listener) {
        this.listener = listener;
    }

    public void setItems(List<ConsultationSession> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_consultation_session, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ConsultationSession item = items.get(position);

        holder.tvPatientPseudonym.setText("Patient: " + item.getPatientPseudonym());
        holder.tvSubstanceBadge.setText(item.getSubstanceType().getDisplayName());
        holder.tvRequestedDate.setText("Requested: " + item.getFormattedDate());
        holder.tvInitialNotes.setText(item.getInitialNotes() != null ? item.getInitialNotes() : "No clinical notes provided.");

        if (item.getStatus() == SessionStatus.REQUESTED) {
            holder.btnAcceptSession.setVisibility(View.VISIBLE);
            holder.btnOpenChat.setVisibility(View.GONE);
            holder.btnAcceptSession.setText("Accept Consultation");
        } else {
            holder.btnAcceptSession.setVisibility(View.GONE);
            holder.btnOpenChat.setVisibility(View.VISIBLE);
        }

        holder.btnAcceptSession.setOnClickListener(v -> {
            if (listener != null) listener.onAccept(item);
        });

        holder.btnOpenChat.setOnClickListener(v -> {
            if (listener != null) listener.onOpenChat(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPatientPseudonym, tvSubstanceBadge, tvRequestedDate, tvInitialNotes;
        Button btnAcceptSession, btnOpenChat;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPatientPseudonym = itemView.findViewById(R.id.tvPatientPseudonym);
            tvSubstanceBadge = itemView.findViewById(R.id.tvSubstanceBadge);
            tvRequestedDate = itemView.findViewById(R.id.tvRequestedDate);
            tvInitialNotes = itemView.findViewById(R.id.tvInitialNotes);
            btnAcceptSession = itemView.findViewById(R.id.btnAcceptSession);
            btnOpenChat = itemView.findViewById(R.id.btnOpenChat);
        }
    }
}
