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
    public static final int SEND_MSG_TO_UI = 7;

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

    public static void updateServerStatus(int status, BluetoothServerSocket serverSocket) {
        Handler uiHandler = CardGamesApplication.getUIHandler();
        Message msg = uiHandler.obtainMessage();
        msg.what = BLUETOOTH_SERVER_STATUS_RESPONSE;
        Bundle data = new Bundle();
        data.putInt("status", status);
        msg.setData(data);
        msg.obj = serverSocket;
        uiHandler.sendMessage(msg);
    }

    public static void handleInputPackage(BluetoothPackage btPackage, String source){
        Handler netHandler = CardGamesApplication.getNetworkHandler();
        Message msg = netHandler.obtainMessage();
        msg.what = BLUETOOTH_HANDLE_INPUT_PACKAGE;
        Bundle data = new Bundle();
        data.putSerializable("package", btPackage);
        data.putString("source", source);
        msg.setData(data);
        netHandler.sendMessage(msg);
    }

    public static void sendPackageToUi(Message msg) {
        msg.what = SEND_MSG_TO_UI;
        Handler uiHandler = CardGamesApplication.getUIHandler();
        uiHandler.sendMessage(msg);
    }
}
