package com.android.management_linen;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothPrinter extends ActivityCompat {
    private static final String TAG = "BluetoothPrinter";
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothSocket bluetoothSocket;
    private OutputStream outputStream;

    public BluetoothPrinter() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }

    @SuppressLint("MissingPermission")
    public void connect(String printerMACAddress) throws IOException {
        BluetoothDevice device = bluetoothAdapter.getRemoteDevice(printerMACAddress);

        bluetoothSocket = device.createRfcommSocketToServiceRecord(UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
        bluetoothSocket.connect();
        outputStream = bluetoothSocket.getOutputStream();
    }

    public void disconnect() throws IOException {
        if (outputStream != null) {
            outputStream.close();
        }
        if (bluetoothSocket != null) {
            bluetoothSocket.close();
        }
    }

    public void print(byte[] data) throws IOException {
        if (outputStream != null) {
            outputStream.write(data);
            outputStream.flush();
        }
    }
}
