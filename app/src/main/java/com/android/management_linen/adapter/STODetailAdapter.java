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

public class STODetailAdapter extends RecyclerView.Adapter<STODetailAdapter.ViewHolder> {

    private List<DetailScanSto> stoDetails;

    public STODetailAdapter(List<DetailScanSto> stoDetails) {
        this.stoDetails = stoDetails;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sto_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DetailScanSto detail = stoDetails.get(position);
        
        holder.tvCategory.setText(detail.getCategory());
        holder.tvSubCategory.setText(detail.getSubCategory());
        holder.tvColor.setText(detail.getColor());
        holder.tvSize.setText(detail.getSize());
        holder.tvCount.setText(String.valueOf(detail.getCount()));
        holder.tvMatchingCount.setText(String.valueOf(detail.getMatchingCount()));
        holder.tvNoMatchCount.setText(String.valueOf(detail.getNoMatchCount()));
    }

    @Override
    public int getItemCount() {
        return stoDetails.size();
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