package com.mangostudio.anotherschoolproject;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

/**
 * Created by limond on 06.03.15.
 */
public class NetworkHandler extends Handler {
    public BluetoothManagement bluetooth = new BluetoothManagement();
    private ArrayList<BluetoothSocket> sockets = new ArrayList<>();

    public NetworkHandler(Looper l) {
       super(l);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch(msg.what){
            case InterThreadCom.BLUETOOTH_CONNECTION_START_REQUEST:
                Bundle data = msg.getData();
                BluetoothDevice device = data.getParcelable("device");
                try {
                    BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString(BluetoothManagement.UUID));
                    socket.connect();
                    sockets.add(socket);
                } catch (IOException e) {
                    e.printStackTrace();
                    InterThreadCom.updateConnectionStatus(BluetoothManagement.CONNECTION_FAILED);
                }
                break;
            case InterThreadCom.BLUETOOTH_SERVER_START_REQUEST:
                BluetoothServerSocket serverSocket;
                try {
                    serverSocket = bluetooth.startServer();
                } catch (IOException e) {
                    e.printStackTrace();
                    InterThreadCom.updateServerStatus(BluetoothManagement.SERVER_CREATION_FAILED, null);
                    break;
                }
                InterThreadCom.updateServerStatus(BluetoothManagement.SERVER_CREATION_SUCCESSFULL, serverSocket);
                try {
                    while (true) {
                        /*
                            Dieser Vorgang wird abgebrochen, indem der UI-Thread den serverSocket schließt und accept() eine Exception wirft.
                            Dieses Vorgehen ist üblich, da accept nicht auf interrupts reagiert
                         */
                        sockets.add(serverSocket.accept());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case InterThreadCom.BLUETOOTH_SERVER_RELEASE_SOCKETS_REQUEST:
                for(BluetoothSocket socket : sockets){
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                sockets.clear();
                break;
        }
    }
}
