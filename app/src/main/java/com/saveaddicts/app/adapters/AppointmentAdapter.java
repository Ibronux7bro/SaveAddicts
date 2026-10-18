package com.saveaddicts.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.saveaddicts.app.R;
import com.saveaddicts.app.models.Appointment;
import com.saveaddicts.app.models.AppointmentStatus;
import com.saveaddicts.app.models.UserRole;

import java.util.ArrayList;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    public interface OnAppointmentActionListener {
        void onConfirm(Appointment appointment);
        void onCancel(Appointment appointment);
    }

    private final List<Appointment> items = new ArrayList<>();
    private final UserRole currentRole;
    private final OnAppointmentActionListener listener;

    public AppointmentAdapter(UserRole currentRole, OnAppointmentActionListener listener) {
        this.currentRole = currentRole;
        this.listener = listener;
    }

    public void setItems(List<Appointment> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment item = items.get(position);
        Context ctx = holder.itemView.getContext();

        // If physician is viewing, show patient pseudonym; if patient is viewing, show doctor name
        if (currentRole == UserRole.PHYSICIAN) {
            holder.tvPartyName.setText("Patient: " + item.getPatientPseudonym());
        } else {
            holder.tvPartyName.setText(item.getPhysicianName());
        }

        holder.tvAppointmentDate.setText(item.getDate());
        holder.tvAppointmentTime.setText(item.getTimeSlot());

        if (item.getReasonNotes() != null && !item.getReasonNotes().trim().isEmpty()) {
            holder.tvReasonNotes.setText(item.getReasonNotes());
            holder.tvReasonNotes.setVisibility(View.VISIBLE);
        } else {
            holder.tvReasonNotes.setVisibility(View.GONE);
        }

        // Status badge styling
        AppointmentStatus status = item.getStatus();
        holder.tvStatusBadge.setText(status.getDisplayStatus());

        switch (status) {
            case CONFIRMED:
                holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_pill_confirmed);
                holder.tvStatusBadge.setTextColor(ContextCompat.getColor(ctx, R.color.success_green));
                holder.btnConfirm.setVisibility(View.GONE);
                holder.btnCancel.setVisibility(View.VISIBLE);
                break;
            case PENDING:
                holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_pill_pending);
                holder.tvStatusBadge.setTextColor(ContextCompat.getColor(ctx, R.color.warning_orange));
                if (currentRole == UserRole.PHYSICIAN) {
                    holder.btnConfirm.setVisibility(View.VISIBLE);
                } else {
                    holder.btnConfirm.setVisibility(View.GONE);
                }
                holder.btnCancel.setVisibility(View.VISIBLE);
                break;
            case CANCELLED:
                holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_pill_cancelled);
                holder.tvStatusBadge.setTextColor(ContextCompat.getColor(ctx, R.color.error_red));
                holder.btnConfirm.setVisibility(View.GONE);
                holder.btnCancel.setVisibility(View.GONE);
                break;
            case COMPLETED:
                holder.tvStatusBadge.setBackgroundResource(R.drawable.bg_pill_confirmed);
                holder.tvStatusBadge.setTextColor(ContextCompat.getColor(ctx, R.color.accent_teal));
                holder.btnConfirm.setVisibility(View.GONE);
                holder.btnCancel.setVisibility(View.GONE);
                break;
        }

        holder.btnConfirm.setOnClickListener(v -> {
            if (listener != null) listener.onConfirm(item);
        });

        holder.btnCancel.setOnClickListener(v -> {
            if (listener != null) listener.onCancel(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvPartyName, tvStatusBadge, tvAppointmentDate, tvAppointmentTime, tvReasonNotes;
        Button btnCancel, btnConfirm;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvPartyName = itemView.findViewById(R.id.tvPartyName);
            tvStatusBadge = itemView.findViewById(R.id.tvStatusBadge);
            tvAppointmentDate = itemView.findViewById(R.id.tvAppointmentDate);
            tvAppointmentTime = itemView.findViewById(R.id.tvAppointmentTime);
            tvReasonNotes = itemView.findViewById(R.id.tvReasonNotes);
            btnCancel = itemView.findViewById(R.id.btnCancel);
            btnConfirm = itemView.findViewById(R.id.btnConfirm);
        }
    }
}
