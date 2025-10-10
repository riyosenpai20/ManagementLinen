package com.example.management_linen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.management_linen.R;
import com.example.management_linen.models.DataItem;
import com.example.management_linen.models.Details;

import java.util.List;

public class DetailsAdapter extends RecyclerView.Adapter<DetailsAdapter.DetailsHolder> {
    private List<Details> detailList;

    public DetailsAdapter(List<Details> detailList) {
        this.detailList = detailList;
    }

    @NonNull
    @Override
    public DetailsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_card, parent, false);
        return new DetailsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsHolder holder, int position) {
//        Details dataItem = detailList.get(position);
        int d = detailList.get(position).getCountBarang();
        holder.tvName.setText(detailList.get(position).getSubCategoryName());
        holder.tvWarna.setText(detailList.get(position).getWarnaName());
        holder.tvUkuran.setText(detailList.get(position).getUkuranName());
        holder.tvPerusahaan.setText(detailList.get(position).getPerusahaanNama());
        holder.tvRuang.setText(detailList.get(position).getBarangruangRuangNama());
        holder.tvCount.setText(Integer.toString(d));
    }

    @Override
    public int getItemCount() {
        return detailList.size();
    }

    public class DetailsHolder extends RecyclerView.ViewHolder{
        TextView tvBatasCuci, tvWarna, tvName, tvUkuran, tvPerusahaan, tvRuang, tvCount;
        public DetailsHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvWarna = itemView.findViewById(R.id.tvWarna);
            tvUkuran = itemView.findViewById(R.id.tvUkuran);
            tvPerusahaan = itemView.findViewById(R.id.tvRS);
            tvRuang = itemView.findViewById(R.id.tvRuang);
            tvCount = itemView.findViewById(R.id.tvCount);
        }
    }
}
