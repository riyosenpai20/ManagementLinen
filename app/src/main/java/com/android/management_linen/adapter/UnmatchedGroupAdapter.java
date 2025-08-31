package com.android.management_linen.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.management_linen.R;
import com.android.management_linen.models.DetailScanSto;

import java.util.List;

public class UnmatchedGroupAdapter extends RecyclerView.Adapter<UnmatchedGroupAdapter.ViewHolder> {

    private List<DetailScanSto.UnmatchedGroup> unmatchedGroups;

    public UnmatchedGroupAdapter(List<DetailScanSto.UnmatchedGroup> unmatchedGroups) {
        this.unmatchedGroups = unmatchedGroups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sto_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DetailScanSto.UnmatchedGroup group = unmatchedGroups.get(position);
        
        holder.tvCategory.setText(group.getCategory());
        holder.tvSubCategory.setText(group.getSubCategory());
        holder.tvColor.setText(group.getColor());
        holder.tvSize.setText(group.getSize());
        holder.tvCount.setText(String.valueOf(group.getCount()));
        
        // Hide matching and no match counts as they don't apply to unmatched groups
        // Fix: directly set the TextView visibility instead of trying to access parent
        holder.tvMatchingCount.setVisibility(View.GONE);
        holder.tvNoMatchCount.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return unmatchedGroups.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategory, tvSubCategory, tvColor, tvSize, tvCount, tvMatchingCount, tvNoMatchCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvSubCategory = itemView.findViewById(R.id.tvSubCategory);
            tvColor = itemView.findViewById(R.id.tvColor);
            tvSize = itemView.findViewById(R.id.tvSize);
            tvCount = itemView.findViewById(R.id.tvCount);
            tvMatchingCount = itemView.findViewById(R.id.tvMatchingCount);
            tvNoMatchCount = itemView.findViewById(R.id.tvNoMatchCount);
        }
    }
}