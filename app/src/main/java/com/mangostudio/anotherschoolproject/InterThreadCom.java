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
    public final static int BLUETOOTH_CONNECTION_START_REQUEST = 1;
    public final static int BLUETOOTH_CONNECTION_START_RESPONSE = 2;
    public final static int BLUETOOTH_SERVER_START_REQUEST = 3;
    public final static int BLUETOOTH_SERVER_STATUS_RESPONSE = 4;

    //Nachricht an den NetThread, dass eine Verbindung zu einem Gerät aufgebaut werden soll
    public static void connectToDevice(Handler netHandler,Context context, BluetoothDevice selectedDevice) {
        Message msg = netHandler.obtainMessage();
        msg.what = BLUETOOTH_CONNECTION_START_REQUEST;
        msg.obj = context;
        Bundle data = new Bundle();
        //Das Objekt Bluetooth-Gerät wird für den Transport in den anderen Thread "flach gemacht" und kommt in Bündel, das an die Nachricht angefügt wird
        data.putParcelable("device",selectedDevice);
        msg.setData(data);
        netHandler.sendMessage(msg);
    }

    public static void updateConnectionStatus(Handler uiHandler, Context context, int connectionStatus) {
        Message msg = uiHandler.obtainMessage();
        msg.what = BLUETOOTH_CONNECTION_START_RESPONSE;
        msg.obj = context;
        // Hier könnte man auch einfach "msg.arg1" benutzen. Für spätere Erweiterbarkeit wird dennoch ein Bundle benutzt
        Bundle data = new Bundle();
        data.putInt("status", connectionStatus);
        msg.setData(data);
        uiHandler.sendMessage(msg);
    }

    public static void startServer(Handler netHandler){
        Message msg = netHandler.obtainMessage();
        msg.what = BLUETOOTH_SERVER_START_REQUEST;
        netHandler.sendMessage(msg);
    }

    public static void updateServerStatus(UIHandler uiHandler, int status, BluetoothServerSocket serverSocket) {
        Message msg = uiHandler.obtainMessage();
        msg.what = BLUETOOTH_SERVER_STATUS_RESPONSE;
        Bundle data = new Bundle();
        data.putInt("status", status);
        msg.setData(data);
        msg.obj = serverSocket;
        uiHandler.sendMessage(msg);
    }
}
