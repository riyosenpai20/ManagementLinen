package com.android.management_linen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.management_linen.R;
import com.android.management_linen.models.DetailsScanBersih;

import java.util.List;

public class DetailsScanBersihAdapter extends RecyclerView.Adapter<DetailsScanBersihAdapter.DetailScanBersihHolder> {
    private List<DetailsScanBersih> detailsScanBersih;
    private int type_rs;

    public DetailsScanBersihAdapter(List<DetailsScanBersih> detailsScanBersih, Context context, int type_rs) {
        this.detailsScanBersih = detailsScanBersih;
        this.type_rs = type_rs;
    }

    @NonNull
    @Override
    public DetailsScanBersihAdapter.DetailScanBersihHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_datatable, parent,false);
        return new DetailScanBersihHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DetailsScanBersihAdapter.DetailScanBersihHolder holder, int position) {
        String name = detailsScanBersih.get(position).getSubCategoryName();
        String warna = detailsScanBersih.get(position).getWarnaName();
        String ukuran = detailsScanBersih.get(position).getUkuranName();
        String ruang = detailsScanBersih.get(position).getBarangruangRuangNama();
        Double berat = detailsScanBersih.get(position).getBerat();
        holder.txtNama.setText(name);
        holder.txtWarna.setText(warna);
        holder.txtUkuran.setText(ukuran);
        if(type_rs != 1){
            holder.txtRuangan.setText(ruang);
        }
        else {
            holder.txtRuangan.setVisibility(View.GONE);
        }
        holder.txtBerat.setText(berat.toString());
    }

    @Override
    public int getItemCount() {
        return detailsScanBersih.size();
    }

    public class DetailScanBersihHolder extends RecyclerView.ViewHolder{
        public TextView txtNama, txtWarna, txtUkuran, txtRuangan, txtBerat;
        public DetailScanBersihHolder(View itemView) {
            super(itemView);
            txtNama = itemView.findViewById(R.id.txtNama);
            txtWarna = itemView.findViewById(R.id.txtWarna);
            txtUkuran = itemView.findViewById(R.id.txtUkuran);
            txtRuangan = itemView.findViewById(R.id.txtRuangan);
            txtBerat = itemView.findViewById(R.id.txtBerat);
        }
    }
}
