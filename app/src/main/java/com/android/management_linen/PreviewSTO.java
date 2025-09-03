package com.android.management_linen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.management_linen.adapter.PdfDocumentAdapter;
import com.android.management_linen.models.DetailScanSto;
import com.android.management_linen.utils.PrinterUtils;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.android.management_linen.models.DetailScanSto;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


//import android.os.Environment;
//import android.print.PrintAttributes;
//import android.print.PrintManager;
//
//import com.android.management_linen.adapter.PdfDocumentAdapter;
//import com.bumptech.glide.Glide;
//import com.google.android.material.floatingactionbutton.FloatingActionButton;
//import com.itextpdf.kernel.events.Event;
//import com.itextpdf.kernel.events.IEventHandler;
//import com.itextpdf.kernel.events.PdfDocumentEvent;
//import com.itextpdf.kernel.font.PdfFont;
//import com.itextpdf.kernel.font.PdfFontFactory;
//import com.itextpdf.kernel.geom.Rectangle;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfPage;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
//import com.itextpdf.layout.Document;
//import com.itextpdf.layout.element.Cell;
//import com.itextpdf.layout.element.Paragraph;
//import com.itextpdf.layout.element.Table;
//import com.itextpdf.layout.property.TextAlignment;
//import com.itextpdf.layout.property.UnitValue;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.io.OutputStream;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.List;
//import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class PreviewSTO extends AppCompatActivity {
    private static final String TAG = "PreviewSTO";

    private TableLayout tableLayout;
    private TableLayout tableLayoutUnmatched;
    private TableLayout tableLayoutNotRegistered;
    private TextView tvNotRegisteredTitle;
    private TextView tvHospitalName;
    private TextView tvRoom;
    private TextView tvDate;
    private Toolbar toolbar;
    private FloatingActionButton fabPrint;

    // For Connection Bluetooth Printer
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_PERMISSIONS_BT = 2;
    private static final String PRINTER_MAC_ADDRESS = "86:67:7A:5E:95:FA";
    private static final String PRINTER_NAME = "SYNERGI_PRINTER"; 
    private static final UUID PRINTER_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice printerDevice;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private ActivityResultLauncher<Intent> enableBluetoothLauncher;
    private Set<BluetoothDevice> pairedDevices;
    private Dialog loadingDialog;
    
    private String hospitalName;
    private String roomName;
    private List<DetailScanSto> stoData;
    private List<DetailScanSto.UnmatchedGroup> unmatchedGroups;
    private List<DetailScanSto> notRegisteredData;
    private float txtSize = 12;
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
        tableLayoutNotRegistered = findViewById(R.id.tableLayoutNotRegistered);
        tvHospitalName = findViewById(R.id.tvHospitalName);
        tvRoom = findViewById(R.id.tvRoom);
        tvDate = findViewById(R.id.tvDate);
        tvNotRegisteredTitle = findViewById(R.id.tvNotRegisteredTitle);

        fabPrint = findViewById(R.id.fabPrint);

        // Initialize Bluetooth
        initBluetooth();

        // Set current date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        tvDate.setText("Tanggal : " + currentDate);

        enableBluetoothLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Bluetooth is enabled, proceed with your logic
                    } else {
                        // Bluetooth is not enabled, handle accordingly
                    }
                }
        );

        // Get hospital name and room name from intent
        hospitalName = getIntent().getStringExtra("hospital_name");
        roomName = getIntent().getStringExtra("room_name");
        
        // Set hospital name and room name
        if (hospitalName != null && !hospitalName.isEmpty()) {
            tvHospitalName.setText("Nama Rumah Sakit : " + hospitalName);
        }
        
        if (roomName != null && !roomName.isEmpty()) {
            tvRoom.setText("Ruangan : " + roomName);
        }
    
        // Get data from intent
        stoData = (List<DetailScanSto>) getIntent().getSerializableExtra("sto_data");
        unmatchedGroups = 
                (List<DetailScanSto.UnmatchedGroup>) getIntent().getSerializableExtra("unmatched_groups");
        notRegisteredData = 
                (List<DetailScanSto>) getIntent().getSerializableExtra("not_registered");
        
        if (stoData != null && !stoData.isEmpty()) {
            Log.d(TAG, "Received stoData with " + stoData.size() + " items");
            // Populate tables with data
            populateMainTable(stoData, unmatchedGroups);
            populateUnmatchedTable(stoData);
            
            // Populate not registered table if data exists
            if (notRegisteredData != null && !notRegisteredData.isEmpty()) {
                populateNotRegisteredTable(notRegisteredData);
            } else {
                // Hide not registered section if no data
                tvNotRegisteredTitle.setVisibility(View.GONE);
                tableLayoutNotRegistered.setVisibility(View.GONE);
            }
        } else {
            Log.e(TAG, "stoData is null or empty");
        }
        
        // Setup PDF button
        findViewById(R.id.fabPdf).setOnClickListener(v -> {
            try {
                File pdfFile = generatePdf(stoData, unmatchedGroups, hospitalName, roomName);
                printPdf(pdfFile);
            } catch (IOException ex) {
                Log.e(TAG, "Error generating PDF: " + ex.getMessage());
                // Show error dialog
                new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Perhatian")
                    .setMessage("Gagal membuat PDF: " + ex.getMessage())
                    .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                    .show();
            }
        });

        // Setup Print button
        fabPrint.setOnClickListener(v -> {
            Log.d(TAG, "fabPrint clicked");
            if(setBTSocket()) {
                Log.d(TAG, "setBTSocket returned true, calling printReceipt");
                printReceipt();
                new Handler(Looper.getMainLooper()).postDelayed(this::disconnectBluetooth, 500);
            } else {
                Log.e(TAG, "setBTSocket returned false");
            }
        });
    }

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
            row.setBackgroundColor(ContextCompat.getColor(this, R.color.tb_RowMain_Primary));
