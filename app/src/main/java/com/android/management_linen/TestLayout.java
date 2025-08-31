package com.android.management_linen;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.management_linen.adapter.DetailsScanBersihAdapter;
import com.android.management_linen.models.DetailsScanBersih;

import java.util.ArrayList;
import java.util.List;

public class TestLayout extends AppCompatActivity {
    RecyclerView rv_table;
    DetailsScanBersihAdapter adpDetailsScanBersih;
    private List<DetailsScanBersih> detailsList = new ArrayList<DetailsScanBersih>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_preview);
        rv_table = findViewById(R.id.rv_table);

        detailsList = new ArrayList<DetailsScanBersih>();
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));
        detailsList.add(new DetailsScanBersih(50, 1, "Baju Baru Sekali", "Hijau", "L", "AAA", "Anggrek", "", "","", 2.1 ));



        rv_table.setLayoutManager(new LinearLayoutManager(this));
        adpDetailsScanBersih = new DetailsScanBersihAdapter(detailsList, TestLayout.this, 1);
        rv_table.setAdapter(adpDetailsScanBersih);

    }
}
