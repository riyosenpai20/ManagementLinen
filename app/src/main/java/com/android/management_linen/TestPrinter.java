package com.android.management_linen;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class TestPrinter extends AppCompatActivity {
    //For Connection Bluetooth Printer
    private static final int REQUEST_ENABLE_BT = 1;
    private static final String PRINTER_MAC_ADDRESS = "86:67:7a:5e:95:fa";
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;
    private static final String TAG = "BluetoothPrinter";
    private static final String PRINTER_NAME = "RPP02N";
    private static final int PERMISSION_REQUEST_CODE = 1;

    Button btnTestPrint, btnAddNewLine, btnLayoutPrint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_testprint);
        btnTestPrint = findViewById(R.id.btnTestPrint);
        btnAddNewLine = findViewById(R.id.btnAddNewLine);
        btnLayoutPrint = findViewById(R.id.btnLayoutPrint);

        //setting printer bluetooth
        checkPermission();
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        btnTestPrint.setOnClickListener(v -> {
            discoverAndPrint(true, false);
        });

        btnAddNewLine.setOnClickListener(v -> {
            discoverAndPrint(false, true);
        });


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissions granted, continue with Bluetooth operations
            } else {
                // Permissions denied
            }
        }
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{
                        Manifest.permission.BLUETOOTH_CONNECT,
                        Manifest.permission.BLUETOOTH_SCAN,
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, PERMISSION_REQUEST_CODE);
            }
        }
    }

    private void discoverAndPrint(Boolean data, Boolean newLine) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            if (device.getName().equals(PRINTER_NAME) || device.getAddress().equals(PRINTER_MAC_ADDRESS)) {
                connectToPrinter(device, data, newLine);
                break;
            }
        }
    }

    private void discoverAndPrint2() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            if (device.getName().equals(PRINTER_NAME) || device.getAddress().equals(PRINTER_MAC_ADDRESS)) {
                connectToPrinter2(device);
                break;
            }
        }
    }

    private void connectToPrinter(BluetoothDevice device, Boolean data, Boolean newLine) {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();

            OutputStream outputStream = bluetoothSocket.getOutputStream();
            printText(outputStream, "", data, newLine);

        } catch (IOException e) {
            Toast.makeText(TestPrinter.this, e.getMessage(), Toast.LENGTH_LONG).show();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    Toast.makeText(TestPrinter.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

//            if (bluetoothSocket != null) {
//                try {
//                    bluetoothSocket.close();
//                } catch (IOException e) {
//                    Toast.makeText(TestPrinter.this, e.getMessage(), Toast.LENGTH_LONG).show();
//                }
//            }
        }
    }

    private void connectToPrinter2(BluetoothDevice device) {
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
            bluetoothSocket.connect();

            OutputStream outputStream2 = bluetoothSocket.getOutputStream();
            View view = findViewById(R.id.printableLayout);
            printLayout(view);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addedNewLine() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        for (BluetoothDevice device : pairedDevices) {
            if (device.getName().equals(PRINTER_NAME) || device.getAddress().equals(PRINTER_MAC_ADDRESS)) {
                try {
                    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        return;
                    }
                    bluetoothSocket = device.createRfcommSocketToServiceRecord(MY_UUID);
                    bluetoothSocket.connect();

                    OutputStream outputStream = bluetoothSocket.getOutputStream();
                    addNewLine(outputStream, "Hello, World!");

                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            }
        }
    }

    private void printText(OutputStream outputStream, String text, Boolean data, Boolean newLine) {
        try {
            if(data) {
                String title = "REPORT SCAN BERSIH";
                String namaRS = "Nama RS : RS Sentra";
                String namaRuang = "Ruangan : Ruang Sentra";

                // Title
                outputStream.write(new byte[]{0x1B, 0x61, 0x01}); // Center align
                outputStream.write(new byte[]{0x1B, 0x45, 0x01}); //Bold Start
                outputStream.write(new byte[]{0x1D, 0x21, 0x10});
                outputStream.write(title.getBytes("UTF-8"));
                outputStream.write(new byte[]{0x1D, 0x21, 0x00});
                outputStream.write(new byte[]{0x1B, 0x45, 0x00}); //Bold Off
                outputStream.write(new byte[]{0x0A}); // Line feed
                outputStream.write(new byte[]{0x1B, 0x61, 0x00}); // Left align
                outputStream.write(new byte[]{0x1B, 0x64, 0x01});

                // Nama RS
                outputStream.write(namaRS.getBytes("UTF-8"));
                outputStream.write(new byte[]{0x0A}); // Line feed

                // Ruangan
                outputStream.write(namaRuang.getBytes("UTF-8"));
                outputStream.write(new byte[]{0x0A}); // Line feed

                // Total width or character
                int totWidth = 48;

                //for width separator
                int[] columnWidths0 = {totWidth}; // Adjust as necessary

                // Tabel Detail
                int[] columnWidths1 = {12, 9, 7, 11, 5}; // Adjust as necessary
                int[] alignments1 = {1, 1, 1, 1, 1}; // 0: Left, 1: Center, 2: Right
                String[] headers1 = {"Linen", "Warna", "Ukuran", "Ruangan", "Berat"};
                printSeparatorLine(outputStream, columnWidths0);
                printRow(outputStream, headers1, columnWidths1, alignments1, true, false);
                printSeparatorLine(outputStream, columnWidths0);

                // Print table rows
                String[][] rows = {
                        {"Baju Pasien Gawat Darurat Sekali", "Hijau", "XL", "Ruang A", "0.5"},
                        {"Baju Pasien ", "Hijau", "XL", "Ruang A", "0.5"},
                        {"Baju Pasien ", "Hijau", "XL", "Ruang A", "0.5"},

                };
                for (String[] row : rows) {
                    printRow(outputStream, row, columnWidths1, alignments1, false, false);
                }
                printSeparatorLine(outputStream, columnWidths0);
//            outputStream.write(new byte[]{0x0A}); // Line feed

                // table for total
                int[] colTotWidth = {23, 23}; // Adjust as necessary
                int[] alignments3 = {1, 1}; // 0: Left, 1: Center, 2: Right
                String[] totalItem = {"Total Linen: 3", "Total Berat (Kg): 7.5"};
                printRow(outputStream, totalItem, colTotWidth, alignments3, false, false);

                // Print Date and time
                int[] colDateWidth = {23, 23};
                int[] alignments4 = {1, 1}; // 0: Left, 1: Center, 2: Right
                String[] date = {"", "03-11-2024 15:13"};
                printRow(outputStream, date, colDateWidth, alignments4, false, false);

                // Signature
                int[] columnWidths3 = {23, 23}; // Adjust as necessary
                int[] alignments5 = {1, 1}; // 0: Left, 1: Center, 2: Right
                String[] footer = {"TTD Ruangan", "TTD Laundry"};
                printSeparatorLine(outputStream, columnWidths0);
                printRow(outputStream, footer, columnWidths3, alignments5, false, false);
                printSeparatorLine(outputStream, columnWidths0);
                outputStream.write(new byte[]{0x1B, 0x64, 0x02});

                printSeparatorLine(outputStream, columnWidths0);
                outputStream.write(new byte[]{0x1B, 0x64, 0x01});

//            int[] columnWidths3 = {totWidth/2, totWidth/2}; // Adjust as necessary
//            int[] alignments3 = {1, 1}; // 0: Left, 1: Center, 2: Right
//            String[] footer = {"TTD Ruangan", "TTD Laundry"};
////            outputStream.write(new byte[]{0x1B, 0x61, 0x00});
//            printSeparatorLine(outputStream, columnWidths3);
//            printRow(outputStream, footer, columnWidths3, alignments3, false, false);
//            printSeparatorLine(outputStream, columnWidths3);
//            outputStream.write(new byte[]{0x1B, 0x64, 0x01});

                outputStream.flush();
            }
            if(newLine){
                outputStream.write(new byte[]{0x1B, 0x64, 0x01});
                outputStream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void addNewLine(OutputStream outputStream, String text) {
        try {

            outputStream.write(new byte[]{0x1B, 0x64, 0x01});

            outputStream.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void printRow(OutputStream outputStream, String[] columns, int[] columnWidths, int[] alignments, boolean isHeader, boolean wrapText) throws IOException {
        StringBuilder rowBuilder = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            rowBuilder.append(padRight(columns[i], columnWidths[i]));
            if (i < columns.length - 1) {
                rowBuilder.append(" "); // Add space between columns
            }
        }
        rowBuilder.append("\n"); // New line at the end of the row
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

    private String alignText(String text, int length, int alignment) {
        if (alignment == 1) { // Center
            return centerText(text, length);
        } else if (alignment == 2) { // Right
            return padLeft(text, length);
        } else { // Left (default)
            return padRight(text, length);
        }
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

    private String centerText(String text, int length) {
        if (text.length() >= length) {
            return text.substring(0, length);
        }
        int padding = (length - text.length()) / 2;
        return String.format("%" + padding + "s%s%" + (length - text.length() - padding) + "s", "", text, "");
    }

    private String[] wrapText(String text, int width) {
        List<String> lines = new ArrayList<>();
        int index = 0;
        while (index < text.length()) {
            lines.add(text.substring(index, Math.min(index + width, text.length())));
            index += width;
        }
        return lines.toArray(new String[0]);
    }

    private String truncateText(String text, int width) {
        return text.length() > width ? text.substring(0, width) : text;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        disconnectPrinter();
    }

    private void disconnectPrinter() {
        try {
            if (bluetoothSocket != null) {
                bluetoothSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Try Alternatif 2
    public Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    // Convert Bitmap to ESC/POS Format
    public byte[] convertBitmapToEscPos(Bitmap bitmap) throws IOException {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int[] pixels = new int[width * height];
        bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        outputStream.write(new byte[]{0x1B, 0x40}); // Initialize printer

        for (int y = 0; y < height; y += 24) {
            outputStream.write(new byte[]{0x1B, 0x2A, 33, (byte) (width % 256), (byte) (width / 256)});
            for (int x = 0; x < width; x++) {
                byte[] slice = new byte[3];
                for (int b = 0; b < 3; b++) {
                    for (int i = 0; i < 8; i++) {
                        int yy = y + (b * 8) + i;
                        if (yy >= height) {
                            slice[b] |= 0;
                        } else {
                            int pixel = pixels[(yy * width) + x];
                            int r = (pixel >> 16) & 0xff;
                            int g = (pixel >> 8) & 0xff;
                            int b2 = pixel & 0xff;
                            int luminance = (r + g + b2) / 3;
                            if (luminance < 128) {
                                slice[b] |= (1 << (7 - i));
                            }
                        }
                    }
                }
                outputStream.write(slice);
            }
            outputStream.write(new byte[]{0x0A}); // Line feed
        }

        return outputStream.toByteArray();
    }

    // Print the layout
    public void printLayout(View view) {
        try {
            Bitmap bitmap = getBitmapFromView(view);
            byte[] data = EscPosCommandGenerator.generate(bitmap);
            if (outputStream != null) {
                outputStream.write(data);
                outputStream.flush();
            } else {
                // Handle outputStream being null
                Log.e("PrintLayout", "OutputStream is null!");
            }
        } catch (IOException e) {
            System.out.println(e.toString());
        }
    }

}
