package com.mangostudio.anotherschoolproject;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import java.util.Arrays;

/**
 * Created by Leon on 03.04.2015.
 */
public class UIHandler extends Handler {
    public UIHandler(Looper l) {
        super(l);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        switch(msg.what){
            case InterThreadCom.BLUETOOTH_PRESENT_RESPONSE:
                handleBluetoothPresent(msg);
                break;
            case InterThreadCom.BLUETOOTH_PAIRED_DEVICES_RESPONSE:
                handleBluetoothDevices(msg);
                break;
        }
    }

    private void handleBluetoothPresent(Message msg){
        if(!msg.getData().getBoolean("present")){
            Activity ctx = (Activity) msg.obj;
            Toast.makeText(ctx,R.string.NoBluetoothWarning,Toast.LENGTH_LONG).show();
        }
    }

    private void handleBluetoothDevices(Message msg){
        Parcelable[] parcelables = msg.getData().getParcelableArray("bluetoothDevices");
        BluetoothDevice[] bluetoothDevices = Arrays.copyOf(parcelables, parcelables.length, BluetoothDevice[].class);
        HostListView list = (HostListView) msg.obj;
        list.setDevices(bluetoothDevices);
        Log.d("size",""+bluetoothDevices.length);
    }
}