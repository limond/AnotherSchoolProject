package com.mangostudio.anotherschoolproject;

import android.bluetooth.BluetoothSocket;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by Leon on 21.04.2015.
 */
public class BluetoothMultiLayerConnection {
    private BluetoothSocket socket;
    private ObjectInputStream inStream;
    private ObjectOutputStream outStream;
    private BluetoothPackageInputThread inputThread;

    public BluetoothMultiLayerConnection(BluetoothSocket socket, ObjectInputStream inStream, ObjectOutputStream outStream){
        this.socket = socket;
        this.inStream = inStream;
        this.outStream = outStream;
        this.inputThread = new BluetoothPackageInputThread(inStream, socket.getRemoteDevice().getAddress());
        inputThread.start();
    }

    public void close() throws IOException {
        inStream.close();
        outStream.close();
        socket.close();
    }
}
