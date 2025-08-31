package com.android.management_linen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.management_linen.R;
import com.android.management_linen.models.DetailsScanBersih;

import java.util.List;

public class DetailsScanAdapter extends RecyclerView.Adapter<DetailsScanAdapter.DetailsHolder> {
    private List<DetailsScanBersih> detailList;
    private String pic, nmRuang;
    private Context context;
    public DetailsScanAdapter(List<DetailsScanBersih> detailList, Context context) {
        this.detailList = detailList;
        this.context = context;
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
        int countbrg = detailList.get(position).getCountBarang();
        int batascuci = detailList.get(position).getBatascuci();
        String name = detailList.get(position).getSubCategoryName();
        int color = context.getResources().getColor(R.color.blue2);
        int color_expiring = context.getResources().getColor(R.color.color_expiring);
        int color_expired = context.getResources().getColor(R.color.color_expired);
        int color_used = context.getResources().getColor(R.color.color_used);
        int color_unused = context.getResources().getColor(R.color.color_unused);
        int color_disabled = context.getResources().getColor(R.color.color_disabled);
        int color_default = context.getResources().getColor(R.color.black);
        int bg_expiring = context.getResources().getColor(R.color.bg_expiring);
        int bg_expired = context.getResources().getColor(R.color.bg_expired);
        int bg_used = context.getResources().getColor(R.color.bg_used);
        int bg_unused = context.getResources().getColor(R.color.bg_unused);
        int bg_disabled = context.getResources().getColor(R.color.bg_disabled);
        int bg_default = context.getResources().getColor(R.color.white);

        switch (detailList.get(position).getStatus()) {
            case "expiring":
                holder.tvName.setTextColor(color_expiring);
                holder.tvStatus.setTextColor(color_expiring);
                holder.cardView.setCardBackgroundColor(bg_expiring);
                holder.tvStatus.setText("Hampir Kedaluarsa");
                break;
            case "expired":
                holder.tvName.setTextColor(color_expired);
                holder.tvStatus.setTextColor(color_expired);
                holder.cardView.setCardBackgroundColor(bg_expired);
                holder.tvStatus.setText("Kadarluasa");
                break;
            case "used":
                holder.tvName.setTextColor(color_used);
                holder.tvStatus.setTextColor(color_used);
                holder.cardView.setCardBackgroundColor(bg_used);
                holder.tvStatus.setText("Dipakai");
                break;
            case "unused":
                holder.tvName.setTextColor(color_unused);
                holder.tvStatus.setTextColor(color_unused);
                holder.cardView.setCardBackgroundColor(bg_unused);
                holder.tvStatus.setText("Belum Terpakai");
                break;
            case "disabled":
                holder.tvName.setTextColor(color_disabled);
                holder.tvStatus.setTextColor(color_disabled);
                holder.cardView.setCardBackgroundColor(bg_disabled);
                holder.tvStatus.setText("Tidak Aktif");
                break;
            case "normal":
                holder.tvName.setTextColor(color_default);
                holder.tvStatus.setTextColor(color_default);
                holder.cardView.setCardBackgroundColor(bg_default);
                holder.tvStatus.setText("Normal");
                break;
            case "Unlisted":
                holder.tvName.setTextColor(color_default);
                holder.tvStatus.setTextColor(color_default);
                holder.cardView.setCardBackgroundColor(bg_default);
                holder.tvStatus.setText("Unlisted");
                break;

        }

        holder.tvName.setText(name);
        holder.tvWarna.setText(detailList.get(position).getWarnaName());
        holder.tvUkuran.setText(detailList.get(position).getUkuranName());
        holder.tvPerusahaan.setText(detailList.get(position).getPerusahaanNama());
        holder.tvRuang.setText(detailList.get(position).getBarangruangRuangNama());
        holder.tvBatasCuci.setText(Integer.toString(batascuci));

    }

    @Override
    public int getItemCount() {
        return detailList.size();
    }

    public class DetailsHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        TextView tvBatasCuci, tvWarna, tvName, tvUkuran, tvPerusahaan, tvRuang, tvCount, tvStatus;
        public DetailsHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName);
            tvWarna = itemView.findViewById(R.id.tvWarna);
            tvUkuran = itemView.findViewById(R.id.tvUkuran);
            tvPerusahaan = itemView.findViewById(R.id.tvRS);
            tvRuang = itemView.findViewById(R.id.tvRuang);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvBatasCuci = itemView.findViewById(R.id.tvBatasCuci);

            cardView = itemView.findViewById(R.id.card_view);
        }
    }
}
