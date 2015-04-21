package com.mangostudio.anotherschoolproject;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by limond on 06.03.15.
 */
public class NetworkHandler extends Handler {
    public BluetoothManagement bluetooth = new BluetoothManagement();
    private Map<String, BluetoothMultiLayerConnection> connections = new HashMap<>();
    public NetworkHandler(Looper l) {
       super(l);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch(msg.what){
            case InterThreadCom.BLUETOOTH_CONNECTION_START_REQUEST:
                connectionStart(msg);
                break;
            case InterThreadCom.BLUETOOTH_SERVER_START_REQUEST:
                startServer();
                break;
            case InterThreadCom.BLUETOOTH_SERVER_RELEASE_SOCKETS_REQUEST:
                releaseConnections();
                break;
            case InterThreadCom.BLUETOOTH_HANDLE_INPUT_PACKAGE:
                handleInputPackage(msg);
                break;
        }
    }

    //Baut einen Socket zum Host auf
    private void connectionStart(Message msg){
        Bundle data = msg.getData();
        BluetoothDevice device = data.getParcelable("device");
        try {
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString(BluetoothManagement.UUID));
            socket.connect();
            ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
            BluetoothMultiLayerConnection connection = new BluetoothMultiLayerConnection(socket,inStream,outStream);
            connections.put(device.getAddress(),connection);
            Thread.sleep(2000,0);
            outStream.writeObject(new BluetoothPackage(42));//Das ist bisher nur ein Test
            InterThreadCom.updateConnectionStatus(BluetoothManagement.CONNECTION_SUCCESSFULL, device);
        } catch (IOException e) {
            e.printStackTrace();
            InterThreadCom.updateConnectionStatus(BluetoothManagement.CONNECTION_FAILED, null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Erstellt einen ServerSocket, um Verbindungen annehmen zu können
    private void startServer(){
        BluetoothServerSocket serverSocket;
        try {
            serverSocket = bluetooth.startServer();
        } catch (IOException e) {
            e.printStackTrace();
            InterThreadCom.updateServerStatus(BluetoothManagement.SERVER_CREATION_FAILED, null);
            return;
        }
        InterThreadCom.updateServerStatus(BluetoothManagement.SERVER_CREATION_SUCCESSFULL, serverSocket);
        try {
            while (true) {
                        /*
                            Dieser Vorgang wird abgebrochen, indem der UI-Thread den serverSocket schließt und accept() eine Exception wirft.
                            Dieses Vorgehen ist üblich, da accept nicht auf interrupts reagiert
                         */
                BluetoothSocket socket = serverSocket.accept();
                ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
                ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
                BluetoothMultiLayerConnection connection = new BluetoothMultiLayerConnection(socket,inStream,outStream);
                connections.put(socket.getRemoteDevice().getAddress(),connection);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Gibt alle Verbindungen frei, die bisher verbunden und noch nicht freigegeben wurden
    private void releaseConnections() {
        for(BluetoothMultiLayerConnection connection : connections.values()){
            try {
                connection.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        connections.clear();
    }

    private void handleInputPackage(Message msg) {
        Log.d("Test", "Test1");
    }
}
