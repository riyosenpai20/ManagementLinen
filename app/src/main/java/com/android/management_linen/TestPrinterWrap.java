package com.android.management_linen;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.management_linen.adapter.DetailsScanBersihAdapter;
import com.android.management_linen.models.DetailsScanBersih;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TestPrinterWrap extends AppCompatActivity {
    SharedPreferences sharedpreferences;
    public static final String SHARED_PREFS = "shared_prefs";
    private int intLayout = 3;
    private PDFView pdfView;
    private Button btnPrint, btnPdf, btnAddLine;
    public String namaRuangReport, pdf_title, token, namaPerusahaan, beratTot, namaRuangLinen, currentDateAndTime;
    public int idRuang, type_rs;

    private List<DetailsScanBersih> detailsListScan;

    // For Connection Bluetooth Printer
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String PRINTER_MAC_ADDRESS = "86:67:7a:5e:95:fa";
    private static final String PRINTER_NAME = "RPP02N"; // Replace with your printer's name
    private static final UUID PRINTER_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // Replace with your printer's UUID
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice printerDevice;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private ActivityResultLauncher<Intent> enableBluetoothLauncher;
    private Set<BluetoothDevice> pairedDevices;
    private Dialog loadingDialog;
    RecyclerView rv_table;
    DetailsScanBersihAdapter adpDetailsScanBersih;
    TextView titleReport, namaRSReport, namaRuangReport2, datePrint, totalLinen2, totalBerat;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testprint);
        Button printButton = findViewById(R.id.btnTestPrint);
        Button btnAddLine = findViewById(R.id.btnAddNewLine);

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

        initBluetooth();
        printButton.setOnClickListener(v -> {
            setBTSocket();
            printReceipt2();
//            try {
//                outputStream.write(("Test").getBytes("UTF-8"));
//                outputStream.flush();
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
            new Handler(Looper.getMainLooper()).postDelayed(this::disconnectBluetooth, 500);
        });
        btnAddLine.setOnClickListener(v -> {
            setBTSocket();
            addNewLine();
            new Handler(Looper.getMainLooper()).postDelayed(this::disconnectBluetooth, 80);
        });
    }

    private void initBluetooth() {
        BluetoothManager bluetoothManager = getSystemService(BluetoothManager.class);
        bluetoothAdapter = bluetoothManager.getAdapter();
        if (bluetoothAdapter == null) {
            // Device doesn't support Bluetooth
            Toast.makeText(this, "Device ini tidak mendukung koneksi Bluetooth", Toast.LENGTH_SHORT).show();
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            enableBluetoothLauncher.launch(enableIntent);
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

    public void setBTSocket() {
        findBluetoothDevice();
        bluetoothSocket = null;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            try {
                bluetoothSocket = printerDevice.createRfcommSocketToServiceRecord(PRINTER_UUID);
                bluetoothAdapter.cancelDiscovery();
                try {
                    bluetoothSocket.connect();
                    outputStream = bluetoothSocket.getOutputStream();
                } catch (IOException e) {
                    try {
                        bluetoothSocket.close();
                    } catch (IOException e1) {
                        System.out.println("Error: " + e1);
                    }
                    alertDialog("Perhatian", "Ada kemungkinan printer dalam keadaan mati, silahkan periksa kembali.");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void alertDialog(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void disconnectBluetooth() {
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printReceipt2() {
        try {
            int[] separatorWidth = {48}; // Adjust as necessary

            // Tabel Detail
            if (type_rs != 1) {
                int[] columnWidths1 = {12, 9, 7, 11, 5}; // Adjust as necessary
                int[] alignments1 = {0, 0, 0, 0, 0}; // 0: Left, 1: Center, 2: Right
                String[] headers1 = {"Linen", "Warna", "Ukuran", "Ruangan", "Berat"};
                printSeparatorLine(outputStream, separatorWidth);
                printRow2(outputStream, headers1, columnWidths1, alignments1, true, true);
                printSeparatorLine(outputStream, separatorWidth);
            } else {
                int[] columnWidths1 = {12, 11, 11, 11}; // Adjust as necessary
                int[] alignments1 = {0, 0, 0, 0}; // 0: Left, 1: Center, 2: Right
                String[] headers1 = {"Linen", "Warna", "Ukuran", "Berat"};
                printSeparatorLine(outputStream, separatorWidth);
                printRow2(outputStream, headers1, columnWidths1, alignments1, true, true);
                printSeparatorLine(outputStream, separatorWidth);
            }

            // Print table rows
            String[][] rows = {
                    {"Baju Pasien Gawat Darurat Sekali", "Hijau", "XL", "Ruang A", "0.5"},
                    {"Baju Pasien ", "Hijau", "XL", "Ruang A", "0.5"},
                    {"Baju Pasien ", "Hijau", "XL", "Ruang Anggrek", "0.5"},
                    {"Baju Pasien ", "Hijau", "XL", "Ruang A", "0.5"},
                    {"Baju Pasien ", "Hijau", "XL", "Ruang Magdalena", "0.5"},
                    {"Baju Pasien ", "Hijau", "XL", "Ruang A", "0.5"},
                    {"Baju Baru beli ditoko online ", "Hijau", "XL", "Ruang A", "0.5"},
                    {"Baju Pasien ", "Hijau", "XL", "Ruang A", "0.5"},
                    {"Baju Baru hasil nabung ", "Hijau", "XL", "Ruang A", "0.5"},
                    {"Baju Pasien ", "Hijau", "XL", "Ruang A", "0.5"},
                    {"Baju Pasien ", "Hijau", "XL", "Ruang Anggrek", "0.5"},
                    {"Baju Pasien ", "Hijau", "XL", "Ruang A", "0.5"},

            };

            int[] columnWidths2 = {12, 9, 7, 11, 5}; // Adjust as necessary
            int[] alignments2 = {0, 0, 0, 0, 0}; // 0: Left, 1: Center, 2: Right
            for (String[] row : rows) {
                printRow2(outputStream, row, columnWidths2, alignments2, false, true);
            }

            printSeparatorLine(outputStream, separatorWidth);


            outputStream.write(("\n").getBytes());
            outputStream.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printRow2(OutputStream outputStream, String[] columns, int[] columnWidths, int[] alignments, boolean isHeader, boolean wrapText) throws IOException {
        List<String[]> wrappedRows = new ArrayList<>();

        for (int i = 0; i < columns.length; i++) {
            if (wrapText) {
                wrappedRows.add(wrapText(columns[i], columnWidths[i]));
            } else {
                wrappedRows.add(new String[]{columns[i]});
            }
        }

        int maxLines = 0;
        for (String[] wrappedRow : wrappedRows) {
            if (wrappedRow.length > maxLines) {
                maxLines = wrappedRow.length;
            }
        }

        for (int line = 0; line < maxLines; line++) {
            StringBuilder rowBuilder = new StringBuilder();
            for (int i = 0; i < columns.length; i++) {
                String text = (line < wrappedRows.get(i).length) ? wrappedRows.get(i)[line] : "";
                rowBuilder.append(padRight(alignText(text, columnWidths[i], alignments[i]), columnWidths[i]));
                if (i < columns.length - 1) {
                    rowBuilder.append(" "); // Add space between columns
                }
            }
            rowBuilder.append("\n"); // New line at the end of the row
            outputStream.write(rowBuilder.toString().getBytes("UTF-8"));
        }
    }

    private String[] wrapText(String text, int columnWidth) {
        List<String> lines = new ArrayList<>();
        int start = 0;
        while (start < text.length()) {
            int end = Math.min(start + columnWidth, text.length());
            lines.add(text.substring(start, end));
            start = end;
        }
        return lines.toArray(new String[0]);
    }
//    private String[] wrapText(String text, int width) {
//        List<String> wrapped = new ArrayList<>();
//        String[] words = text.split(" ");
//        StringBuilder line = new StringBuilder();
//
//        for (String word : words) {
//            if (line.length() + word.length() + 1 > width) {
//                wrapped.add(line.toString());
//                line = new StringBuilder();
//            }
//            if (line.length() > 0) {
//                line.append(" ");
//            }
//            line.append(word);
//        }
//        wrapped.add(line.toString());
//
//        return wrapped.toArray(new String[0]);
//    }

    private String padRight(String text, int length) {
        if (text.length() > length) {
            return text.substring(0, length);
        }
        StringBuilder sb = new StringBuilder(text);
        while (sb.length() < length) {
            sb.append(' ');
        }
        return sb.toString();
    }


    private String padLeft(String text, int length) {
        if (text.length() > length) {
            return text.substring(0, length);
        }
        StringBuilder sb = new StringBuilder();
        while (sb.length() < length - text.length()) {
            sb.append(' ');
        }
        sb.append(text);
        return sb.toString();
    }


    private String alignText(String text, int length, int alignment) {
        if (alignment == 1) { // Center
            return centerText(text, length);
        } else if (alignment == 2) { // Right
            return padLeft(text, length);
        } else { // Left (default)
            return padRight(text, length);
        }
    }

    private String centerText(String text, int length) {
        if (text.length() >= length) {
            return text.substring(0, length);
        }
        int padding = (length - text.length()) / 2;
        StringBuilder sb = new StringBuilder();
        while (sb.length() < padding) {
            sb.append(' ');
        }
        sb.append(text);
        while (sb.length() < length) {
            sb.append(' ');
        }
        return sb.toString();
    }


    private void printSeparatorLine(OutputStream outputStream, int[] columnWidths) throws IOException {
        StringBuilder separatorLine = new StringBuilder();
        for (int width : columnWidths) {
            for (int i = 0; i < width; i++) {
                separatorLine.append("-");
            }
        }
        separatorLine.append("\n");
        outputStream.write(separatorLine.toString().getBytes("UTF-8"));
    }

    private void printCustomText(OutputStream outputStream, String text, int alignment, int textSize) throws IOException {
        String alignedText = alignText(text, 32, alignment); // Assuming a fixed width of 32 characters
        outputStream.write(alignedText.getBytes("UTF-8"));
        outputStream.write("\n".getBytes());
    }

    private void addNewLine() {
        try {
            if (outputStream != null) {
                outputStream.write("\n".getBytes());
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
