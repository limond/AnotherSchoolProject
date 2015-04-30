package com.mangostudio.anotherschoolproject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.widget.ArrayAdapter;

import java.util.Set;

/**
 * Created by Leon on 03.04.2015.
 */
public class InterThreadCom {
    public static final int BLUETOOTH_CONNECTION_START_REQUEST = 1;
    public static final int BLUETOOTH_CONNECTION_START_RESPONSE = 2;
    public static final int BLUETOOTH_SERVER_START_REQUEST = 3;
    public static final int BLUETOOTH_SERVER_STATUS_RESPONSE = 4;
    public static final int BLUETOOTH_SERVER_RELEASE_SOCKETS_REQUEST = 5;
    public static final int BLUETOOTH_HANDLE_INPUT_PACKAGE = 6;
    public static final int BLUETOOTH_SOCKET_CLOSED = 7;
    public static final int BLUETOOTH_SEND_PACKAGE = 8;
    public static final int BLUETOOTH_SOCKET_OPENED = 9;
    public static final int BLUETOOTH_SERVER_SOCKET_OPENED = 10;
    public static final int BLUETOOTH_SERVER_STOP = 11;
    public static final int BLUETOOTH_SERVER_STOPPED_STATUS = 12;

    public static final int GAME_START = 13;


    //Nachricht an den NetThread, dass eine Verbindung zu einem Gerät aufgebaut werden soll
    public static void connectToDevice(BluetoothDevice selectedDevice) {
        Handler netHandler = CardGamesApplication.getNetworkHandler();
        Message msg = netHandler.obtainMessage();
        msg.what = BLUETOOTH_CONNECTION_START_REQUEST;
        Bundle data = new Bundle();
        //Das Objekt Bluetooth-Gerät wird für den Transport in den anderen Thread "flach gemacht" und kommt in Bündel, das an die Nachricht angefügt wird
        data.putParcelable("device", selectedDevice);
        msg.setData(data);
        netHandler.sendMessage(msg);
    }

    public static void updateConnectionStatus(int connectionStatus, BluetoothDevice device) {
        Handler uiHandler = CardGamesApplication.getUIHandler();
        Message msg = uiHandler.obtainMessage();
        msg.what = BLUETOOTH_CONNECTION_START_RESPONSE;
        Bundle data = new Bundle();
        data.putInt("status", connectionStatus);
        if(device != null) data.putParcelable("device",device);
        msg.setData(data);
        uiHandler.sendMessage(msg);
    }

    public static void startServer(){
        Handler netHandler = CardGamesApplication.getNetworkHandler();
        Message msg = netHandler.obtainMessage();
        msg.what = BLUETOOTH_SERVER_START_REQUEST;
        netHandler.sendMessage(msg);
    }

    public static void releaseAllSockets(){
        Handler netHandler = CardGamesApplication.getNetworkHandler();
        Message msg = netHandler.obtainMessage();
        msg.what = BLUETOOTH_SERVER_RELEASE_SOCKETS_REQUEST;
        netHandler.sendMessage(msg);
    }

    public static void updateServerStatus(int status) {
        Handler uiHandler = CardGamesApplication.getUIHandler();
        Message msg = uiHandler.obtainMessage();
        msg.what = BLUETOOTH_SERVER_STATUS_RESPONSE;
        Bundle data = new Bundle();
        data.putInt("status", status);
        msg.setData(data);
        uiHandler.sendMessage(msg);
    }

    public static void handleInputPackage(BluetoothPackage btPackage, String source){
        Handler handler;
        switch(btPackage.getDestination()){
            case BluetoothPackage.HANDLER_DESTINATION_NETWORK:
                handler = CardGamesApplication.getNetworkHandler();
                break;
            //Fall-Through (nur aus Verständlichkeitsgründen)
            case BluetoothPackage.HANDLER_DESTINATION_UI:
            default:
                handler = CardGamesApplication.getUIHandler();
                break;
        }
        Message msg = handler.obtainMessage();
        msg.what = BLUETOOTH_HANDLE_INPUT_PACKAGE;
        Bundle data = new Bundle();
        data.putSerializable("package", btPackage);
        data.putString("source", source);
        msg.setData(data);
        handler.sendMessage(msg);
    }

    public static void handleServerSocketOpened(BluetoothServerSocket serverSocket) {
        Handler netHandler = CardGamesApplication.getNetworkHandler();
        Message msg = netHandler.obtainMessage();
        msg.what = BLUETOOTH_SERVER_SOCKET_OPENED;
        msg.obj = serverSocket;
        netHandler.sendMessage(msg);
    }

    public static void stopServer() {
        Handler netHandler = CardGamesApplication.getNetworkHandler();
        Message msg = netHandler.obtainMessage();
        msg.what = BLUETOOTH_SERVER_STOP;
        netHandler.sendMessage(msg);
    }

    public static void handleSocketOpened(BluetoothSocket socket) {
        Handler netHandler = CardGamesApplication.getNetworkHandler();
        Message msg = netHandler.obtainMessage();
        msg.what = BLUETOOTH_SOCKET_OPENED;
        msg.obj = socket;
        netHandler.sendMessage(msg);
    }

    public static void handleSocketClosed(String address) {
        Handler netHandler = CardGamesApplication.getNetworkHandler();
        Message msg = netHandler.obtainMessage();
        msg.what = BLUETOOTH_SOCKET_CLOSED;
        Bundle data = new Bundle();
        data.putString("address",address);
        msg.setData(data);
        netHandler.sendMessage(msg);
    }

    /*
        Die folgenden Methoden ermöglichen es aus anderen Threads Pakete an andere geräte zu schicken
     */
    public static void sendPackageBroadcast(BluetoothPackage pkg) {
        //untested
        Handler netHandler = CardGamesApplication.getNetworkHandler();
        Message msg = netHandler.obtainMessage();
        msg.what = BLUETOOTH_SEND_PACKAGE;
        Bundle data = new Bundle();
        data.putBoolean("broadcast", true);
        msg.obj = pkg;
        msg.setData(data);
        netHandler.sendMessage(msg);
    }

    public static void sendPackage(BluetoothPackage pkg, String address) {
        //untested
        Handler netHandler = CardGamesApplication.getNetworkHandler();
        Message msg = netHandler.obtainMessage();
        msg.what = BLUETOOTH_SEND_PACKAGE;
        Bundle data = new Bundle();
        data.putBoolean("broadcast",false);
        data.putString("address", address);
        msg.obj = pkg;
        msg.setData(data);
        netHandler.sendMessage(msg);
    }

    public static void startGame() {
        Handler netHandler = CardGamesApplication.getNetworkHandler();
        Message msg = netHandler.obtainMessage();
        msg.what = GAME_START;
        netHandler.sendMessage(msg);
    }

    public static void stopServerStatus(int code){
        Handler uiHandler = CardGamesApplication.getUIHandler();
        Message msg = uiHandler.obtainMessage();
        msg.what = BLUETOOTH_SERVER_STOPPED_STATUS;
        Bundle data = new Bundle();
        data.putInt("status", code);
        msg.setData(data);
        uiHandler.sendMessage(msg);
    }
}
