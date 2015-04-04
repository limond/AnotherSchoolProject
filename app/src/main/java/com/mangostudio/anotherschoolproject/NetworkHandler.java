package com.mangostudio.anotherschoolproject;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

/**
 * Created by limond on 06.03.15.
 */
public class NetworkHandler extends Handler {
    //Setze den Messege-Handler für den UI-Thread
    public UIHandler uiHandler = new UIHandler(Looper.getMainLooper());
    public BluetoothManagement bluetooth = new BluetoothManagement();

    public NetworkHandler(Looper l) {
       super(l);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch(msg.what){
            case InterThreadCom.BLUETOOTH_STATUS_REQUEST:
                //Antwortet, ob es einen Bluetooth-Adapter gibt
                InterThreadCom.bluetoothPresentStatus(uiHandler, (Activity) msg.obj, bluetooth.getAdapterStatus());
                break;
            case InterThreadCom.BLUETOOTH_PAIRED_DEVICES_REQUEST:
                //Antwortet, welche Geräte gepaired sind
                InterThreadCom.sendBluetoothDevices(uiHandler, (HostListView) msg.obj, bluetooth.getPairedDevices());
                break;
        }
    }
}
