package com.android.management_linen;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Set;
import java.util.UUID;

public class TestPrinterr extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String PRINTER_NAME = "RPP02N"; // Ganti dengan nama printer Anda
    private static final UUID PRINTER_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // Ganti dengan UUID printer Anda
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice printerDevice;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_testprint);

        Button printButton = findViewById(R.id.btnTestPrint);
        Button btnAddLine = findViewById(R.id.btnAddNewLine);
        printButton.setOnClickListener(v -> printReceipt());
        btnAddLine.setOnClickListener(v -> addNewLine());
        initBluetooth();
    }

    private void initBluetooth() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            // Bluetooth tidak tersedia di perangkat
            return;
        }

        if (!bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                if (device.getName().equals(PRINTER_NAME)) {
                    printerDevice = device;
                    break;
                }
            }
        }

        try {
            if (printerDevice != null) {
                bluetoothSocket = printerDevice.createRfcommSocketToServiceRecord(PRINTER_UUID);
                bluetoothSocket.connect();
                outputStream = bluetoothSocket.getOutputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printReceipt() {
        try {
            String title = "REPORT SCAN BERSIH";
            String namaRS = "Nama RS : RS Sentra";
            String namaRuang = "Ruangan : Ruang Sentra";
            int totalLinen = 5;


            int[] separatorWidth = {48}; // Adjust as necessary

            // Title
            outputStream.write(new byte[]{0x1B, 0x61, 0x01}); // Center align
            outputStream.write(new byte[]{0x1B, 0x45, 0x01}); // Bold start
            outputStream.write(new byte[]{0x1D, 0x21, 0x10}); // Double height + width
            outputStream.write(title.getBytes("UTF-8"));
            outputStream.write(new byte[]{0x0A}); // Line feed
            outputStream.write(new byte[]{0x1B, 0x45, 0x00}); // Bold end
            outputStream.write(new byte[]{0x1D, 0x21, 0x00}); // Normal height + width
            outputStream.write(new byte[]{0x1B, 0x64, 0x01}); // add 1 line

            // Nama RS dan Ruang
            outputStream.write(new byte[]{0x1B, 0x61, 0x00}); // Center align
            outputStream.write(namaRS.getBytes("UTF-8"));
            outputStream.write(new byte[]{0x0A}); // Line feed
            outputStream.write(namaRuang.getBytes("UTF-8"));
            outputStream.write(new byte[]{0x0A}); // Line feed


            // Tabel Detail
            int[] columnWidths1 = {12, 9, 7, 11, 5}; // Adjust as necessary
            int[] alignments1 = {1, 1, 1, 1, 1}; // 0: Left, 1: Center, 2: Right
            String[] headers1 = {"Linen", "Warna", "Ukuran", "Ruangan", "Berat"};
            printSeparatorLine(outputStream, separatorWidth);
            printRow(outputStream, headers1, columnWidths1, alignments1, true, false);
            printSeparatorLine(outputStream, separatorWidth);

            // Print table rows
            String[][] rows = {
                    {"Baju Pasien Gawat Darurat Sekali", "Hijau", "XL", "Ruang A", "0.5"},
                    {"Baju Pasien ", "Hijau", "XL", "Ruang A", "0.5"},
                    {"Baju Pasien ", "Hijau", "XL", "Ruang A", "0.5"},
                    {"Baju Pasien ", "Hijau", "XL", "Ruang A", "0.5"},

            };
            for (String[] row : rows) {
                printRow(outputStream, row, columnWidths1, alignments1, false, false);
            }
            printCustomSeparatorLine(outputStream, separatorWidth, "-");

            // Print total item and weight
            int[] colTot = {23,24};
            int[] alignments2 = {1, 1}; // 0: Left, 1: Center, 2: Right
            String[] headers2 = {"Total Linen: "+totalLinen, "Total Berat (Kg): 7.5"};
            printRow(outputStream, headers2, colTot, alignments2, true, false);

            // Print Date and time
            int[] colDateWidth = {23, 23};
            int[] alignments4 = {1, 1}; // 0: Left, 1: Center, 2: Right
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault());
            String currentDateAndTime = sdf.format(new Date());
            String[] date = {"",currentDateAndTime};
            printRow(outputStream, date, colDateWidth, alignments4, true, false);

            // Signature
            int[] columnWidths3 = {23, 23}; // Adjust as necessary
            int[] alignments5 = {1, 1}; // 0: Left, 1: Center, 2: Right
            String[] footer = {"TTD Ruangan", "TTD Laundry"};
            printSeparatorLine(outputStream, separatorWidth);
            printRow(outputStream, footer, columnWidths3, alignments5, true, false);
            printSeparatorLine(outputStream, separatorWidth);
            outputStream.write(new byte[]{0x1B, 0x64, 0x03});

            printCustomSeparatorLine(outputStream, separatorWidth, "=");

            outputStream.write(new byte[]{0x1B, 0x64, 0x01});
            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addNewLine() {
        String textToPrint = " \n";

        printData(textToPrint);
    }

    private void printRow(OutputStream outputStream, String[] columns, int[] columnWidths, int[] alignments, boolean isHeader, boolean wrapText) throws IOException {
        StringBuilder rowBuilder = new StringBuilder();
        if(isHeader) {
            for (int i = 0; i < columns.length; i++) {
                rowBuilder.append(padRight(columns[i], columnWidths[i]));
                if (i < columns.length - 1) {
                    rowBuilder.append(" "); // Add space between columns
                }
            }
            rowBuilder.append("\n"); // New line at the end of the row
        }
        else {
            for (int i = 0; i < columns.length; i++) {
                System.out.println("col: " + columnWidths[i]);
                if (i == 0) {
                    rowBuilder.append(padRight(columns[i], 47));
                    rowBuilder.append("\n");
                    rowBuilder.append("            "); // added space from column 0
                } else {
                    rowBuilder.append(padRight(columns[i], columnWidths[i]));
                }

                if (i < columns.length - 1) {
                    rowBuilder.append(" "); // Add space between columns
                }
            }
            rowBuilder.append("\n"); // New line at the end of the row
        }
        outputStream.write(rowBuilder.toString().getBytes("UTF-8"));
    }

    private void printSeparatorLine(OutputStream outputStream, int[] columnWidths) throws IOException {
        StringBuilder lineBuilder = new StringBuilder();
        for (int width : columnWidths) {
            for (int i = 0; i < width; i++) {
                lineBuilder.append("-");
            }
            lineBuilder.append(""); // Add space between columns
        }
        lineBuilder.append("\n");
        outputStream.write(lineBuilder.toString().getBytes("UTF-8"));
    }

    private void printCustomSeparatorLine(OutputStream outputStream, int[] columnWidths, String symbol) throws IOException {
        StringBuilder lineBuilder = new StringBuilder();
        for (int width : columnWidths) {
            for (int i = 0; i < width; i++) {
                lineBuilder.append(symbol);
            }
            lineBuilder.append(""); // Add space between columns
        }
        lineBuilder.append("\n");
        outputStream.write(lineBuilder.toString().getBytes("UTF-8"));
    }

    private String padRight(String text, int length) {
        if (text.length() > length) {
            return text.substring(0, length);
        }
        return String.format("%-" + length + "s", text);
    }

    private String padLeft(String text, int length) {
        if (text.length() > length) {
            return text.substring(0, length);
        }
        return String.format("%" + length + "s", text);
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
        return String.format("%" + padding + "s%s%" + (length - text.length() - padding) + "s", "", text, "");
    }

    private void printData(String text) {
        try {
            if (outputStream != null) {
                outputStream.write(text.getBytes());
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        disconnectBluetooth();
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (bluetoothSocket != null && !bluetoothSocket.isConnected()) {
                if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                bluetoothSocket.connect();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
