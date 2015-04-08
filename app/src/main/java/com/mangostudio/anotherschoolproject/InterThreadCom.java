package com.mangostudio.anotherschoolproject;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import java.util.Set;

/**
 * Created by Leon on 03.04.2015.
 */
public class InterThreadCom {
    public final static int BLUETOOTH_CONNECTION_START_REQUEST = 1;
    public final static int BLUETOOTH_CONNECTION_START_RESPONSE = 2;

    //Nachricht an den NetThread, dass eine Verbindung zu einem Ger�t aufgebaut werden soll
    public static void connectToDevice(Handler netHandler,Context context, BluetoothDevice selectedDevice) {
        Message msg = netHandler.obtainMessage();
        msg.what = BLUETOOTH_CONNECTION_START_REQUEST;
        msg.obj = context;
        Bundle data = new Bundle();
        //Das Objekt Bluetooth-Ger�t wird f�r den Transport in den anderen Thread "flach gemacht" und kommt in B�ndel, das an die Nachricht angef�gt wird
        data.putParcelable("device",selectedDevice);
        msg.setData(data);
        netHandler.sendMessage(msg);
    }

    public static void updateConnectionStatus(Handler uiHandler, Context context, int connectionStatus) {
        Message msg = uiHandler.obtainMessage();
        msg.what = BLUETOOTH_CONNECTION_START_RESPONSE;
        msg.obj = context;
        // Hier k�nnte man auch einfach "msg.arg1" benutzen. F�r sp�tere Erweiterbarkeit wird dennoch ein Bundle benutzt
        Bundle data = new Bundle();
        data.putInt("status",connectionStatus);
        msg.setData(data);
        uiHandler.sendMessage(msg);
    }
}
