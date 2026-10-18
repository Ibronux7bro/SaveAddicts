package com.saveaddicts.app.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saveaddicts.app.R;
import com.saveaddicts.app.models.HealthStatus;
import com.saveaddicts.app.models.UserProfile;
import com.saveaddicts.app.repositories.Callback;
import com.saveaddicts.app.repositories.RepositoryLocator;

import java.util.ArrayList;
import java.util.List;

public class PatientListAdapter extends RecyclerView.Adapter<PatientListAdapter.ViewHolder> {

    public interface OnPatientSelectListener {
        void onSelectPatient(UserProfile patient);
    }

    private final List<UserProfile> items = new ArrayList<>();
    private final OnPatientSelectListener listener;

    public PatientListAdapter(OnPatientSelectListener listener) {
        this.listener = listener;
    }

    public void setItems(List<UserProfile> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        UserProfile patient = items.get(position);

        holder.tvPseudonym.setText(patient.getPseudonym());
        holder.tvSubstance.setText("Declared Substance: " + patient.getSubstancePreference().getDisplayName());
        holder.tvAvatarInitials.setText(patient.getInitials());

        // Fetch latest health status dynamically from repository
        RepositoryLocator.getRecoveryRepository().getLatestHealthStatus(patient.getUid(), new Callback<HealthStatus>() {
            @Override
            public void onSuccess(HealthStatus status) {
                holder.tvStatusBadge.setText(status.getLabel());
                try {
                    holder.tvStatusBadge.setTextColor(Color.parseColor(status.getColorHex()));
                } catch (Exception ignored) {}
            }

            @Override
            public void onError(Exception exception) {
                holder.tvStatusBadge.setText("Status Pending");
            }
        });

        holder.btnUpdateProgress.setOnClickListener(v -> {
            if (listener != null) listener.onSelectPatient(patient);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvAvatarInitials, tvPseudonym, tvSubstance, tvStatusBadge;
        Button btnUpdateProgress;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAvatarInitials = itemView.findViewById(R.id.tvAvatarInitials);
            tvPseudonym = itemView.findViewById(R.id.tvPseudonym);
            tvSubstance = itemView.findViewById(R.id.tvSubstance);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            btnUpdateProgress = itemView.findViewById(R.id.btnUpdateProgress);
        }
    }
}
