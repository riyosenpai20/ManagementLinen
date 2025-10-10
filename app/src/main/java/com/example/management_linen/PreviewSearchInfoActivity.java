package com.example.management_linen;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

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
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.management_linen.models.DetailScanInfo;
import com.example.management_linen.models.ResponseInfo;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class PreviewSearchInfoActivity extends AppCompatActivity {
    private static final String TAG = "PreviewSearchInfoActivity";
    
    private TableLayout tableLayout;
    private TextView tvRumahSakit;
    private TextView tvTotalItems;
    private String namaRuang;
    private String mode;
    private ResponseInfo responseData;
    private FloatingActionButton fabPrint;
    private FloatingActionButton fabPdf;
    
    // SharedPreferences
    private SharedPreferences sharedpreferences;
    public static final String SHARED_PREFS = "shared_prefs";
    private String token;
    private String namaPerusahaan = "";
    private int idRuang;
    private String lokasi;
    
    // For Bluetooth Printer
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_search_info);

        // Initialize views
        tableLayout = findViewById(R.id.tableLayout);
        tvRumahSakit = findViewById(R.id.tvRumahSakit);
        tvTotalItems = findViewById(R.id.tvTotalItems);
        fabPrint = findViewById(R.id.fabPrint);
        fabPdf = findViewById(R.id.fabPdf);
        
        // Initialize SharedPreferences
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedpreferences.getString("token", null);
        namaPerusahaan = sharedpreferences.getString("namaPerusahaan", "");
        idRuang = sharedpreferences.getInt("idRuang", 0);
        
        // Initialize Bluetooth
        initBluetooth();
        
        enableBluetoothLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Bluetooth is enabled, proceed with your logic
                        Toast.makeText(this, "Bluetooth berhasil diaktifkan", Toast.LENGTH_SHORT).show();
                    } else {
                        // Bluetooth is not enabled, handle accordingly
                        Toast.makeText(this, "Bluetooth harus diaktifkan untuk mencetak", Toast.LENGTH_SHORT).show();
                    }
                }
        );

        // Get data from intent
        if (getIntent().hasExtra("responseData")) {
            responseData = (ResponseInfo) getIntent().getSerializableExtra("responseData");
        }
        
        if (getIntent().hasExtra("namaRuang")) {
            namaRuang = getIntent().getStringExtra("namaRuang");
            tvRumahSakit.setText(namaRuang);
            lokasi = namaRuang;
        }
        
        if (getIntent().hasExtra("mode")) {
            mode = getIntent().getStringExtra("mode");
        }

        // Display data based on mode
        if (responseData != null && responseData.getData() != null) {
            if ("summary".equals(mode)) {
                displaySummaryData(responseData.getData());
            } else {
                // For detail mode, implement if needed
            }
        }
        
        // Setup PDF button
        if (fabPdf != null) {
            fabPdf.setOnClickListener(v -> {
                try {
                    File pdfFile = generatePdf(responseData.getData(), namaPerusahaan, lokasi);
                    printPdf(pdfFile);
                } catch (IOException ex) {
                    Log.e(TAG, "Error generating PDF: " + ex.getMessage());
                    // Show error dialog
                    new AlertDialog.Builder(this)
                        .setTitle("Perhatian")
                        .setMessage("Gagal membuat PDF: " + ex.getMessage())
                        .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                        .show();
                }
            });
        }

        // Setup Print button
        if (fabPrint != null) {
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
    }

    private void displaySummaryData(List<DetailScanInfo> data) {
        // Create a map to aggregate linen by category
        Map<String, Integer> linenSummary = new HashMap<>();
        int totalItems = 0;

        // Aggregate data
        for (DetailScanInfo item : data) {
            String key = item.getCategory();
            if (item.getSubCategory() != null && !item.getSubCategory().isEmpty()) {
                key += " " + item.getSubCategory();
            }
            if (item.getColor() != null && !item.getColor().isEmpty()) {
                key += " " + item.getColor();
            }
            if (item.getSize() != null && !item.getSize().isEmpty()) {
                key += " " + item.getSize();
            }
            
            int count = item.getCount();
            linenSummary.put(key, linenSummary.getOrDefault(key, 0) + count);
            totalItems += count;
        }

        // Update total items text
        tvTotalItems.setText("Total Items: " + totalItems);

        // Add data rows to table
        for (Map.Entry<String, Integer> entry : linenSummary.entrySet()) {
            TableRow row = new TableRow(this);
            row.setPadding(5, 5, 5, 5);
            
            // Alternate row colors for better readability
            if (tableLayout.getChildCount() % 2 == 1) {
                row.setBackgroundColor(Color.parseColor("#F0F0F0"));
            }

            // Linen name column
            TextView tvLinen = new TextView(this);
            tvLinen.setText(entry.getKey());
            tvLinen.setPadding(5, 5, 5, 5);
            tvLinen.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f));
            
            // Count column
            TextView tvCount = new TextView(this);
            tvCount.setText(String.valueOf(entry.getValue()));
            tvCount.setPadding(5, 5, 5, 5);
            tvCount.setGravity(Gravity.CENTER);
            tvCount.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));

            // Add views to row
            row.addView(tvLinen);
            row.addView(tvCount);
            
            // Add row to table
            tableLayout.addView(row);
        }
    }
    
    // Initialize Bluetooth
    private void initBluetooth() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
            bluetoothAdapter = bluetoothManager.getAdapter();
        } else {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        }
        
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(this, "Perangkat tidak mendukung Bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Check if Bluetooth is enabled
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBluetoothLauncher.launch(enableBtIntent);
        }
    }
    
    // Set up Bluetooth socket
    @SuppressLint("MissingPermission")
    private boolean setBTSocket() {
        try {
            if (bluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth tidak tersedia", Toast.LENGTH_SHORT).show();
                return false;
            }
            
            if (!bluetoothAdapter.isEnabled()) {
                Toast.makeText(this, "Bluetooth tidak aktif", Toast.LENGTH_SHORT).show();
                return false;
            }
            
            // Get paired devices
            pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName().equals(PRINTER_NAME) || device.getAddress().equals(PRINTER_MAC_ADDRESS)) {
                        printerDevice = device;
                        break;
                    }
                }
            }
            
            if (printerDevice == null) {
                Toast.makeText(this, "Printer tidak ditemukan", Toast.LENGTH_SHORT).show();
                return false;
            }
            
            // Connect to the printer
            bluetoothSocket = printerDevice.createRfcommSocketToServiceRecord(PRINTER_UUID);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal terhubung ke printer: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    
    // Disconnect Bluetooth
    private void disconnectBluetooth() {
        try {
            if (outputStream != null) {
                outputStream.close();
            }
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // Print receipt
    private void printReceipt() {
        try {
            if (outputStream == null) {
                Toast.makeText(this, "Printer tidak terhubung", Toast.LENGTH_SHORT).show();
                return;
            }
            
            // Get current date and time
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            String currentDateTime = dateFormat.format(new Date());
            
            // Create receipt content
            StringBuilder receiptBuilder = new StringBuilder();
            receiptBuilder.append("================================\n");
            receiptBuilder.append("         LAPORAN LINEN\n");
            receiptBuilder.append("================================\n\n");
            receiptBuilder.append("Tanggal: ").append(currentDateTime).append("\n");
            receiptBuilder.append("Lokasi: ").append(lokasi != null ? lokasi : "-").append("\n\n");
            receiptBuilder.append("--------------------------------\n");
            receiptBuilder.append("Linen                    Jumlah\n");
            receiptBuilder.append("--------------------------------\n");
            
            // Add data rows
            int totalItems = 0;
            for (int i = 1; i < tableLayout.getChildCount(); i++) {
                TableRow row = (TableRow) tableLayout.getChildAt(i);
                if (row.getChildCount() >= 2) {
                    TextView tvLinen = (TextView) row.getChildAt(0);
                    TextView tvCount = (TextView) row.getChildAt(1);
                    
                    String linen = tvLinen.getText().toString();
                    String count = tvCount.getText().toString();
                    
                    // Format to align columns
                    receiptBuilder.append(String.format("%-25s %5s\n", linen, count));
                    
                    try {
                        totalItems += Integer.parseInt(count);
                    } catch (NumberFormatException e) {
                        // Ignore parsing errors
                    }
                }
            }
            
            receiptBuilder.append("--------------------------------\n");
            receiptBuilder.append(String.format("Total Items: %d\n", totalItems));
            receiptBuilder.append("================================\n\n\n");
            
            // Send to printer
            outputStream.write(receiptBuilder.toString().getBytes());
            outputStream.flush();
            
            Toast.makeText(this, "Berhasil mencetak", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Gagal mencetak: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    // Generate PDF file
    private File generatePdf(List<DetailScanInfo> data, String namaPerusahaan, String lokasi) throws IOException {
        // Create directory for PDF if it doesn't exist
        File pdfDir = new File(getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "LinenReports");
        if (!pdfDir.exists()) {
            pdfDir.mkdirs();
        }
        
        // Create PDF file
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "LinenReport_" + timeStamp + ".pdf";
        File pdfFile = new File(pdfDir, fileName);
        
        // Create PDF document
        PdfWriter writer = new PdfWriter(pdfFile);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        try {
            // Add header
            Paragraph header = new Paragraph("LAPORAN LINEN")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(16)
                    .setBold();
            document.add(header);
            
            // Add company name
            Paragraph company = new Paragraph(namaPerusahaan)
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14);
            document.add(company);
            
            // Add date and location
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
            String currentDateTime = dateFormat.format(new Date());
            
            Paragraph dateTime = new Paragraph("Tanggal: " + currentDateTime)
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(10);
            document.add(dateTime);
            
            Paragraph location = new Paragraph("Lokasi: " + (lokasi != null ? lokasi : "-"))
                    .setTextAlignment(TextAlignment.LEFT)
                    .setFontSize(10);
            document.add(location);
            
            document.add(new Paragraph("\n"));
            
            // Create table
            Table table = new Table(UnitValue.createPercentArray(new float[]{75, 25}))
                    .setWidth(UnitValue.createPercentValue(100));
            
            // Add table header
            Cell headerCell1 = new Cell()
                    .add(new Paragraph("Linen"))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold();
            
            Cell headerCell2 = new Cell()
                    .add(new Paragraph("Jumlah"))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setBold();
            
            table.addHeaderCell(headerCell1);
            table.addHeaderCell(headerCell2);
            
            // Add data rows
            int totalItems = 0;
            
            // Create a map to aggregate linen by category
            Map<String, Integer> linenSummary = new HashMap<>();
            
            // Aggregate data
            for (DetailScanInfo item : data) {
                String key = item.getCategory();
                if (item.getSubCategory() != null && !item.getSubCategory().isEmpty()) {
                    key += " " + item.getSubCategory();
                }
                if (item.getColor() != null && !item.getColor().isEmpty()) {
                    key += " " + item.getColor();
                }
                if (item.getSize() != null && !item.getSize().isEmpty()) {
                    key += " " + item.getSize();
                }
                
                int count = item.getCount();
                linenSummary.put(key, linenSummary.getOrDefault(key, 0) + count);
                totalItems += count;
            }
            
            // Add data rows to table
            for (Map.Entry<String, Integer> entry : linenSummary.entrySet()) {
                Cell cell1 = new Cell()
                        .add(new Paragraph(entry.getKey()))
                        .setTextAlignment(TextAlignment.LEFT);
                
                Cell cell2 = new Cell()
                        .add(new Paragraph(String.valueOf(entry.getValue())))
                        .setTextAlignment(TextAlignment.CENTER);
                
                table.addCell(cell1);
                table.addCell(cell2);
            }
            
            document.add(table);
            
            // Add total
            Paragraph total = new Paragraph("Total Items: " + totalItems)
                    .setTextAlignment(TextAlignment.RIGHT)
                    .setFontSize(12)
                    .setBold();
            document.add(total);
            
        } finally {
            document.close();
        }
        
        return pdfFile;
    }
    
    // Print PDF file
    private void printPdf(File pdfFile) {
        if (pdfFile.exists()) {
            // Create print manager
            PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
            
            // Create print adapter
            PdfDocumentAdapter pdfAdapter = new PdfDocumentAdapter(this, pdfFile.getAbsolutePath());
            
            // Create print job
            String jobName = getString(R.string.app_name) + " Document";
            printManager.print(jobName, pdfAdapter, new PrintAttributes.Builder().build());
            
            Toast.makeText(this, "PDF berhasil dibuat: " + pdfFile.getName(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "File PDF tidak ditemukan", Toast.LENGTH_SHORT).show();
        }
    }
}