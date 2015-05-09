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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by limond on 06.03.15.
 */
public class NetworkHandler extends Handler {

    public static final String ACTION_SOCKET_CLOSED = "com.mangostudio.anotherschoolproject.socketClosed";
    public static final String ACTION_SOCKET_OPENED = "com.mangostudio.anotherschoolproject.socketOpened";

    public BluetoothManagement bluetooth = new BluetoothManagement();
    private Map<String, BluetoothMultiLayerConnection> connections = new HashMap<>();
    private BluetoothServerSocket currentServerSocket;

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
            case InterThreadCom.BLUETOOTH_SERVER_SOCKET_OPENED:
                handleBluetoothServerSocketOpened(msg);
                break;
            case InterThreadCom.BLUETOOTH_SERVER_STOP:
                handleStopServer();
                break;
            case InterThreadCom.BLUETOOTH_SOCKET_OPENED:
                handleBluetoothSocketOpened(msg);
                break;
            case InterThreadCom.BLUETOOTH_SOCKET_CLOSED:
                handleSocketClosed(msg);
                break;
            case InterThreadCom.BLUETOOTH_SEND_PACKAGE:
                handleSendPackage(msg);
                break;
            case InterThreadCom.GAME_START:
                handleGameStart();
                break;
        }
    }

    //Baut einen Verbindung zum Host auf
    private void connectionStart(Message msg){
        Bundle data = msg.getData();
        BluetoothDevice device = data.getParcelable("device");
        try {
            BluetoothSocket socket = device.createRfcommSocketToServiceRecord(UUID.fromString(BluetoothManagement.UUID));
            socket.connect();
            BluetoothMultiLayerConnection connection = new BluetoothMultiLayerConnection(socket);
            connections.put(device.getAddress(), connection);
            InterThreadCom.updateConnectionStatus(BluetoothManagement.CONNECTION_SUCCESSFULL, device);
        } catch (IOException e) {
            e.printStackTrace();
            InterThreadCom.updateConnectionStatus(BluetoothManagement.CONNECTION_FAILED, null);
        }
    }

    //Erstellt einen BluetoothServerSocketThread, um Verbindungen annehmen zu können
    private void startServer(){
        BluetoothServerSocketThread serverSocketThread = new BluetoothServerSocketThread();
        serverSocketThread.start();
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
    }

    /*
        Die Funktion behandelt Nachrichten der Verbindungspartner
        Bisher werden Nachrichten nur geloggt.
     */
    private void handleInputPackage(Message msg) {
        Log.d("NetThread", msg.toString());
    }

    private void handleBluetoothServerSocketOpened(Message msg) {
        currentServerSocket = (BluetoothServerSocket) msg.obj;
    }

    //Stoppt den BluetooothServerSocketThread indirekt, indem der BluetoothServerSocket geschlossen wird, was die while-Schleife im Thread unterbricht
    private void handleStopServer() {
        if(currentServerSocket != null) try {
            currentServerSocket.close();
            InterThreadCom.stopServerStatus(R.string.ServerStopSuccessfully);
        } catch (IOException e) {
            InterThreadCom.stopServerStatus(R.string.ServerStopFailed);
            e.printStackTrace();
        }
    }

    //Füge eine neue Verbindung zur Liste hinzu
    private void handleBluetoothSocketOpened(Message msg) {
        BluetoothSocket socket = (BluetoothSocket) msg.obj;
        try {
            BluetoothMultiLayerConnection connection = new BluetoothMultiLayerConnection(socket);
            connections.put(socket.getRemoteDevice().getAddress(),connection);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Entferne eine Verbindung aus der Liste
    private void handleSocketClosed(Message msg) {
        Bundle data = msg.getData();
        String address = data.getString("address");
        BluetoothMultiLayerConnection connection =  connections.get(address);
        if(connection == null) return;
        connections.remove(address);
}

    //Behandelt ausgehende Bluetooth-Pakete aus anderen Threads
    private void handleSendPackage(Message msg) {
        Bundle data = msg.getData();
        if(data.getBoolean("broadcast")){
            sendPackageBroadcast((BluetoothPackage) msg.obj);
        }
        else{
            sendPackage((BluetoothPackage) msg.obj, data.getString("address"));
        }
    }

    //Sendet ein Paket, nachdem die Adresse "aufgelöst" wurde
    private void sendPackage(BluetoothPackage pkg, String address){
        BluetoothMultiLayerConnection connection = connections.get(address);
        if (connection != null) sendPackage(pkg, connection);
    }

    //Sendet ein Paket an eine Verbindung
    private void sendPackage(BluetoothPackage pkg, BluetoothMultiLayerConnection connection){
        try {
            connection.getOutStream().writeObject(pkg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Sendet ein Paket an alle Verbindungen
    private void sendPackageBroadcast(BluetoothPackage pkg){
        for(BluetoothMultiLayerConnection connection : connections.values()){
            sendPackage(pkg, connection);
        }
    }

    /*
        Startet das Spiel, indem ein entsprechender Broadcast gemacht wird.
        Spielteilnehmer werden an die Spieler geschickt / bekanntgemacht
     */
    private void handleGameStart() {
        BluetoothPackage startPackage = new BluetoothPackage(BluetoothPackage.HANDLER_DESTINATION_UI, BluetoothPackage.ACTION_START_CLIENT_GAME);
        HashMap<String, Object> mapData = new HashMap<>();
        ArrayList<BluetoothDeviceRepresentation> deviceList = new ArrayList<>();
        for(BluetoothMultiLayerConnection connection : connections.values()){
            BluetoothDevice device = connection.getRemoteDevice();
            BluetoothDeviceRepresentation deviceRepresentation = new BluetoothDeviceRepresentation(device.getName(), device.getAddress());
            deviceList.add(deviceRepresentation);
        }
        mapData.put("clientList",deviceList);
        mapData.put("host",new BluetoothDeviceRepresentation(bluetooth.getName(),bluetooth.getAddress()));
        startPackage.setAdditionalData(mapData);
        sendPackageBroadcast(startPackage);
    }
}
