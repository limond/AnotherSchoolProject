package com.mangostudio.anotherschoolproject;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.IOException;

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
            case InterThreadCom.BLUETOOTH_CONNECTION_START_REQUEST:
                Bundle data = msg.getData();
                BluetoothDevice device = data.getParcelable("device");
                try {
                    new BluetoothConnection(device);
                } catch (IOException e) {
                    e.printStackTrace();
                    InterThreadCom.updateConnectionStatus(uiHandler, (Context) msg.obj,BluetoothConnection.CONNECTION_FAILED);
                }
                break;
        }
    }
}
