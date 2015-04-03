package com.mangostudio.anotherschoolproject;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.Set;

/**
 * Created by limond on 06.03.15.
 */
public class NetworkHandler extends Handler {
    public UIHandler uiHandler = new UIHandler(Looper.getMainLooper());
    public BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
    public Set<BluetoothDevice> pairedDevices;

    public NetworkHandler(Looper l) {
       super(l);
    }
    //Setze den Messege-Handler für den UI-Thread


    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch(msg.what){
            case InterThreadCom.BLUETOOTH_PRESENT_REQUEST:
                //Antwortet, ob es einen Bluetooth-Adapter gibt
                InterThreadCom.bluetoothPresentStatus(uiHandler, (Activity) msg.obj, adapter != null);
                break;
            case InterThreadCom.BLUETOOTH_PAIRED_DEVICES_REQUEST:
                this.pairedDevices = adapter.getBondedDevices();
                InterThreadCom.sendBluetoothDevices(uiHandler, (HostListView) msg.obj, this.pairedDevices);
                break;
        }
    }
}