//            if (tableLayout.getChildCount() % 2 == 0) {
//                row.setBackgroundColor(ContextCompat.getColor(this, R.color.tb_RowOdd_Primary));
//            } else {
//                row.setBackgroundColor(ContextCompat.getColor(this, R.color.tb_RowEven_Primary));
//            }
            
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
            tvSystem.setText(String.valueOf(item.getCount() + + item.getNoMatchTagsCount()));
            tvSystem.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
            tvSystem.setGravity(Gravity.CENTER);
            tvSystem.setPadding(5, 5, 5, 5);
            tvSystem.setTextSize(txtSize);
            
            // Create and add the scan count column
            TextView tvScan = new TextView(this);
            tvScan.setText(String.valueOf(item.getMatchingCount() + item.getNoMatchTagsCount()));
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
                row.setBackgroundColor(ContextCompat.getColor(this, R.color.tb_RowNewGroup_Primary));
//                if (tableLayout.getChildCount() % 2 == 0) {
//                    row.setBackgroundColor(ContextCompat.getColor(this, R.color.tb_RowEven2_Primary));
//                }
//                else {
//                    row.setBackgroundColor(ContextCompat.getColor(this, R.color.tb_RowOdd2_Primary));
//                }
                // Create and add the linen name column
                TextView tvLinen = new TextView(this);
                tvLinen.setText(group.getCategory() + " " + group.getSubCategory() + " " + 
                               group.getColor() + " " + group.getSize());
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
                    row.setBackgroundColor(ContextCompat.getColor(this, R.color.tb_RowUnknown_Primary));
