package com.mangostudio.anotherschoolproject;

import android.bluetooth.BluetoothDevice;
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

    public BluetoothMultiLayerConnection(BluetoothSocket socket) throws IOException {
        this.socket = socket;
        this.outStream = new ObjectOutputStream(socket.getOutputStream());
        this.inStream = new ObjectInputStream(socket.getInputStream());
        this.inputThread = new BluetoothPackageInputThread(inStream, socket.getRemoteDevice().getAddress());
        inputThread.start();
    }

    public void close() throws IOException {
        inStream.close();
        outStream.close();
        socket.close();
    }

    public ObjectOutputStream getOutStream(){
        return this.outStream;
    }

    public ObjectInputStream getInStream(){
        return this.inStream;
    }

    public BluetoothDevice getRemoteDevice(){
        return socket.getRemoteDevice();
    }
}
