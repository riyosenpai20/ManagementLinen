package com.example.management_linen;

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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
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
import androidx.recyclerview.widget.RecyclerView;

import com.example.management_linen.adapter.PdfDocumentAdapter;
import com.example.management_linen.utils.PrinterUtils;
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
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import com.example.management_linen.helpers.ApiClient;
import com.example.management_linen.helpers.ApiHelper;
import com.example.management_linen.models.*;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PreviewSearchActivity extends AppCompatActivity {
    private static final String TAG = "PreviewSearchActivity";
    
    private RecyclerView recyclerView;
    private TextView tvRumahSakit;
    private TextView tvTotalItems;
    private List<String> tagList;
    private String namaRuang;
    private TableLayout tableLayout;
    private float txtSize = 12;
    private FloatingActionButton fabPrint;
    private FloatingActionButton fabPdf;
    
    SharedPreferences sharedpreferences;
    public static final String SHARED_PREFS = "shared_prefs";
    private String token;
    private ApiHelper apiHelper;

    private List<DetailScanUnknown> searchData;
    private String cardType = "";
    private String namaPerusahaan = "";
    private int idRuang;
    private String lokasi;
    
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preview_search);
        
        // Initialize searchData
        searchData = new ArrayList<>();

        // Initialize SharedPreferences
        sharedpreferences = getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        token = sharedpreferences.getString("token", null);
        cardType = sharedpreferences.getString("cardType", "");
        idRuang = sharedpreferences.getInt("idRuang", 0);
        namaPerusahaan = sharedpreferences.getString("namaPerusahaan", "");

        System.out.println("idRuang PreviewSearch: " + idRuang);

        if (token == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Initialize views
        getSupportActionBar().setTitle("Preview Pencarian");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tvRumahSakit = findViewById(R.id.tvRumahSakit);
        tvTotalItems = findViewById(R.id.tvTotalItems);
        tableLayout = findViewById(R.id.tableLayout); // Tambahkan inisialisasi tableLayout di sini
        fabPrint = findViewById(R.id.fabPrint);
        fabPdf = findViewById(R.id.fabPdf);
        
        // Initialize Bluetooth
        initBluetooth();
        
        // Initialize API helper
        apiHelper = ApiClient.getClient().create(ApiHelper.class);

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

        // Get data from intent
        Intent intent = getIntent();
        if (intent != null) {
            // Cek apakah ada responseData dari SearchInfoActivity
            ResponseUnknownLinen responseData = (ResponseUnknownLinen) intent.getSerializableExtra("responseData");
            
            if (responseData != null) {
                // Gunakan responseData langsung
                tvRumahSakit.setText(namaPerusahaan);
                
                if (responseData.getData() != null) {
                    tvTotalItems.setText(String.valueOf(responseData.getData().size()));
                    searchData = responseData.getData();
                    
                    // Tampilkan data di tabel
                    displayDataInTable(searchData);
                }
            } else {
                // Fallback ke cara lama jika tidak ada responseData
                tagList = (List<String>) intent.getSerializableExtra("tagList");
                namaRuang = intent.getStringExtra("namaRuang");
                
                if (tagList != null) {
                    tvRumahSakit.setText(namaPerusahaan);
                    tvTotalItems.setText(String.valueOf(tagList.size()));
                    
                    System.out.println(tagList);
                    
                    // Kirim data otomatis saat halaman dibuka
                    kirimDataKeServer();
                } else {
                    Toast.makeText(this, "Tidak ada data yang ditemukan", Toast.LENGTH_SHORT).show();
                }
            }
        }
        
        // Setup PDF button
        if (fabPdf != null) {
            fabPdf.setOnClickListener(v -> {
                try {
                    File pdfFile = generatePdf(searchData, namaPerusahaan, lokasi);
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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
    
    private void displayDataInTable(List<DetailScanUnknown> data) {
        if (data == null || data.isEmpty()) {
            Toast.makeText(this, "Tidak ada data untuk ditampilkan", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Bersihkan tabel terlebih dahulu
        tableLayout.removeAllViews();
        
        // Tambahkan header
        TableRow headerRow = new TableRow(this);
        headerRow.setPadding(5, 10, 5, 10);
        headerRow.setBackgroundColor(ContextCompat.getColor(this, R.color.colorPrimary));
        
        // Tambahkan kolom header
        String[] headers = {"No", "Kategori", "Sub Kategori", "Lokasi", "RFID"};
        for (String header : headers) {
            TextView textView = new TextView(this);
            textView.setText(header);
            textView.setTextColor(ContextCompat.getColor(this, R.color.white));
            textView.setPadding(10, 5, 10, 5);
            textView.setGravity(Gravity.CENTER);
            headerRow.addView(textView);
        }
        
        tableLayout.addView(headerRow);
        
        // Tambahkan data
        int rowIndex = 0;
        for (DetailScanUnknown item : data) {
            List<String> rfids = item.getRfids();
            if (rfids != null && !rfids.isEmpty()) {
                for (String rfid : rfids) {
                    TableRow row = new TableRow(this);
                    row.setPadding(5, 10, 5, 10);
                    row.setLayoutParams(new TableLayout.LayoutParams(
                            TableLayout.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                    ));
                    
                    // Gunakan warna berbeda untuk baris ganjil dan genap
                    row.setBackgroundColor(ContextCompat.getColor(this, 
                            rowIndex % 2 == 0 ? R.color.tb_RowUnknown_Primary : R.color.white));
                    
                    // Tambahkan kolom data
                    TextView tvNo = new TextView(this);
                    tvNo.setText(String.valueOf(rowIndex + 1));
                    tvNo.setPadding(10, 5, 10, 5);
                    tvNo.setGravity(Gravity.CENTER);
                    row.addView(tvNo);
                    
                    TextView tvCategory = new TextView(this);
                    tvCategory.setText(item.getCategory());
                    tvCategory.setPadding(10, 5, 10, 5);
                    row.addView(tvCategory);
                    
                    TextView tvSubCategory = new TextView(this);
                    tvSubCategory.setText(item.getSubCategory());
                    tvSubCategory.setPadding(10, 5, 10, 5);
                    row.addView(tvSubCategory);
                    
                    TextView tvLocation = new TextView(this);
                    tvLocation.setText(item.getLocation());
                    tvLocation.setPadding(10, 5, 10, 5);
                    row.addView(tvLocation);
                    
                    TextView tvRfid = new TextView(this);
                    tvRfid.setText(rfid);
                    tvRfid.setPadding(10, 5, 10, 5);
                    row.addView(tvRfid);
                    
                    tableLayout.addView(row);
                    rowIndex++;
                }
            }
        }
    }
    
    private void kirimDataKeServer() {
        if (tagList == null || tagList.isEmpty()) {
            Toast.makeText(this, "Tidak ada data untuk dikirim", Toast.LENGTH_SHORT).show();
            return;
        }
        
        // Menampilkan loading
        Toast.makeText(this, "Mengirim data dari lokasi " + cardType, Toast.LENGTH_SHORT).show();
        
        // Memanggil API untuk mengirim data
        RfidRequest rfidRequest = new RfidRequest(tagList, cardType, idRuang);
        Call<ResponseUnknownLinen> call = apiHelper.search_unknown_linen(
                rfidRequest,
                "Token " + token,
                "application/json",
                "application/json"
        );
        
        call.enqueue(new Callback<ResponseUnknownLinen>() {
            @Override
            public void onResponse(Call<ResponseUnknownLinen> call, Response<ResponseUnknownLinen> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ResponseUnknownLinen responseData = response.body();
//                    Toast.makeText(PreviewSearchActivity.this, responseData.getMessage(), Toast.LENGTH_SHORT).show();

                    if(cardType == "ruangan") {
                        lokasi = responseData.getRuang();
                    } else {
                        lokasi = cardType;
                    }
                    List<DetailScanUnknown> items = responseData.getData();
                    Log.d("PreviewSearch", "Jumlah items: " + (items != null ? items.size() : 0));
                    if (items != null && !items.isEmpty()) {
                        int rowIndex = 0; // Tambahkan variabel ini di awal loop items
                        
                        for (DetailScanUnknown item : items) {
                            // Tampilkan informasi item lebih detail
                            Log.d("PreviewSearch", "Category: " + item.getCategory() 
                                  + ", SubCategory: " + item.getSubCategory()
                                  + ", Location: " + item.getLocation()
                                  + ", RFID count: " + (item.getRfids() != null ? item.getRfids().size() : 0));
//                            System.out.println(item.getRfids());
                            // Tambahkan ke list untuk ditampilkan
                            searchData.add(item);

//                            TableRow row = new TableRow(PreviewSearchActivity.this);
//                            row.setPadding(3,10,3,10);
//                            row.setLayoutParams(new TableLayout.LayoutParams(
//                                    TableLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
//                            ));
//
//                            row.setBackgroundColor(ContextCompat.getColor(PreviewSearchActivity.this, R.color.tb_RowMain_Primary));

                            List<String> rfids = item.getRfids();
                            if(rfids != null && !rfids.isEmpty()) {
                                for(String rfid : rfids) {
                                    System.out.println(rfid);
                                    TableRow row = new TableRow(PreviewSearchActivity.this);
                                    row.setPadding(5,10,5,10);
                                    row.setLayoutParams(new TableLayout.LayoutParams(
                                            TableLayout.LayoutParams.MATCH_PARENT,
                                            ViewGroup.LayoutParams.WRAP_CONTENT
                                    ));

                                    // Gunakan warna berbeda untuk baris ganjil dan genap
                                    row.setBackgroundColor(ContextCompat.getColor(PreviewSearchActivity.this, 
                                            rowIndex % 2 == 0 ? R.color.tb_RowUnknown_Primary : R.color.white));
                                    rowIndex++;
                                    TextView tvRfid = new TextView(PreviewSearchActivity.this);
                                    tvRfid.setText(rfid);
                                    // Perbaiki parameter layout - gunakan TableRow.LayoutParams bukan TableLayout.LayoutParams
                                    tvRfid.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                                    tvRfid.setGravity(Gravity.CENTER);
                                    tvRfid.setPadding(5,5,5,5);
                                    tvRfid.setTextSize(txtSize);

                                    TextView tvLinen = new TextView(PreviewSearchActivity.this);
                                    tvLinen.setText(item.getSubCategory() + " " + item.getColor() + " " + item.getSize());
                                    // Perbaiki parameter layout
                                    tvLinen.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                                    tvLinen.setGravity(Gravity.LEFT);
                                    tvLinen.setPadding(5,5,5,5);
                                    tvLinen.setTextSize(txtSize);

                                    TextView tvLokasi = new TextView(PreviewSearchActivity.this);
                                    tvLokasi.setText(lokasi != null ? lokasi : "-");
                                    // Perbaiki parameter layout
                                    tvLokasi.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                                    tvLokasi.setGravity(Gravity.CENTER);
                                    tvLokasi.setPadding(5,5,5,5);
                                    tvLokasi.setTextSize(txtSize);

                                    row.addView(tvRfid);
                                    row.addView(tvLinen);
                                    row.addView(tvLokasi); // Tambahkan kolom lokasi ke dalam row

                                    tableLayout.addView(row);
                                }
                            }
                        }
                        
//                        // Tampilkan data dalam RecyclerView dengan format tabel
//                        DetailScanUnknownAdapter adapter = new DetailScanUnknownAdapter(searchData, PreviewSearchActivity.this);
//                        recyclerView.setAdapter(adapter);
                    } else {
                        Toast.makeText(PreviewSearchActivity.this, "Tidak ada data yang ditemukan", Toast.LENGTH_SHORT).show();
                    }
                    
                    // Tambahkan logika untuk menampilkan hasil pencarian jika diperlukan
                    // Misalnya, update RecyclerView dengan data dari responseData.getData()
                } else {
                    Toast.makeText(PreviewSearchActivity.this, "Gagal mengirim data", Toast.LENGTH_SHORT).show();
                }
            }
            
            @Override
            public void onFailure(Call<ResponseUnknownLinen> call, Throwable t) {
                Toast.makeText(PreviewSearchActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
    }
    
    // Check and request Bluetooth permissions
    private boolean checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, REQUEST_PERMISSIONS_BT);
                return false;
            }
        }
        return true;
    }
    
    // Set up Bluetooth socket
    @SuppressLint("MissingPermission")
    private boolean setBTSocket() {
        if (!checkBluetoothPermissions()) {
            return false;
        }
        
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth tidak tersedia", Toast.LENGTH_SHORT).show();
            return false;
        }
        
        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBluetoothLauncher.launch(enableBtIntent);
            return false;
        }
        
        try {
            // Get paired devices
            pairedDevices = bluetoothAdapter.getBondedDevices();
            if (pairedDevices.size() > 0) {
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getName() != null && device.getName().equals(PRINTER_NAME) || 
                        device.getAddress().equals(PRINTER_MAC_ADDRESS)) {
                        printerDevice = device;
                        break;
                    }
                }
            }
            
            if (printerDevice == null) {
                Toast.makeText(this, "Printer tidak ditemukan", Toast.LENGTH_SHORT).show();
                return false;
            }
            
            // Create socket
            bluetoothSocket = printerDevice.createRfcommSocketToServiceRecord(PRINTER_UUID);
            bluetoothSocket.connect();
            outputStream = bluetoothSocket.getOutputStream();
            
            return true;
        } catch (IOException e) {
            Log.e(TAG, "Error setting up Bluetooth socket: " + e.getMessage());
            Toast.makeText(this, "Gagal terhubung ke printer: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            return false;
        }
    }
    
    // Disconnect Bluetooth
    private void disconnectBluetooth() {
        if (bluetoothSocket != null) {
            try {
                outputStream.close();
                bluetoothSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error disconnecting Bluetooth: " + e.getMessage());
            }
        }
    }
    
    // Print receipt
    private void printReceipt() {
        if (outputStream == null) {
            Toast.makeText(this, "Printer tidak terhubung", Toast.LENGTH_SHORT).show();
            return;
        }
        
        try {
            int[] separatorWidth = {48}; // Adjust as necessary
            // Print header
            String header = "LINEN TIDAK DIKETAHUI\n";
            Log.d(TAG, "Writing header: " + header);
            outputStream.write(new byte[]{0x1B, 0x61, 0x01}); // Center align
            outputStream.write(new byte[]{0x1B, 0x45, 0x01}); // Bold start
            outputStream.write(new byte[]{0x1D, 0x21, 0x10}); // Double height + width
            outputStream.write(header.getBytes("UTF-8"));

            outputStream.write(new byte[]{0x1D, 0x21, 0x00}); // Normal height + width
            String hospitalLine = namaPerusahaan + "\n\n";
            outputStream.write(hospitalLine.getBytes("UTF-8"));
            outputStream.write(new byte[]{0x1B, 0x45, 0x00}); // Bold end
            outputStream.write(new byte[]{0x1B, 0x61, 0x00}); // Center align

            String roomLine = "Ruangan : " + lokasi + "\n";
            outputStream.write(roomLine.getBytes("UTF-8"));

            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            String currentDate = sdf.format(new Date());
            String dateLine = "Tanggal : " + currentDate + "\n\n";
            outputStream.write(dateLine.getBytes("UTF-8"));

            // Print table header
            String tableHeader = "LINEN                            RFID\n";
            outputStream.write(tableHeader.getBytes("UTF-8"));
            PrinterUtils.printCustomSeparatorLine(outputStream, separatorWidth, "-");

            // Print table data
            for (DetailScanUnknown item : searchData) {
                List<String> rfids = item.getRfids();
                String linenName = item.getSubCategory() + " " +
                        item.getColor() + " " + item.getSize();

//                String systemCount = String.valueOf(item.getCount() + item.getNoMatchTagsCount());
//                String scanCount = String.valueOf(item.getMatchingCount() + item.getNoMatchTagsCount());
                if (rfids != null && !rfids.isEmpty()) {
                    for (String rfid :rfids) {
                        if (linenName.length() > 23) {
                            // Bagi teks berdasarkan jumlah huruf
                            String firstLine = linenName.substring(0, 23);
                            String secondLine = linenName.substring(23);

                            String line = String.format("%-23s %-23s\n", firstLine, rfid);
                            outputStream.write(line.getBytes("UTF-8"));

                            String continuationLine = String.format("%-23s %-23s\n", secondLine, "");
                            outputStream.write(continuationLine.getBytes("UTF-8"));
                        } else {
                            String line = String.format("%-23s %-23s\n", linenName, rfid);
                            outputStream.write(line.getBytes("UTF-8"));
                        }
                    }
                }
            }

            PrinterUtils.printCustomSeparatorLine(outputStream, separatorWidth, "-");


            // Print footer
            outputStream.write(new byte[]{0x1B, 0x61, 0x01}); // Center align
            outputStream.write("\n".getBytes("UTF-8"));
            outputStream.write("TTD Ruangan:           TTD Laundry:\n\n\n\n".getBytes("UTF-8"));
            outputStream.write("____________          ____________\n\n".getBytes("UTF-8"));
            outputStream.write(new byte[]{0x1B, 0x61, 0x00}); // Center align


            Toast.makeText(this, "Berhasil mencetak", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(TAG, "Error printing receipt: " + e.getMessage());
            Toast.makeText(this, "Gagal mencetak: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
    
    // Generate PDF
    private File generatePdf(List<DetailScanUnknown> searchData, String namaPerusahaan, String lokasi ) throws IOException {
        // Create PDF file
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String fileName = "SearchResult_" + timeStamp + ".pdf";
        File pdfFile = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName);
        
        // Create PDF document
        PdfWriter writer = new PdfWriter(pdfFile);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        
        // Add header
        Paragraph header = new Paragraph("LINEN TIDAK DIKETAHUI")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(16);
        document.add(header);
        
        // Add hospital name
        Paragraph hospitalParagraph = new Paragraph("Rumah Sakit: " + namaPerusahaan)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(12);
        document.add(hospitalParagraph);
        
        // Add location
        Paragraph locationParagraph = new Paragraph("Lokasi: " + (lokasi != null ? lokasi : "-"))
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(12);
        document.add(locationParagraph);
        
        // Add date
        String currentDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        Paragraph dateParagraph = new Paragraph("Tanggal: " + currentDate)
                .setTextAlignment(TextAlignment.LEFT)
                .setFontSize(12);
        document.add(dateParagraph);
        
        document.add(new Paragraph("\n"));
        
        // Create table
        Table table = new Table(UnitValue.createPercentArray(new float[]{33.33f, 33.33f, 33.33f}))
                .useAllAvailableWidth();
        
        // Add table headers
        Cell headerRfid = new Cell().add(new Paragraph("RFID").setTextAlignment(TextAlignment.CENTER));
        Cell headerLinen = new Cell().add(new Paragraph("Linen").setTextAlignment(TextAlignment.CENTER));
        Cell headerLokasi = new Cell().add(new Paragraph("Lokasi").setTextAlignment(TextAlignment.CENTER));
        
        table.addHeaderCell(headerRfid);
        table.addHeaderCell(headerLinen);
        table.addHeaderCell(headerLokasi);
        
        // Add table data
        for (DetailScanUnknown item : searchData) {
            List<String> rfids = item.getRfids();
            if (rfids != null && !rfids.isEmpty()) {
                for (String rfid : rfids) {
                    table.addCell(new Cell().add(new Paragraph(rfid).setTextAlignment(TextAlignment.CENTER)));
                    table.addCell(new Cell().add(new Paragraph(item.getSubCategory() + " " + item.getColor() + " " + item.getSize()).setTextAlignment(TextAlignment.LEFT)));
                    table.addCell(new Cell().add(new Paragraph(lokasi != null ? lokasi : "-").setTextAlignment(TextAlignment.CENTER)));
                }
            }
        }
        
        document.add(table);

        document.add(new Paragraph("\n"));

        Table footerTable2 = new Table(UnitValue.createPercentArray(new float[]{1, 1}));
        footerTable2.setWidth(UnitValue.createPercentValue(100));

        footerTable2.addCell(new Cell().add(new Paragraph("TTD Ruangan"))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorderTop(new SolidBorder(1))
                .setBorderLeft(new SolidBorder(1))
                .setBorderRight(new SolidBorder(1))
                .setBorderBottom(Border.NO_BORDER)
        );
        footerTable2.addCell(new Cell().add(new Paragraph("TTD Laundry"))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorderTop(new SolidBorder(1))
                .setBorderLeft(new SolidBorder(1))
                .setBorderRight(new SolidBorder(1))
                .setBorderBottom(Border.NO_BORDER)
        );

        footerTable2.addCell(new Cell().add(new Paragraph(""))
                .setBorderLeft(new SolidBorder(1))
                .setBorderRight(new SolidBorder(1))
                .setBorderTop(Border.NO_BORDER)
                .setBorderBottom(Border.NO_BORDER)
                .setHeight(30)
        );

        footerTable2.addCell(new Cell().add(new Paragraph(""))
                .setBorderLeft(new SolidBorder(1))
                .setBorderRight(new SolidBorder(1))
                .setBorderTop(Border.NO_BORDER)
                .setBorderBottom(Border.NO_BORDER)
                .setHeight(30)
        );

        footerTable2.addCell(new Cell().add(new Paragraph(""))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorderTop(Border.NO_BORDER)
                .setBorderLeft(new SolidBorder(1))
                .setBorderRight(new SolidBorder(1))
                .setBorderBottom(new SolidBorder(1))
        );
        footerTable2.addCell(new Cell().add(new Paragraph(""))
                .setTextAlignment(TextAlignment.CENTER)
                .setBorderTop(Border.NO_BORDER)
                .setBorderLeft(new SolidBorder(1))
                .setBorderRight(new SolidBorder(1))
                .setBorderBottom(new SolidBorder(1))
        );

        document.add(footerTable2);

        document.close();
        
        return pdfFile;
    }
    
    // Print PDF
    private void printPdf(File pdfFile) {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        String jobName = getString(R.string.app_name) + " Document";
        
        PrintAttributes.Builder builder = new PrintAttributes.Builder();
        builder.setMediaSize(PrintAttributes.MediaSize.ISO_A4);
        builder.setColorMode(PrintAttributes.COLOR_MODE_COLOR);
        builder.setResolution(new PrintAttributes.Resolution("res1", "Resolution", 600, 600));
        builder.setMinMargins(PrintAttributes.Margins.NO_MARGINS);
        
        printManager.print(jobName, new PdfDocumentAdapter(this, pdfFile.getAbsolutePath()), builder.build());
    }
}