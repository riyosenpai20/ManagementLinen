package com.android.management_linen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.management_linen.R;
import com.android.management_linen.models.DetailScanUnknown;

import java.util.List;

public class DetailScanUnknownAdapter extends RecyclerView.Adapter<DetailScanUnknownAdapter.ViewHolder> {
    private List<DetailScanUnknown> items;
    private Context context;

    public DetailScanUnknownAdapter(List<DetailScanUnknown> items, Context context) {
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_detail_scan_unknown, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DetailScanUnknown item = items.get(position);
        
        // Mengambil RFID pertama dari list atau string kosong jika list kosong
        String rfidText = item.getRfids() != null && !item.getRfids().isEmpty() ? 
                          item.getRfids().get(0) : "";
        holder.tvRfid.setText(rfidText);
        
        holder.tvLinen.setText(item.getCategory() + " " + item.getSubCategory() + " " + 
                              item.getColor() + " " + item.getSize());
        holder.tvLokasi.setText(item.getLocation());
        
        // Alternate row colors for better readability
        if (position % 2 == 0) {
            holder.itemView.setBackgroundResource(R.color.tb_RowEven_Primary);
        } else {
            holder.itemView.setBackgroundResource(R.color.tb_RowOdd_Primary);
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRfid, tvLinen, tvLokasi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRfid = itemView.findViewById(R.id.tvRfid);
            tvLinen = itemView.findViewById(R.id.tvLinen);
            tvLokasi = itemView.findViewById(R.id.tvLokasi);
        }
    }
}