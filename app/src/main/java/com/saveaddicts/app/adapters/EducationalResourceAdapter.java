package com.saveaddicts.app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.saveaddicts.app.R;
import com.saveaddicts.app.models.EducationalResource;
import com.saveaddicts.app.models.ResourceType;

import java.util.ArrayList;
import java.util.List;

public class EducationalResourceAdapter extends RecyclerView.Adapter<EducationalResourceAdapter.ViewHolder> {

    public interface OnResourceClickListener {
        void onItemClick(EducationalResource resource);
        void onEdit(EducationalResource resource);
        void onDelete(EducationalResource resource);
    }

    private final List<EducationalResource> items = new ArrayList<>();
    private final boolean isAdminMode;
    private final OnResourceClickListener listener;

    public EducationalResourceAdapter(boolean isAdminMode, OnResourceClickListener listener) {
        this.isAdminMode = isAdminMode;
        this.listener = listener;
    }

    public void setItems(List<EducationalResource> newItems) {
        this.items.clear();
        if (newItems != null) {
            this.items.addAll(newItems);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_educational_resource, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EducationalResource item = items.get(position);

        holder.tvTitle.setText(item.getTitle());
        holder.tvDescription.setText(item.getDescription());
        holder.tvResourceType.setText(item.getResourceType().getLabel());
        holder.tvSubstanceBadge.setText(item.getSubstanceType().getDisplayName());
        holder.tvAgeBadge.setText(item.getAgeGroup().getLabel());
        holder.tvCulturalBadge.setText(item.getCulturalContext());

        if (item.getResourceType() == ResourceType.VIDEO) {
            holder.ivResourceType.setImageResource(R.drawable.ic_video);
        } else if (item.getResourceType() == ResourceType.BOOK) {
            holder.ivResourceType.setImageResource(R.drawable.ic_book);
        } else {
            holder.ivResourceType.setImageResource(R.drawable.ic_article);
        }

        // Distinct sample content badge
        holder.tvSampleBadge.setVisibility(item.isSampleContent() ? View.VISIBLE : View.GONE);

        // Admin controls
        if (isAdminMode) {
            holder.adminControls.setVisibility(View.VISIBLE);
            holder.btnEditResource.setOnClickListener(v -> {
                if (listener != null) listener.onEdit(item);
            });
            holder.btnDeleteResource.setOnClickListener(v -> {
                if (listener != null) listener.onDelete(item);
            });
        } else {
            holder.adminControls.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivResourceType;
        TextView tvResourceType, tvSampleBadge, tvTitle, tvDescription;
        TextView tvSubstanceBadge, tvAgeBadge, tvCulturalBadge;
        LinearLayout adminControls;
        Button btnEditResource, btnDeleteResource;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivResourceType = itemView.findViewById(R.id.ivResourceType);
            tvResourceType = itemView.findViewById(R.id.tvResourceType);
            tvSampleBadge = itemView.findViewById(R.id.tvSampleBadge);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvSubstanceBadge = itemView.findViewById(R.id.tvSubstanceBadge);
            tvAgeBadge = itemView.findViewById(R.id.tvAgeBadge);
            tvCulturalBadge = itemView.findViewById(R.id.tvCulturalBadge);
            adminControls = itemView.findViewById(R.id.adminControls);
            btnEditResource = itemView.findViewById(R.id.btnEditResource);
            btnDeleteResource = itemView.findViewById(R.id.btnDeleteResource);
        }
    }
}
