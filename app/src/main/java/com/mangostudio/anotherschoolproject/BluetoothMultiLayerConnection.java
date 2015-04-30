package com.mangostudio.anotherschoolproject;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
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
        this.inputThread = new BluetoothPackageInputThread(inStream, socket.getRemoteDevice());
        inputThread.start();
        /*
            Normalerweise schickt das System Broadcasts mit verbundenen Geräten, diese werden aber in Außnahmefällen
            (z.B. Sockets werden gleich nacheinander geschlossen und geöffnet) nicht gesendet.
            Hier wird ein Broadcast implementiert, der schneller sein soll, weil er gesendet wird, sobald der Socket öffnet.
            Hier wird auf die InterThreadCom-Klasse verzichtet, um den Broadcast gleichzeitig mit den Systemmeldungen behandeln zu können.
         */
        Intent socketClosedIntent = new Intent(NetworkHandler.ACTION_SOCKET_OPENED);
        socketClosedIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, this.socket.getRemoteDevice());
        CardGamesApplication.getContext().sendBroadcast(socketClosedIntent);
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