//                    if (tableLayoutUnmatched.getChildCount() % 2 == 0) {
//                        row.setBackgroundColor(ContextCompat.getColor(this, R.color.tb_RowOdd_Primary));
//                    } else {
//                        row.setBackgroundColor(ContextCompat.getColor(this, R.color.tb_RowEven_Primary));
//                    }
                    
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
    
    private void populateNotRegisteredTable(List<DetailScanSto> notRegisteredData) {
        if (notRegisteredData == null || notRegisteredData.isEmpty()) {
            Log.d(TAG, "No not registered data found, hiding not registered section");
            tvNotRegisteredTitle.setVisibility(View.GONE);
            tableLayoutNotRegistered.setVisibility(View.GONE);
            return;
        }
        
        Log.d(TAG, "Populating not registered table with " + notRegisteredData.size() + " items");
        
        for (DetailScanSto item : notRegisteredData) {
            // Process no_match_tags for not registered items
            List<String> noMatchTags = item.getNoMatchTags();
            
            if (noMatchTags != null && !noMatchTags.isEmpty()) {
                for (String rfid : noMatchTags) {
                    // Create a new row
                    TableRow row = new TableRow(this);
                    row.setPadding(5, 10, 5, 10);
                    row.setLayoutParams(new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.MATCH_PARENT,
                            TableLayout.LayoutParams.WRAP_CONTENT));
                    
                    // Set background color for the row
                    row.setBackgroundColor(ContextCompat.getColor(this, R.color.tb_RowUnknown_Primary));
                    
                    // Create and add the RFID column
                    TextView tvRfid = new TextView(this);
                    tvRfid.setText(rfid);
                    tvRfid.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                    tvRfid.setGravity(Gravity.CENTER);
                    tvRfid.setPadding(5, 5, 5, 5);
                    tvRfid.setTextSize(txtSize);
                    
                    // Create and add the status column
                    TextView tvStatus = new TextView(this);
                    tvStatus.setText("Belum Terdaftar");
                    tvStatus.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                    tvStatus.setGravity(Gravity.CENTER);
                    tvStatus.setPadding(5, 5, 5, 5);
                    tvStatus.setTextSize(txtSize);
                    
                    // Add TextViews to the row
                    row.addView(tvRfid);
                    row.addView(tvStatus);
                    
                    // Add the row to the table
                    tableLayoutNotRegistered.addView(row);
                }
            }
        }
        
        Log.d(TAG, "Not registered table populated");
        
        // Show the not registered section
        tvNotRegisteredTitle.setVisibility(View.VISIBLE);
        tableLayoutNotRegistered.setVisibility(View.VISIBLE);
    }
    
    private void initBluetooth() {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
                bluetoothAdapter = bluetoothManager.getAdapter();
            } else {
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            }
    
            if (bluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth tidak tersedia pada perangkat ini", Toast.LENGTH_SHORT).show();
                return;
            }
    
            if (!bluetoothAdapter.isEnabled()) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                    Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
                    Log.d(TAG, "Paired devices count: " + pairedDevices.size());
                    for (BluetoothDevice device : pairedDevices) {
                        Log.d(TAG, "Paired device: " + device.getName() + " - " + device.getAddress());
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error initializing Bluetooth: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void findBluetoothDevice() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals(PRINTER_NAME)) {
                        printerDevice = device;

                        break;
                    }
                }
            }
        }
    }

    private void findPairedPrinter() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12 (API level 31) and higher
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
            }
        } else {
            // Android 11 (API level 30) and lower
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH/*, Manifest.permission.BLUETOOTH_ADMIN*/}, REQUEST_ENABLE_BT);
            }
        }
        pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    System.out.println("device.getName() : " + device.getAlias().toString());
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    if (device.getAlias().toString().equals(PRINTER_NAME)) {
                        printerDevice = device;
                        break;
                    }
                }
            }
        }
        System.out.println("Nama Printer : " + printerDevice);
        System.out.println("pairedDevices : " + pairedDevices);
    }

    private boolean connectToPrinter() {
        findPairedPrinter(); // pastikan ini tidak null-kan printerDevice
        System.out.println("Konek ke Printer : " + printerDevice);
        if(printerDevice == null) {
            alertDialog("Printer Tidak ditemukan", "Tidak ada perangkat printer yang cocok dengan nama yang ditentukan.");
            return false;
        }
        bluetoothSocket = null;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                // Android 12 (API level 31) and higher
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_ENABLE_BT);
                }
            } else {
                // Android 11 (API level 30) and lower
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH/*, Manifest.permission.BLUETOOTH_ADMIN*/}, REQUEST_ENABLE_BT);
                }
            }
            bluetoothSocket = printerDevice.createRfcommSocketToServiceRecord(PRINTER_UUID);
            try {
                bluetoothAdapter.cancelDiscovery();
            } catch (SecurityException se) {
                se.printStackTrace();
            }
            try {
                bluetoothSocket.connect();
                outputStream = bluetoothSocket.getOutputStream();
                System.out.println("menghubungkan bluetooth socket");
                return true;
            } catch (IOException e) {
                try {
                    bluetoothSocket.close();
                    System.out.println("menutup bluetooth socket");
                } catch (IOException e1) {
                    System.out.println("Error: " + e1);
                }
                alertDialog("Perhatian", "Ada kemungkinan printer dalam keadaan mati, silahkan periksa kembali.");
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
            alertDialog("Error", "Terjadi kesalahan saat menghubungkan ke printer.");
            return false;
        }
    }

    private boolean setBTSocket() {
        findBluetoothDevice();
        // Percobaan
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12 (API level 31) and higher
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN}, REQUEST_ENABLE_BT);
            } else {
                findPairedPrinter();
            }
        } else {
            // Android 11 (API level 30) and lower
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, REQUEST_ENABLE_BT);
            } else {
                findPairedPrinter();
            }
        }

        return connectToPrinter();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    private void printReceipt() {
        Log.d(TAG, "printReceipt started");
        try {
            int[] separatorWidth = {48}; // Adjust as necessary
            // Print header
            String header = "STOCK OPNAME\n";
            Log.d(TAG, "Writing header: " + header);
            outputStream.write(new byte[]{0x1B, 0x61, 0x01}); // Center align
            outputStream.write(new byte[]{0x1B, 0x45, 0x01}); // Bold start
            outputStream.write(new byte[]{0x1D, 0x21, 0x10}); // Double height + width
            outputStream.write(header.getBytes("UTF-8"));

            outputStream.write(new byte[]{0x1D, 0x21, 0x00}); // Normal height + width
            String hospitalLine = hospitalName + "\n\n";
            outputStream.write(hospitalLine.getBytes("UTF-8"));
            outputStream.write(new byte[]{0x1B, 0x45, 0x00}); // Bold end
            outputStream.write(new byte[]{0x1B, 0x61, 0x00}); // Center align

            String roomLine = "Ruangan : " + roomName + "\n";
            outputStream.write(roomLine.getBytes("UTF-8"));
            
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            String currentDate = sdf.format(new Date());
            String dateLine = "Tanggal : " + currentDate + "\n\n";
            outputStream.write(dateLine.getBytes("UTF-8"));
            
            // Print table header
            String tableHeader = "LINEN                            SISTEM  SCAN\n";
            outputStream.write(tableHeader.getBytes("UTF-8"));
            PrinterUtils.printCustomSeparatorLine(outputStream, separatorWidth, "-");
            
            // Print table data
            for (int i = 0; i < stoData.size(); i++) {
                DetailScanSto item = stoData.get(i);
                String linenName = item.getSubCategory() + " " +
                                  item.getColor() + " " + item.getSize();
                
                String systemCount = String.valueOf(item.getCount() + item.getNoMatchTagsCount());
                String scanCount = String.valueOf(item.getMatchingCount() + item.getNoMatchTagsCount());
                
                if (linenName.length() > 32) {
                    // Bagi teks berdasarkan jumlah huruf
                    String firstLine = linenName.substring(0, 32);
                    String secondLine = linenName.substring(32);

                    String line = String.format("%-32s %-7s %-7s\n", firstLine, systemCount, scanCount);
                    outputStream.write(line.getBytes("UTF-8"));

                    String continuationLine = String.format("%-32s\n", secondLine);
                    outputStream.write(continuationLine.getBytes("UTF-8"));
                }
                else {
                    String line = String.format("%-32s %-7s %-7s\n", linenName, systemCount, scanCount);
                    outputStream.write(line.getBytes("UTF-8"));
                }
            }

            PrinterUtils.printCustomSeparatorLine(outputStream, separatorWidth, "-");
            
            // Print unmatched items if any
            boolean hasUnmatchedRfids = false;
            for (DetailScanSto item : stoData) {
                List<String> noMatchingRfids = item.getNoMatchingRfids();
                if (noMatchingRfids != null && !noMatchingRfids.isEmpty()) {
                    hasUnmatchedRfids = true;
                    break;
                }
            }
            if (hasUnmatchedRfids) {
                outputStream.write(new byte[]{0x1B, 0x45, 0x01}); // Bold end
                outputStream.write("\nLINEN TIDAK DIKETAHUI\n".getBytes("UTF-8"));
                outputStream.write(new byte[]{0x1B, 0x45, 0x00}); // Bold end
                PrinterUtils.printCustomSeparatorLine(outputStream, separatorWidth, "-");

                // Print header for unmatched items
                String unmatchedHeader = String.format("%-26s %-21s\n", "RFID", "Linen");
                outputStream.write(unmatchedHeader.getBytes("UTF-8"));
                PrinterUtils.printCustomSeparatorLine(outputStream, separatorWidth, "-");

                for (DetailScanSto item : stoData) {
                    List<String> noMatchingRfids = item.getNoMatchingRfids();
                    if (noMatchingRfids != null && !noMatchingRfids.isEmpty()) {
                        for (String rfid : noMatchingRfids) {
                            String linenName = item.getSubCategory() + " " + item.getColor() + " " + item.getSize();

                            // Implementasi text wrap untuk kolom Linen
                            if (linenName.length() > 21) {
                                // Cari posisi spasi terakhir sebelum karakter ke-21
                                int lastSpacePos = linenName.substring(0, Math.min(21, linenName.length())).lastIndexOf(" ");

                                if (lastSpacePos > 0) {
                                    // Bagi teks pada spasi terakhir
                                    String firstLine = linenName.substring(0, lastSpacePos);
                                    String secondLine = linenName.substring(lastSpacePos + 1);

                                    // Cetak baris pertama dengan RFID
                                    String line = String.format("%-26s %-21s", rfid, firstLine);
                                    outputStream.write(line.getBytes("UTF-8"));
                                    outputStream.write(new byte[]{0x0A}); // Line feed

                                    // Cetak baris kedua tanpa RFID
                                    String continuationLine = String.format("%-26s %-21s", "", secondLine);
                                    outputStream.write(continuationLine.getBytes("UTF-8"));
                                    outputStream.write(new byte[]{0x0A}); // Line feed
                                } else {
                                    // Jika tidak ada spasi, potong di karakter ke-21
                                    String firstLine = linenName.substring(0, 21);
                                    String secondLine = linenName.substring(21);

                                    // Cetak baris pertama dengan RFID
                                    String line = String.format("%-26s %-21s", rfid, firstLine);
                                    outputStream.write(line.getBytes("UTF-8"));
                                    outputStream.write(new byte[]{0x0A}); // Line feed

                                    // Cetak baris kedua tanpa RFID
                                    String continuationLine = String.format("%-26s %-21s", "", secondLine);
                                    outputStream.write(continuationLine.getBytes("UTF-8"));
                                    outputStream.write(new byte[]{0x0A}); // Line feed
                                }
                            } else {
                                // Cetak dalam satu baris jika teks pendek
                                String line = String.format("%-26s %-21s", rfid, linenName);
                                outputStream.write(line.getBytes("UTF-8"));
                                outputStream.write(new byte[]{0x0A}); // Line feed
                            }
                        }
                    }
                }

                PrinterUtils.printCustomSeparatorLine(outputStream, separatorWidth, "-");
            }
            
            // Print not registered items if any
            if (notRegisteredData != null && !notRegisteredData.isEmpty()) {
                outputStream.write("\nLINEN TIDAK TERDAFTAR:\n".getBytes("UTF-8"));
                PrinterUtils.printCustomSeparatorLine(outputStream, separatorWidth, "-");
                
                // Print header for not registered items
                String notRegisteredHeader = String.format("%-26s %-21s\n", "RFID", "Linen");
                outputStream.write(notRegisteredHeader.getBytes("UTF-8"));
                PrinterUtils.printCustomSeparatorLine(outputStream, separatorWidth, "-");

                for (DetailScanSto item : notRegisteredData) {
                    List<String> noMatchTags = item.getNoMatchTags();
                    if (noMatchTags != null && !noMatchTags.isEmpty()) {
                        for (String rfid : noMatchTags) {
                            String linenName = item.getSubCategory() + " " + item.getColor() + " " + item.getSize();
                            String line = String.format("%-26s %-21s\n", rfid, linenName);
                            outputStream.write(line.getBytes("UTF-8"));
                        }
                    }
                }

                PrinterUtils.printCustomSeparatorLine(outputStream, separatorWidth, "-");
            }
            
            // Print footer
            outputStream.write(new byte[]{0x1B, 0x61, 0x01}); // Center align
            outputStream.write("\n".getBytes("UTF-8"));
            outputStream.write("TTD Ruangan:           TTD Laundry:\n\n\n\n".getBytes("UTF-8"));
            outputStream.write("____________          ____________\n\n".getBytes("UTF-8"));
            outputStream.write(new byte[]{0x1B, 0x61, 0x00}); // Center align

            Toast.makeText(this, "Berhasil mencetak", Toast.LENGTH_SHORT).show();
            
        } catch (IOException e) {
            Log.e(TAG, "Error in printReceipt: " + e.getMessage());
            e.printStackTrace();
            Toast.makeText(this, "Gagal mencetak: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void showLoadingDialog() {
        loadingDialog = new Dialog(PreviewSTO.this);
        loadingDialog.setContentView(R.layout.dialog_loading);
        loadingDialog.setCancelable(false);

        ImageView loadingImage = loadingDialog.findViewById(R.id.loading_image);
        Glide.with(PreviewSTO.this).asGif().load(R.drawable.loading).into(loadingImage);
        loadingDialog.show();
    }
    
    private void alertDialog(String title, String message) {
        new AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
            .show();
    }
    
    private void disconnectBluetooth() {
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
                Log.d(TAG, "Bluetooth socket closed");
            }
        } catch (IOException e) {
            Log.e(TAG, "Error closing bluetooth socket: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectBluetooth();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        // Check if bluetooth is enabled
        if (bluetoothAdapter != null && !bluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                enableBluetoothLauncher.launch(enableBtIntent);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_PERMISSIONS_BT);
            }
        }
    }

    private File generatePdf(List<DetailScanSto> stoData, List<DetailScanSto.UnmatchedGroup> unmatchedGroups, 
                            String hospitalName, String roomName) throws IOException {
        File pdfFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), "sto_report.pdf");
        PdfWriter writer = new PdfWriter(Files.newOutputStream(pdfFile.toPath()));
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        
        // Add page number
        PageNumberEventHandler pageNumberHandler = new PageNumberEventHandler(pdfDoc);
        pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, pageNumberHandler);
        
        // Add title
        Paragraph title = new Paragraph("STOCK OPNAME");
        title.setFontSize(18);
        title.setBold();
        title.setTextAlignment(TextAlignment.CENTER);
        document.add(title);
        
        // Add hospital name
        Paragraph hospitalPara = new Paragraph("Nama Rumah Sakit : " + hospitalName);
        hospitalPara.setFontSize(12);
        document.add(hospitalPara);
        
        // Add room name
        Paragraph roomPara = new Paragraph("Ruangan : " + roomName);
        roomPara.setFontSize(12);
        document.add(roomPara);
        
        // Add date
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
        String currentDate = sdf.format(new Date());
        Paragraph datePara = new Paragraph("Tanggal : " + currentDate);
        datePara.setFontSize(12);
        document.add(datePara);
        
        document.add(new Paragraph("\n"));
        
        // Create main table
        Table table = new Table(UnitValue.createPercentArray(new float[]{3, 1, 1}));
        table.setWidth(UnitValue.createPercentValue(100));
        
        // Add table headers
        table.addHeaderCell(new Cell().add(new Paragraph("Linen")).setTextAlignment(TextAlignment.CENTER).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("System")).setTextAlignment(TextAlignment.CENTER).setBold());
        table.addHeaderCell(new Cell().add(new Paragraph("Scan")).setTextAlignment(TextAlignment.CENTER).setBold());
        
        // Add data rows
        for (DetailScanSto item : stoData) {
            table.addCell(new Cell().add(new Paragraph(item.getCategory() + " " + item.getSubCategory() + " " + 
                                                      item.getColor() + " " + item.getSize())));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getCount() + item.getNoMatchTagsCount())))
                         .setTextAlignment(TextAlignment.CENTER));
            table.addCell(new Cell().add(new Paragraph(String.valueOf(item.getMatchingCount() + item.getNoMatchTagsCount())))
                         .setTextAlignment(TextAlignment.CENTER));
        }
        
        // Add unmatched groups if available
        if (unmatchedGroups != null && !unmatchedGroups.isEmpty()) {
            for (DetailScanSto.UnmatchedGroup group : unmatchedGroups) {
                table.addCell(new Cell().add(new Paragraph(group.getCategory() + " " + group.getSubCategory() + " " + 
                                                          group.getColor() + " " + group.getSize())));
                table.addCell(new Cell().add(new Paragraph("-")).setTextAlignment(TextAlignment.CENTER));
                table.addCell(new Cell().add(new Paragraph(String.valueOf(group.getCount())))
                             .setTextAlignment(TextAlignment.CENTER));
            }
        }
        
        document.add(table);
        
        // Add unmatched RFIDs section if needed
        boolean hasUnmatchedRfids = false;
        for (DetailScanSto item : stoData) {
            List<String> noMatchingRfids = item.getNoMatchingRfids();
            if (noMatchingRfids != null && !noMatchingRfids.isEmpty()) {
                hasUnmatchedRfids = true;
                break;
            }
        }
        
        if (hasUnmatchedRfids) {
            document.add(new Paragraph("\n"));
            Paragraph unmatchedTitle = new Paragraph("Linen Tidak Diketahui");
            unmatchedTitle.setBold();
            unmatchedTitle.setFontSize(14);
            document.add(unmatchedTitle);
            
            Table unmatchedTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}));
            unmatchedTable.setWidth(UnitValue.createPercentValue(100));
            
            // Add table headers
            unmatchedTable.addHeaderCell(new Cell().add(new Paragraph("RFID")).setTextAlignment(TextAlignment.CENTER).setBold());
            unmatchedTable.addHeaderCell(new Cell().add(new Paragraph("Linen")).setTextAlignment(TextAlignment.CENTER).setBold());
            
            // Add unmatched RFIDs
            for (DetailScanSto item : stoData) {
                List<String> noMatchingRfids = item.getNoMatchingRfids();
                if (noMatchingRfids != null && !noMatchingRfids.isEmpty()) {
                    for (String rfid : noMatchingRfids) {
                        unmatchedTable.addCell(new Cell().add(new Paragraph(rfid)).setTextAlignment(TextAlignment.CENTER));
                        unmatchedTable.addCell(new Cell().add(new Paragraph(item.getCategory() + " " + item.getSubCategory() + " " + 
                                                                          item.getColor() + " " + item.getSize())));
                    }
                }
            }
            
            document.add(unmatchedTable);
        }

        // Add not_register RFIDs section if needed
        boolean hasNotRegisterRfids = false;
        for (DetailScanSto item : notRegisteredData) {
            List<String> noMatchingTags = item.getNoMatchTags();
            if (noMatchingTags != null && !noMatchingTags.isEmpty()) {
                hasNotRegisterRfids = true;
                break;
            }
        }
        if(hasNotRegisterRfids) {
            document.add(new Paragraph("\n"));
            Paragraph unmatchedTitle = new Paragraph("Linen Tidak Terdaftar");
            unmatchedTitle.setBold();
            unmatchedTitle.setFontSize(14);
            document.add(unmatchedTitle);

            Table notRegisterTable = new Table(UnitValue.createPercentArray(new float[]{1, 2}));
            notRegisterTable.setWidth(UnitValue.createPercentValue(100));

            // Add table headers
            notRegisterTable.addHeaderCell(new Cell().add(new Paragraph("RFID")).setTextAlignment(TextAlignment.CENTER).setBold());
            notRegisterTable.addHeaderCell(new Cell().add(new Paragraph("Linen")).setTextAlignment(TextAlignment.CENTER).setBold());

            // Add unmatched RFIDs
            for (DetailScanSto item : notRegisteredData) {
                List<String> noMatchingTags = item.getNoMatchTags();
                if (noMatchingTags != null && !noMatchingTags.isEmpty()) {
                    for (String rfid : noMatchingTags) {
                        notRegisterTable.addCell(new Cell().add(new Paragraph(rfid)).setTextAlignment(TextAlignment.CENTER));
                        notRegisterTable.addCell(new Cell().add(new Paragraph(item.getCategory() + " " + item.getSubCategory() + " " +
                                item.getColor() + " " + item.getSize())));
                    }
                }
            }

            document.add(notRegisterTable);
        }

        
        document.close();
        return pdfFile;
    }
    
    private void printPdf(File pdfFile) {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        try {
            PrintAttributes printAttributes = new PrintAttributes.Builder()
                    .setMediaSize(PrintAttributes.MediaSize.ISO_A4)
                    .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
                    .setResolution(new PrintAttributes.Resolution("res1", "Resolution", 600, 600))
                    .setMinMargins(PrintAttributes.Margins.NO_MARGINS)
                    .build();
            PdfDocumentAdapter pdfDocumentAdapter = new PdfDocumentAdapter(this, pdfFile.getAbsolutePath());
            printManager.print("Document", pdfDocumentAdapter, printAttributes);
        } catch (Exception e) {
            Log.e(TAG, "Error printing PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private class PageNumberEventHandler implements IEventHandler {
        private PdfFont font;
        private PdfDocument pdfDoc;

        public PageNumberEventHandler(PdfDocument pdfDoc) throws IOException {
            this.font = PdfFontFactory.createFont();
            this.pdfDoc = pdfDoc;
        }

        @SuppressLint("DefaultLocale")
        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfPage page = docEvent.getPage();
            int pageNumber = pdfDoc.getPageNumber(page);
            int totalPages = pdfDoc.getNumberOfPages();
            Rectangle pageSize = page.getPageSize();
            PdfCanvas pdfCanvas = new PdfCanvas(page);

            pdfCanvas.beginText()
                    .setFontAndSize(font, 12)
                    .moveText(pageSize.getWidth() - 100, pageSize.getBottom() + 20)
                    .showText(String.format("%d / %d", pageNumber, totalPages))
                    .endText()
                    .release();
        }
    }
}