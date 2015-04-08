package com.mangostudio.anotherschoolproject;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.util.UUID;

/**
 * Created by Leon on 08.04.2015.
 */
public class BluetoothConnection {
    public static final int CONNECTION_FAILED = 1;
    public BluetoothSocket socket;

    public BluetoothConnection(BluetoothDevice device) throws IOException {
        socket = device.createRfcommSocketToServiceRecord(UUID.fromString(BluetoothManagement.UUID));
        socket.connect();
    }
}
