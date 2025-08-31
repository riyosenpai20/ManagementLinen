package com.android.management_linen;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.management_linen.models.DetailScanSto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PreviewSTO extends AppCompatActivity {
    private static final String TAG = "PreviewSTO";

    private TableLayout tableLayout;
    private TableLayout tableLayoutUnmatched;
    private TextView tvHospitalName;
    private TextView tvRoom;
    private TextView tvDate;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_sto);
    
        // Set up toolbar
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Result");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        
        // Set navigation icon (back button)
        toolbar.setNavigationIcon(R.drawable.ic_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    
        // Initialize views
        tableLayout = findViewById(R.id.tableLayout);
        tableLayoutUnmatched = findViewById(R.id.tableLayoutUnmatched);
        tvHospitalName = findViewById(R.id.tvHospitalName);
        tvRoom = findViewById(R.id.tvRoom);
        tvDate = findViewById(R.id.tvDate);
    
        // Set current date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        tvDate.setText("Tanggal : " + currentDate);
    
        // Get hospital name and room name from intent
        String hospitalName = getIntent().getStringExtra("hospital_name");
        String roomName = getIntent().getStringExtra("room_name");
        
        // Set hospital name and room name
        if (hospitalName != null && !hospitalName.isEmpty()) {
            tvHospitalName.setText("Nama Rumah Sakit : " + hospitalName);
        }
        
        if (roomName != null && !roomName.isEmpty()) {
            tvRoom.setText("Ruangan : " + roomName);
        }
    
        // Get data from intent
        List<DetailScanSto> stoData = (List<DetailScanSto>) getIntent().getSerializableExtra("sto_data");
        List<DetailScanSto.UnmatchedGroup> unmatchedGroups = 
                (List<DetailScanSto.UnmatchedGroup>) getIntent().getSerializableExtra("unmatched_groups");
        
        if (stoData != null && !stoData.isEmpty()) {
            Log.d(TAG, "Received stoData with " + stoData.size() + " items");
            // Populate tables with data
            populateMainTable(stoData, unmatchedGroups);
            populateUnmatchedTable(stoData);
        } else {
            Log.e(TAG, "stoData is null or empty");
        }
    }
    float txtSize = 12;
    private void populateMainTable(List<DetailScanSto> stoData, List<DetailScanSto.UnmatchedGroup> unmatchedGroups) {
        Log.d(TAG, "Populating main table with " + stoData.size() + " items");
        
        // First add regular STO items
        for (DetailScanSto item : stoData) {
            // Create a new row
            TableRow row = new TableRow(this);
            row.setPadding(5, 10, 5, 10);
            row.setLayoutParams(new TableLayout.LayoutParams(
                    TableLayout.LayoutParams.MATCH_PARENT,
                    TableLayout.LayoutParams.WRAP_CONTENT));
            // Text size

            // Set border for the row
            if (tableLayout.getChildCount() % 2 == 0) {
                row.setBackgroundColor(ContextCompat.getColor(this, R.color.tb_RowOdd_Primary));
            } else {
                row.setBackgroundColor(ContextCompat.getColor(this, R.color.tb_RowEven_Primary));
            }
            
            // Create and add the linen name column
            TextView tvLinen = new TextView(this);
            tvLinen.setText(item.getCategory() + " " + item.getSubCategory() + " " + 
                           item.getColor() + " " + item.getSize());
            tvLinen.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            tvLinen.setGravity(Gravity.LEFT);
            tvLinen.setPadding(5, 5, 5, 5);
            tvLinen.setTextSize(txtSize);
            
            // Create and add the system count column
            TextView tvSystem = new TextView(this);
            tvSystem.setText(String.valueOf(item.getCount()));
            tvSystem.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            tvSystem.setGravity(Gravity.CENTER);
            tvSystem.setPadding(5, 5, 5, 5);
            tvSystem.setTextSize(txtSize);
            
            // Create and add the scan count column
            TextView tvScan = new TextView(this);
            tvScan.setText(String.valueOf(item.getMatchingCount()));
            tvScan.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            tvScan.setGravity(Gravity.CENTER);
            tvScan.setPadding(5, 5, 5, 5);
            tvScan.setTextSize(txtSize);
            
            // Add TextViews to the row
            row.addView(tvLinen);
            row.addView(tvSystem);
            row.addView(tvScan);
            
            // Add the row to the table
            tableLayout.addView(row);
        }
        
        // Then add unmatched groups if available
        if (unmatchedGroups != null && !unmatchedGroups.isEmpty()) {
            Log.d(TAG, "Adding " + unmatchedGroups.size() + " unmatched groups to main table");
            
            for (DetailScanSto.UnmatchedGroup group : unmatchedGroups) {
                // Create a new row
                TableRow row = new TableRow(this);
                row.setPadding(5, 10, 5, 10);
                row.setLayoutParams(new TableLayout.LayoutParams(
                        TableLayout.LayoutParams.MATCH_PARENT,
                        TableLayout.LayoutParams.WRAP_CONTENT));
                
                // Set a different background color for unmatched groups
                row.setBackgroundColor(ContextCompat.getColor(this, R.color.grayslate));
                
                // Create and add the linen name column
                TextView tvLinen = new TextView(this);
                tvLinen.setText(group.getCategory() + " " + group.getSubCategory() + " " + 
                               group.getColor() + " " + group.getSize() + " (Unmatched)");
                tvLinen.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                tvLinen.setGravity(Gravity.LEFT);
                tvLinen.setPadding(5, 5, 5, 5);
                tvLinen.setTextSize(txtSize);
                
                // Create and add the system count column (empty for unmatched groups)
                TextView tvSystem = new TextView(this);
                tvSystem.setText("-");
                tvSystem.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                tvSystem.setGravity(Gravity.CENTER);
                tvSystem.setPadding(5, 5, 5, 5);
                tvSystem.setTextSize(txtSize);
                
                // Create and add the scan count column (showing count of unmatched RFIDs)
                TextView tvScan = new TextView(this);
                tvScan.setText(String.valueOf(group.getCount()));
                tvScan.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                tvScan.setGravity(Gravity.CENTER);
                tvScan.setPadding(5, 5, 5, 5);
                tvScan.setTextSize(txtSize);
                
                // Add TextViews to the row
                row.addView(tvLinen);
                row.addView(tvSystem);
                row.addView(tvScan);
                
                // Add the row to the table
                tableLayout.addView(row);
            }
        }
        
        Log.d(TAG, "Main table populated with " + tableLayout.getChildCount() + " rows");
    }
    
    private void populateUnmatchedTable(List<DetailScanSto> stoData) {
        boolean hasUnmatchedRfids = false;
        int unmatchedCount = 0;
        System.out.println(stoData);
        for (DetailScanSto item : stoData) {
            // Check if there are no matching RFIDs
            List<String> noMatchingRfids = item.getNoMatchingRfids();
            System.out.println(item);
            System.out.println(noMatchingRfids);
            if (noMatchingRfids != null && !noMatchingRfids.isEmpty()) {
                Log.d(TAG, "Found " + noMatchingRfids.size() + " unmatched RFIDs for item: " + 
                      item.getCategory() + " " + item.getSubCategory());
                hasUnmatchedRfids = true;
                for (String rfid : noMatchingRfids) {
                    unmatchedCount++;
                    // Create a new row
                    TableRow row = new TableRow(this);
                    row.setPadding(5, 10, 5, 10);
                    row.setLayoutParams(new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));
                    
                    // Set border for the row
                    if (tableLayoutUnmatched.getChildCount() % 2 == 0) {
                        row.setBackgroundColor(ContextCompat.getColor(this, R.color.tb_RowOdd_Primary));
                    } else {
                        row.setBackgroundColor(ContextCompat.getColor(this, R.color.tb_RowEven_Primary));
                    }
                    
                    // Create and add the RFID column
                    TextView tvRfid = new TextView(this);
                    tvRfid.setText(rfid);
                    tvRfid.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                    tvRfid.setGravity(Gravity.CENTER);
                    tvRfid.setPadding(5, 5, 5, 5);
                    tvRfid.setTextSize(txtSize);
                    
                    // Create and add the linen description column
                    TextView tvLinen = new TextView(this);
                    tvLinen.setText(item.getCategory() + " " + item.getSubCategory() + " " + 
                                   item.getColor() + " " + item.getSize());
                    tvLinen.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                    tvLinen.setGravity(Gravity.LEFT);
                    tvLinen.setPadding(5, 5, 5, 5);
                    tvLinen.setTextSize(txtSize);
                    
                    // Add TextViews to the row
                    row.addView(tvRfid);
                    row.addView(tvLinen);
                    
                    // Add the row to the table
                    tableLayoutUnmatched.addView(row);
                }
            }
        }
        
        Log.d(TAG, "Unmatched table populated with " + unmatchedCount + " rows");
        
        // Hide the unmatched section if there are no unmatched RFIDs
        if (!hasUnmatchedRfids) {
            Log.d(TAG, "No unmatched RFIDs found, hiding unmatched section");
            TextView tvUnmatchedTitle = findViewById(R.id.tvUnmatchedTitle);
            if (tvUnmatchedTitle != null) {
                tvUnmatchedTitle.setVisibility(View.GONE);
            }
            tableLayoutUnmatched.setVisibility(View.GONE);
        }
    }
    
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}