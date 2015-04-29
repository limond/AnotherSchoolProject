package com.mangostudio.anotherschoolproject;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
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

    public static final String ACTION_SOCKET_CLOSED = "com.mangostudio.anotherschoolproject.socketClosed";

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
            case InterThreadCom.BLUETOOTH_SOCKET_CLOSED:
                handleSocketClosed(msg);
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
            BluetoothMultiLayerConnection connection = new BluetoothMultiLayerConnection(socket);
            connections.put(device.getAddress(), connection);

            //Das ist bisher nur ein Test [ACTION_START_CLIENT_GAME wird später an einer ganz anderen Stelle eigentlich geschickt]
            BluetoothPackage pkg = new BluetoothPackage(BluetoothPackage.HANDLER_DESTINATION_UI, BluetoothPackage.ACTION_START_CLIENT_GAME);
            HashMap<String,Object> pkgData = new HashMap<>();
            pkgData.put("test","lol");
            pkg.setAdditionalData(pkgData);
            connection.getOutStream().writeObject(pkg);

            InterThreadCom.updateConnectionStatus(BluetoothManagement.CONNECTION_SUCCESSFULL, device);
        } catch (IOException e) {
            e.printStackTrace();
            InterThreadCom.updateConnectionStatus(BluetoothManagement.CONNECTION_FAILED, null);
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
                BluetoothMultiLayerConnection connection = new BluetoothMultiLayerConnection(socket);
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

    /*
        Die Funktion behandelt Nachrichten der Verbindungspartner
        ACHTUNG: Solange der Server neue Verbindungen akzeptiert kann der Handler keine Messages aus der MessageQueue verarbeiten
        Nachrichten können in dieser Zeit stattdessen an den UiHandler geschickt werden
     */
    private void handleInputPackage(Message msg) {
        Log.d("NetThread", msg.toString());
    }

    private void handleSocketClosed(Message msg) {
        Bundle data = msg.getData();
        String address = data.getString("address");
        BluetoothMultiLayerConnection connection =  connections.get(address);
        if(connection == null) return;
        BluetoothDevice remoteDevice = connection.getRemoteDevice();
        connections.remove(address);
        /*
            Normalerweise schickt das System nach ca. 10 Sekunden Trennung einen Broadcast, um getrennte Geräte mitzuteilen.
            Hier wird ein Broadcast implementiert, der schneller sein soll, weil er gesendet wird, sobald der Socket schließt.
            Hier wird auf die InterThreadCom-Klasse verzichtet, um den Broadcast gleichzeitig mit den Systemmeldungen behandeln zu können.
         */
        Intent socketClosedIntent = new Intent(ACTION_SOCKET_CLOSED);
        socketClosedIntent.putExtra(BluetoothDevice.EXTRA_DEVICE, remoteDevice);
        CardGamesApplication.getContext().sendBroadcast(socketClosedIntent);
    }
}
