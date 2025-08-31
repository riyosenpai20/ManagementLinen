package com.android.management_linen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.management_linen.R;
import com.android.management_linen.helpers.GroupedScan;

import java.util.List;
public class GroupedScanAdapter extends RecyclerView.Adapter<GroupedScanAdapter.GroupedScanHolder>{
    private List<GroupedScan> groupedList;
    private int type_rs;

    public GroupedScanAdapter(List<GroupedScan> groupedList, Context context, int type_rs) {
        this.groupedList = groupedList;
        this.type_rs = type_rs;
    }

    @NonNull
    @Override
    public GroupedScanAdapter.GroupedScanHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_datatable2, parent, false); // Gunakan layout baru (atau reuse)
        return new GroupedScanHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupedScanHolder holder, int position) {
        GroupedScan group = groupedList.get(position);

        holder.txtNama.setText(group.getSubCategoryName());
        holder.txtWarna.setText(group.getWarnaName());
        holder.txtUkuran.setText(group.getUkuranName());
        holder.txtJumlah.setText(String.valueOf(group.getJumlah()));
        holder.txtBerat.setText(String.format("%.2f kg", group.getTotalBerat()));

        if (type_rs != 1) {
            holder.txtRuangan.setText(group.getRuangName());
            holder.txtRuangan.setVisibility(View.VISIBLE);
            holder.lblRuangan.setVisibility(View.VISIBLE);
        } else {
            holder.txtRuangan.setVisibility(View.GONE);
            holder.lblRuangan.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return groupedList.size();
    }

    public class GroupedScanHolder extends RecyclerView.ViewHolder {
        public TextView txtNama, txtWarna, txtUkuran, txtRuangan, txtJumlah, txtBerat, lblRuangan;

        public GroupedScanHolder(@NonNull View itemView) {
            super(itemView);
            txtNama = itemView.findViewById(R.id.txtNama);
            txtWarna = itemView.findViewById(R.id.txtWarna);
            txtUkuran = itemView.findViewById(R.id.txtUkuran);
            txtRuangan = itemView.findViewById(R.id.txtRuangan);
            lblRuangan = itemView.findViewById(R.id.lblRuangan);
            txtJumlah = itemView.findViewById(R.id.txtJumlah);
            txtBerat = itemView.findViewById(R.id.txtBerat);
        }
    }
}
